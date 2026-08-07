package top.mahiro76.autoTotem;

import org.bukkit.Particle;

/**
 * 默认版本适配器（回退实现）。
 * <p>
 * 当版本适配器反射加载失败时使用。通过运行时字符串解析粒子名：
 * 优先尝试新版本名称 {@code TOTEM_OF_UNDYING}，回退到旧版本名称 {@code TOTEM}。
 */
public final class DefaultVersionAdapter implements VersionAdapter {

    @Override
    public Particle getTotemParticle() {
        try {
            return Particle.valueOf("TOTEM_OF_UNDYING");
        } catch (IllegalArgumentException e) {
            return Particle.valueOf("TOTEM");
        }
    }
}
