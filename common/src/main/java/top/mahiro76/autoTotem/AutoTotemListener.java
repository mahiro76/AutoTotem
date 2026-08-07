package top.mahiro76.autoTotem;

import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

/**
 * 玩家受伤事件监听器，自动图腾的核心逻辑所在。
 * <p>
 * 玩家受到致命伤害时，若满足条件（未关闭、未在冷却、非虚空掉落、背包有图腾），
 * 则消耗图腾、取消伤害并施加原版图腾效果。
 */
public final class AutoTotemListener implements Listener {

    /** 主插件实例，用于访问玩家状态与图腾操作 */
    private final AutoTotem plugin;

    /**
     * 构造监听器。
     *
     * @param plugin 主插件实例
     */
    public AutoTotemListener(AutoTotem plugin) {
        this.plugin = plugin;
    }

    /**
     * 玩家受到伤害时触发自动图腾判定。
     * <p>使用 {@link EventPriority#HIGHEST} 优先级，确保在其他插件之后、
     * 伤害结算之前执行，此时 {@code getFinalDamage()} 已经是最终伤害值。
     *
     * @param event 实体受伤事件
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event) {
        // 仅处理玩家受伤
        if (!(event.getEntity() instanceof Player player)) return;
        // 事件已被其他插件取消，或玩家已死亡，跳过
        if (event.isCancelled() || player.isDead()) return;

        // 【死亡判定】掉落虚空不触发自动图腾
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) return;

        // 玩家未开启自动图腾，跳过
        if (!plugin.isEnabled(player)) return;

        // 【冷却判断】距上次触发不足冷却时间则不触发
        long currentTick = player.getWorld().getFullTime();
        long lastTrigger = plugin.getLastTriggerTick(player);
        int cooldown = plugin.getCooldownTicks(player);
        if (cooldown > 0 && lastTrigger != Long.MIN_VALUE && currentTick - lastTrigger < cooldown) return;

        // 【致命伤害判定】只有伤害足以致死时才触发（生命 + 伤害吸收 < 最终伤害）
        double finalDamage = event.getFinalDamage();
        double health = player.getHealth() + player.getAbsorptionAmount();
        if (finalDamage < health) return;

        // 【PVP 判定】若配置关闭 PVP 触发，则被玩家攻击造成的致命伤害不触发自动图腾
        if (!plugin.isPvpEnabled()
                && event instanceof EntityDamageByEntityEvent entityEvent
                && entityEvent.getDamager() instanceof Player) {
            return;
        }

        // 背包中没有图腾则交给原版逻辑（不主动干预）
        if (!plugin.hasTotem(player)) return;

        // 【消耗图腾并取消伤害】消耗失败（如并发竞态）则放弃触发
        if (!plugin.consumeTotem(player)) return;
        event.setCancelled(true);

        // 施加原版图腾效果，并记录触发时间用于冷却
        applyTotemEffects(player);
        plugin.markTriggered(player, currentTick);
    }

    /**
     * 模拟原版不死图腾触发后的效果。
     *
     * @param player 触发图腾的玩家
     */
    private void applyTotemEffects(Player player) {
        // 恢复生命为 1 点并清除身上的火焰
        player.setHealth(1.0);
        player.setFireTicks(0);

        // 移除所有现存药水效果（与原版图腾行为一致）
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            player.removePotionEffect(effect.getType());
        }

        // 添加原版图腾的药水效果：
        // - 生命恢复 II（900 tick，约 45 秒）
        // - 伤害吸收 IV（100 tick，约 5 秒）
        // - 抗火 I（800 tick，约 40 秒）
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 800, 0));

        // 播放图腾使用音效与图腾粒子特效（模拟原版动画）
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);
        // 发送图腾复活实体效果，触发客户端原版动画（绿色粒子升腾 + 全屏粒子幕）
        player.playEffect(EntityEffect.TOTEM_RESURRECT);
        player.getWorld().spawnParticle(plugin.getVersionAdapter().getTotemParticle(), player.getLocation().add(0, 1, 0), 100, 0.5, 0.5, 0.5, 0.0);
    }
}
