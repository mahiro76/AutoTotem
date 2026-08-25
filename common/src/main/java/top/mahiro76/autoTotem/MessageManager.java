package top.mahiro76.autoTotem;

import com.destroystokyo.paper.ClientOption;

import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 多语言消息管理器。
 * <p>
 * 语言文件位于插件目录的 {@code lang/} 子目录，使用 Minecraft 标准的 {@code .lang} 格式，
 * 文件名为语言代码，例如 {@code en-US.lang}、{@code zh-CN.lang}。
 * <p>
 * 文件格式：
 * <ul>
 *   <li>每行一条 {@code key=value}</li>
 *   <li>{@code #} 开头的行为注释</li>
 *   <li>占位符：{@code %s}（按顺序）或 {@code %1$s}、{@code %2$s}（按位置）</li>
 * </ul>
 * <p>
 * 发送消息时根据玩家客户端语言（{@code Player#getLocale()}）自动选择语言，
 * 找不到对应语言时按以下顺序回退：
 * <ol>
 *   <li>config.yml 中的 {@code default-locale}</li>
 *   <li>内置英文 en-US</li>
 * </ol>
 */
public final class MessageManager {

    /** 内置语言文件清单（首次启动时复制到插件目录） */
    private static final String[] BUILTIN_LOCALES = {"en-US", "zh-CN"};

    /** 默认语言 */
    private static final String FALLBACK_LOCALE = "en-us";

    /** 匹配占位符：%s 或 %1$s、%2$s 等 */
    private static final Pattern PLACEHOLDER = Pattern.compile("%(\\d+\\$)?s");

    private final AutoTotem plugin;
    /** 规范化语言代码（如 zh-cn） -> 该语言的消息映射（key -> 文本） */
    private final Map<String, Map<String, String>> locales = new HashMap<>();

    /**
     * 构造消息管理器并加载全部语言文件。
     *
     * @param plugin 主插件实例
     */
    public MessageManager(AutoTotem plugin) {
        this.plugin = plugin;
        reload();
    }

    /**
     * 加载（或重载）全部语言文件。
     * <p>首次启动会把内置的 en-US、zh-CN 语言文件复制到插件目录的 lang/ 文件夹，
     * 之后加载 lang/ 目录下所有 .lang 文件作为可用语言（服主可自行增删）。
     */
    public void reload() {
        locales.clear();
        File langDir = new File(plugin.getDataFolder(), "lang");

        // 首次启动：复制内置语言文件
        for (String locale : BUILTIN_LOCALES) {
            if (!new File(langDir, locale + ".lang").exists()) {
                plugin.saveResource("lang/" + locale + ".lang", false);
            }
        }

        // 加载 lang/ 目录下全部 .lang 文件
        File[] files = langDir.listFiles((dir, name) -> name.endsWith(".lang"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                String locale = normalize(name.substring(0, name.length() - 5)); // 去掉 ".lang" 后缀
                locales.put(locale, loadLangFile(file));
            }
        }
    }

    /**
     * 解析单个 .lang 文件。
     * <p>每行 {@code key=value}，跳过空行与 {@code #} 注释行。
     *
     * @param file 语言文件
     * @return 键 -> 文本 的映射；文件读取失败时返回空映射
     */
    private Map<String, String> loadLangFile(File file) {
        Map<String, String> messages = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1);
                if (!key.isEmpty()) {
                    messages.put(key, value);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Unable To Load Language File" + file.getName() + " - " + e.getMessage());
        }
        return messages;
    }

    /**
     * 使用默认语言获取消息（用于控制台日志等没有玩家上下文的场景）。
     *
     * @param key          语言文件中的键
     * @param placeholders 依次替换占位符的参数
     * @return 格式化后的消息；键不存在时返回键名本身
     */
    public String get(String key, String... placeholders) {
        return format(resolve(plugin.getConfig().getString("default-locale", "en-US")), key, placeholders);
    }

    /**
     * 根据玩家客户端语言获取消息。
     *
     * @param player       目标玩家
     * @param key          语言文件中的键
     * @param placeholders 依次替换占位符的参数
     * @return 格式化后的消息；键不存在时返回键名本身
     */
    public String getFor(Player player, String key, String... placeholders) {
        return format(resolve(player.getClientOption(ClientOption.LOCALE)), key, placeholders);
    }

    /**
     * 根据玩家客户端语言向玩家发送消息。
     *
     * @param player       目标玩家
     * @param key          语言文件中的键
     * @param placeholders 依次替换占位符的参数
     */
    public void send(Player player, String key, String... placeholders) {
        player.sendMessage(getFor(player, key, placeholders));
    }

    /**
     * 按回退链解析语言配置：
     * 玩家语言 -> 默认语言（default-locale）-> en-US。
     *
     * @param playerLocale 玩家客户端语言代码，如 zh_cn、en_us
     * @return 匹配到的语言消息映射；都未命中时返回 null
     */
    private Map<String, String> resolve(String playerLocale) {
        Map<String, String> messages = locales.get(normalize(playerLocale));
        if (messages != null) return messages;
        messages = locales.get(normalize(plugin.getConfig().getString("default-locale", "en-US")));
        if (messages != null) return messages;
        return locales.get(FALLBACK_LOCALE);
    }

    /**
     * 从语言映射中取出消息并替换占位符。
     *
     * @param messages     语言消息映射，可能为 null
     * @param key          消息键
     * @param placeholders 替换占位符的参数
     * @return 格式化后的消息；映射或键不存在时返回键名本身
     */
    private String format(Map<String, String> messages, String key, String... placeholders) {
        if (messages == null) return key;
        String message = messages.get(key);
        if (message == null) return key;
        return replacePlaceholders(message, placeholders);
    }

    /**
     * 替换消息中的占位符。
     * <p>支持 {@code %s}（按出现顺序取参数）与 {@code %1$s}、{@code %2$s}（按位置取参数）；
     * 参数不足时对应位置留空。
     *
     * @param message      原始消息文本
     * @param placeholders 替换占位符的参数
     * @return 替换完成后的文本
     */
    private String replacePlaceholders(String message, String... placeholders) {
        Matcher matcher = PLACEHOLDER.matcher(message);
        StringBuffer sb = new StringBuffer();
        int sequential = 0;
        while (matcher.find()) {
            int index;
            String indexGroup = matcher.group(1);
            if (indexGroup != null) {
                // "%1$s" -> 去掉 "$" 后解析数字，再转为 0 基下标
                index = Integer.parseInt(indexGroup.substring(0, indexGroup.length() - 1)) - 1;
            } else {
                // "%s" 按顺序
                index = sequential++;
            }
            String replacement = (index >= 0 && index < placeholders.length) ? placeholders[index] : "";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 规范化语言代码：转小写、下划线转连字符。
     * <p>例如 {@code zh_cn} -> {@code zh-cn}，{@code en-US} -> {@code en-us}，
     * 以便与 lang/ 目录下的文件名匹配。
     *
     * @param locale 原始语言代码，可能为 null
     * @return 规范化后的语言代码
     */
    private String normalize(String locale) {
        if (locale == null) return FALLBACK_LOCALE;
        return locale.toLowerCase().replace('_', '-');
    }
}
