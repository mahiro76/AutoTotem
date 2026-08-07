package top.mahiro76.autoTotem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * /autototem 命令的执行器与 Tab 补全实现。
 * <p>
 * 子命令：
 * <ul>
 *   <li>{@code enabled true|false} — 开启/关闭自动图腾</li>
 *   <li>{@code cooldowntime [tick]} — 查看/设置触发冷却时间</li>
 *   <li>{@code reload} — 重新加载配置文件</li>
 *   <li>{@code help} — 显示帮助</li>
 * </ul>
 */
public final class AutoTotemCommand implements CommandExecutor, TabCompleter {

    /** 主插件实例，用于读写玩家的开关与冷却设置 */
    private final AutoTotem plugin;

    /**
     * 构造命令处理器。
     *
     * @param plugin 主插件实例
     */
    public AutoTotemCommand(AutoTotem plugin) {
        this.plugin = plugin;
    }

    /**
     * 命令入口，根据第一个参数分发到对应的子命令处理逻辑。
     *
     * @param sender  命令发送者
     * @param command 被执行的命令对象
     * @param label   命令的别名
     * @param args    命令参数数组
     * @return 始终返回 true（不向玩家显示用法错误）
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 只允许玩家执行本命令
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessageManager().get("command-only-player"));
            return true;
        }
        // 无参数时显示帮助
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        // 按第一个参数分发到对应的子命令
        switch (args[0].toLowerCase()) {
            case "enabled" -> handleEnabled(player, args);
            case "cooldowntime", "cd" -> handleCooldown(player, args);
            case "setdefaultcooldown", "sdc" -> handleSetDefaultCooldown(player, args);
            case "reload" -> {
                if (!player.hasPermission("autototem.reload")) {
                    plugin.getMessageManager().send(player, "reload-no-permission");
                    return true;
                }
                handleReload(player);
            }
            default -> sendHelp(player);
        }
        return true;
    }

    /**
     * 处理 {@code enabled} 子命令：查看或设置自动图腾开关。
     * <p>无参数时查询当前状态；带 {@code true/false} 参数时设置开关。
     *
     * @param player 执行命令的玩家
     * @param args   完整命令参数
     */
    private void handleEnabled(Player player, String[] args) {
        // 无参数：查询当前状态
        if (args.length < 2) {
            String status = plugin.isEnabled(player)
                    ? plugin.getMessageManager().getFor(player, "enabled-status-on")
                    : plugin.getMessageManager().getFor(player, "enabled-status-off");
            plugin.getMessageManager().send(player, "enabled-query", status);
            plugin.getMessageManager().send(player, "enabled-usage");
            return;
        }
        // 有参数：设置开关
        switch (args[1].toLowerCase()) {
            case "true" -> {
                plugin.setEnabled(player, true);
                plugin.getMessageManager().send(player, "enabled-true");
            }
            case "false" -> {
                plugin.setEnabled(player, false);
                plugin.getMessageManager().send(player, "enabled-false");
            }
            default -> plugin.getMessageManager().send(player, "enabled-invalid");
        }
    }

    /**
     * 处理 {@code cooldowntime} 子命令：查看或设置触发冷却时间。
     * <p>无参数时显示当前冷却；带数字参数时设置冷却（tick，20 tick = 1 秒）。
     *
     * @param player 执行命令的玩家
     * @param args   完整命令参数
     */
    private void handleCooldown(Player player, String[] args) {
        // 无参数：显示当前冷却时间
        if (args.length < 2) {
            int current = plugin.getCooldownTicks(player);
            plugin.getMessageManager().send(player, "cooldown-query",
                    String.valueOf(current), formatSeconds(current));
            return;
        }
        // 有参数：解析并设置冷却时间
        try {
            int ticks = Integer.parseInt(args[1]);
            if (ticks < 0) {
                plugin.getMessageManager().send(player, "cooldown-negative");
                return;
            }
            // 玩家设置的冷却时间不能低于服务器默认值
            int minTicks = plugin.getDefaultCooldownTicks();
            if (ticks < minTicks) {
                plugin.getMessageManager().send(player, "cooldown-below-default",
                        String.valueOf(minTicks), formatSeconds(minTicks));
                return;
            }
            plugin.setCooldownTicks(player, ticks);
            plugin.getMessageManager().send(player, "cooldown-set",
                    String.valueOf(ticks), formatSeconds(ticks));
        } catch (NumberFormatException e) {
            // 参数不是整数时给出提示
            plugin.getMessageManager().send(player, "cooldown-invalid");
        }
    }

    /**
     * 处理 {@code setdefaultcooldown} 子命令：查看/设置服务器默认冷却时间。
     * <p>需要 {@code autototem.admin} 权限（默认仅 OP）。无参数时显示当前默认冷却；
     * 带数字参数时设置默认冷却并写入 config.yml。
     *
     * @param player 执行命令的玩家
     * @param args   完整命令参数
     */
    private void handleSetDefaultCooldown(Player player, String[] args) {
        // 仅管理员可用
        if (!player.hasPermission("autototem.admin")) {
            plugin.getMessageManager().send(player, "reload-no-permission");
            return;
        }
        // 无参数：显示当前服务器默认冷却
        if (args.length < 2) {
            int current = plugin.getDefaultCooldownTicks();
            plugin.getMessageManager().send(player, "default-cooldown-query",
                    String.valueOf(current), formatSeconds(current));
            return;
        }
        // 有参数：解析并设置默认冷却时间
        try {
            int ticks = Integer.parseInt(args[1]);
            if (ticks < 0) {
                plugin.getMessageManager().send(player, "cooldown-negative");
                return;
            }
            plugin.setDefaultCooldownTicks(ticks);
            plugin.getMessageManager().send(player, "default-cooldown-set",
                    String.valueOf(ticks), formatSeconds(ticks));
        } catch (NumberFormatException e) {
            plugin.getMessageManager().send(player, "cooldown-invalid");
        }
    }

    /**
     * 处理 {@code reload} 子命令：重新加载 config.yml 与 messages.yml。
     *
     * @param player 执行命令的玩家
     */
    private void handleReload(Player player) {
        plugin.loadConfig();
        plugin.getMessageManager().reload();
        plugin.getMessageManager().send(player, "reload-success");
    }

    /**
     * 向玩家显示命令帮助信息。
     *
     * @param player 执行命令的玩家
     */
    private void sendHelp(Player player) {
        MessageManager messages = plugin.getMessageManager();
        messages.send(player, "help-header");
        messages.send(player, "help-enabled");
        messages.send(player, "help-cooldown");
        messages.send(player, "help-help");
        if (player.hasPermission("autototem.admin")){
            messages.send(player, "help-setdefault");
            messages.send(player, "help-reload");
        }
    }

    /**
     * 将 tick 数转换为便于阅读的秒数文本。
     * <p>整秒时输出整数，否则保留一位小数。
     *
     * @param ticks tick 数
     * @return 秒数文本，如 "2" 或 "1.5"
     */
    private String formatSeconds(int ticks) {
        if (ticks % 20 == 0) {
            return String.valueOf(ticks / 20);
        }
        return String.format("%.1f", ticks / 20.0);
    }

    /**
     * Tab 补全：根据当前已输入的参数提供候选补全项。
     * <ul>
     *   <li>第一个参数：子命令名</li>
     *   <li>第二个参数：{@code enabled} 给出 true/false；{@code cooldowntime} 给出 tick 占位提示</li>
     * </ul>
     *
     * @param sender  命令发送者
     * @param command 被执行的命令对象
     * @param alias   命令的别名
     * @param args    已输入的命令参数
     * @return 候选补全列表；非玩家发送者返回空列表
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        // 控制台没有 Tab 补全需求
        if (!(sender instanceof Player)) return List.of();
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            // 第一级：列出所有子命令
            completions.add("enabled");
            completions.add("cooldowntime");
            // 管理员专属命令仅在拥有对应权限时显示
            if (sender.hasPermission("autototem.admin")) {
                completions.add("setdefaultcooldown");
                completions.add("reload");
            }
        } else if (args.length == 2) {
            // 第二级：根据子命令给出对应的参数候选
            switch (args[0].toLowerCase()) {
                case "enabled" -> {
                    completions.add("true");
                    completions.add("false");
                }
                case "cooldowntime", "cd", "setdefaultcooldown", "sdc" -> completions.add("<tick>");
            }
        }
        return completions;
    }
}
