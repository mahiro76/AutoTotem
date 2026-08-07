package top.mahiro76.autoTotem;

import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AutoTotem 插件主类。
 * <p>
 * 负责插件的启动/关闭、玩家状态的存储（自动图腾开关、冷却时间、上次触发 tick），
 * 以及图腾的查找与消耗。
 */
public final class AutoTotem extends JavaPlugin {

    /** 玩家 UUID -> 是否开启自动图腾（null 表示未设置，使用默认值） */
    private final Map<UUID, Boolean> enabledPlayers = new HashMap<>();
    /** 玩家 UUID -> 自定义冷却时间（tick），未设置时使用默认值 */
    private final Map<UUID, Integer> cooldownTicks = new HashMap<>();
    /** 玩家 UUID -> 上次触发自动图腾时所在世界的 tick（用于冷却判断） */
    private final Map<UUID, Long> lastTriggerTicks = new HashMap<>();

    /** 语言文件管理器（messages.yml） */
    private MessageManager messageManager;

    /** 玩家未单独设置时的默认开关状态（来自 config.yml） */
    private boolean defaultEnabled = true;
    /** 玩家未单独设置时的默认冷却时间（tick，来自 config.yml） */
    private int defaultCooldownTicks = 0;

    /**
     * 插件启动时调用。
     * <ol>
     *   <li>保存默认配置（若插件目录下还没有 config.yml）</li>
     *   <li>加载配置</li>
     *   <li>初始化语言文件管理器</li>
     *   <li>注册伤害监听器 {@link AutoTotemListener}</li>
     *   <li>注册 /autototem 命令的执行器与 Tab 补全</li>
     * </ol>
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        messageManager = new MessageManager(this);
        getServer().getPluginManager().registerEvents(new AutoTotemListener(this), this);
        AutoTotemCommand command = new AutoTotemCommand(this);
        PluginCommand autoTotemCommand = getCommand("autototem");
        if (autoTotemCommand != null) {
            autoTotemCommand.setExecutor(command);
            autoTotemCommand.setTabCompleter(command);
        }
        getLogger().info(messageManager.get("plugin-enabled"));
    }

    /** 插件关闭时调用，仅输出日志提示。 */
    @Override
    public void onDisable() {
        getLogger().info(messageManager == null ? "AutoTotem has been disabled" : messageManager.get("plugin-disabled"));
    }

    /**
     * 重新加载配置文件。
     * <p>读取 config.yml 中的默认开关状态与默认冷却时间，并对冷却时间做非负保护。
     */
    public void loadConfig() {
        reloadConfig();
        defaultEnabled = getConfig().getBoolean("default-enabled", true);
        defaultCooldownTicks = Math.max(0, getConfig().getInt("default-cooldown-ticks", 0));
    }

    /** 获取语言文件管理器。 */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * 获取指定玩家当前的自动图腾开关状态。
     *
     * @param player 目标玩家
     * @return 开启返回 true；若玩家从未设置过，则返回配置的默认值
     */
    public boolean isEnabled(Player player) {
        return enabledPlayers.getOrDefault(player.getUniqueId(), defaultEnabled);
    }

    /**
     * 设置指定玩家自动图腾的开关状态（按玩家独立记忆）。
     *
     * @param player  目标玩家
     * @param enabled true 开启 / false 关闭
     */
    public void setEnabled(Player player, boolean enabled) {
        enabledPlayers.put(player.getUniqueId(), enabled);
    }

    /**
     * 获取指定玩家的触发冷却时间。
     *
     * @param player 目标玩家
     * @return 冷却 tick 数；若玩家未单独设置，则返回配置的默认值
     */
    public int getCooldownTicks(Player player) {
        return cooldownTicks.getOrDefault(player.getUniqueId(), defaultCooldownTicks);
    }

    /**
     * 设置指定玩家的触发冷却时间（对负数做非负保护）。
     *
     * @param player 目标玩家
     * @param ticks  冷却 tick 数（20 tick = 1 秒）
     */
    public void setCooldownTicks(Player player, int ticks) {
        cooldownTicks.put(player.getUniqueId(), Math.max(0, ticks));
    }

    /**
     * 获取指定玩家上次触发自动图腾时的世界 tick。
     *
     * @param player 目标玩家
     * @return 上次触发 tick；若从未触发过则返回 {@link Long#MIN_VALUE}，便于首次触发不被冷却拦截
     */
    public long getLastTriggerTick(Player player) {
        return lastTriggerTicks.getOrDefault(player.getUniqueId(), Long.MIN_VALUE);
    }

    /**
     * 记录指定玩家的自动图腾触发时刻。
     *
     * @param player    目标玩家
     * @param worldTick 触发时所在世界的 tick（{@code World#getFullTime()}）
     */
    public void markTriggered(Player player, long worldTick) {
        lastTriggerTicks.put(player.getUniqueId(), worldTick);
    }

    /**
     * 判断玩家背包中是否存有图腾（只判断，不消耗）。
     * <p>查找范围：副手 -> 主手 -> 背包全部物品栏。
     *
     * @param player 目标玩家
     * @return 存在图腾返回 true
     */
    public boolean hasTotem(Player player) {
        if (isTotem(player.getInventory().getItemInOffHand())) return true;
        if (isTotem(player.getInventory().getItemInMainHand())) return true;
        for (ItemStack item : player.getInventory().getContents()) {
            if (isTotem(item)) return true;
        }
        return false;
    }

    /**
     * 从玩家背包中消耗一个图腾。
     * <p>消耗顺序：副手 -> 主手 -> 背包全部物品栏（与 {@link #hasTotem} 保持一致）。
     *
     * @param player 目标玩家
     * @return 成功消耗返回 true；背包中没有图腾返回 false
     */
    public boolean consumeTotem(Player player) {
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (isTotem(offHand)) {
            offHand.setAmount(offHand.getAmount() - 1);
            return true;
        }
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (isTotem(mainHand)) {
            mainHand.setAmount(mainHand.getAmount() - 1);
            return true;
        }
        for (ItemStack item : player.getInventory().getContents()) {
            if (isTotem(item)) {
                item.setAmount(item.getAmount() - 1);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个物品是否是有效的不死图腾。
     *
     * @param item 待判断的物品，可能为 null（空的物品栏格）
     * @return 物品为图腾且数量大于 0 时返回 true
     */
    private boolean isTotem(ItemStack item) {
        return item != null && item.getType() == Material.TOTEM_OF_UNDYING && item.getAmount() > 0;
    }
}
