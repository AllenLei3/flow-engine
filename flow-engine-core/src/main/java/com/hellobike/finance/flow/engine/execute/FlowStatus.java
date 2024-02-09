package com.hellobike.finance.flow.engine.execute;

/**
 * @author 徐磊080
 */
public enum FlowStatus {

    /**
     * 运行中
     */
    RUN,

    /**
     * 暂停
     */
    SUSPEND,

    /**
     * 正常结束
     */
    END,

    /**
     * 运行失败
     */
    FAIL
}
