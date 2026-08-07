package top.mahiro76.autoTotem.adapter;

import org.bukkit.Particle;
import top.mahiro76.autoTotem.VersionAdapter;

/**
 * 1.20.x 版本适配器。
 * <p>该版本线的 Paper API 使用 {@code Particle.TOTEM}。
 */
public final class Adapter120 implements VersionAdapter {

    @Override
    public Particle getTotemParticle() {
        return Particle.TOTEM;
    }
}
