package top.mahiro76.autoTotem;

import org.bukkit.Particle;

/**
 * 版本差异适配器。
 * <p>
 * 各版本模块（paper-1-20 / paper-1-21 / paper-26-1 / paper-26-2）针对各自的
 * Paper API 编译本接口的实现，以处理跨版本 API 差异。例如 1.20.5 起
 * {@code Particle.TOTEM} 改名为 {@code Particle.TOTEM_OF_UNDYING}。
 * <p>
 * 运行时由主类 {@link AutoTotem} 根据服务器版本通过反射加载对应实现，
 * 因此 common 模块本身不直接引用任何版本特定的 API 常量。
 */
public interface VersionAdapter {

    /**
     * 获取不死图腾触发时的粒子类型（当前服务器版本对应的常量）。
     *
     * @return 图腾粒子
     */
    Particle getTotemParticle();
}
