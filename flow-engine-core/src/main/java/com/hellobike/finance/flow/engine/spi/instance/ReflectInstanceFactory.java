package com.hellobike.finance.flow.engine.spi.instance;

/**
 * 反射获取实例对象
 *
 * @author 徐磊080
 */
public class ReflectInstanceFactory implements InstanceFactory {

    @Override
    public Object getInstance(Class<?> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("ReflectInstanceFactory getInstance error!", e);
        }
    }

    @Override
    public Object getInstance(String className) {
        try {
            Class<?> onwClass = Class.forName(className);
            return onwClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("ReflectInstanceFactory getInstance byName error!", e);
        }
    }
}