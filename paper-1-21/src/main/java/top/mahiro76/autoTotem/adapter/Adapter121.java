package top.mahiro76.autoTotem.adapter;

import org.bukkit.Particle;
import top.mahiro76.autoTotem.VersionAdapter;

/**
 * 1.21.x 版本适配器。
 * <p>该版本线的 Paper API 使用 {@code Particle.TOTEM_OF_UNDYING}（1.20.5 起更名）。
 */
public final class Adapter121 implements VersionAdapter {

    @Override
    public Particle getTotemParticle() {
        return Particle.TOTEM_OF_UNDYING;
    }
}
