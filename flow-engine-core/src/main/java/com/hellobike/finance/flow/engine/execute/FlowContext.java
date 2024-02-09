package com.hellobike.finance.flow.engine.execute;

import java.io.Serializable;

/**
 * 流程执行上下文
 *
 * @author 徐磊080
 */
public interface FlowContext extends Serializable {

    /**
     * 获取上下文原始类型对象
     *
     * @param clz target class
     * @return target class instance
     */
    default <T extends FlowContext> T getInstance(Class<T> clz) {
        if (clz.isAssignableFrom(this.getClass())) {
            return (T) this;
        }
        throw new IllegalArgumentException("FlowContext type not match! cast error!");
    }

}
