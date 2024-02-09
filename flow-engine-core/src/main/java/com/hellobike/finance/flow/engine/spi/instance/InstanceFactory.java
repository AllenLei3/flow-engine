package com.hellobike.finance.flow.engine.spi.instance;

import com.hellobike.finance.flow.engine.spi.SPI;

/**
 * 用于SPI加载Class实例
 *
 * @author 徐磊080
 */
@SPI
public interface InstanceFactory {

    /**
     * 获取Class类型的实例
     *
     * @param type Class
     * @return class instance
     */
    Object getInstance(Class<?> type);

    /**
     * 获取Class类型的实例
     *
     * @param className className
     * @return class instance
     */
    Object getInstance(String className);
}