package com.hellobike.finance.flow.engine.spi;

/**
 * 路径加载策略
 *
 * @author 徐磊080
 */
public interface SpiLoadStrategy extends Comparable<SpiLoadStrategy> {

    int MAX_PRIORITY = Integer.MIN_VALUE;
    int MIN_PRIORITY = Integer.MAX_VALUE;

    /**
     * 加载路径
     */
    String directory();

    /**
     * 路径优先级
     */
    int getPriority();

    /**
     * 需要额外排除的路径列表
     */
    default String[] excludedPackages() {
        return null;
    }

    /**
     * 如果扩展名相同, 是否支持被覆盖
     */
    default boolean overridden() {
        return false;
    }

    @Override
    default int compareTo(SpiLoadStrategy that) {
        return Integer.compare(this.getPriority(), that.getPriority());
    }
}
