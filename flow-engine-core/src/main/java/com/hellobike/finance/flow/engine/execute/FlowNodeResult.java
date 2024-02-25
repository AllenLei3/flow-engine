package com.hellobike.finance.flow.engine.execute;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 徐磊080
 */
@Getter
@Setter
public class FlowNodeResult {

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * SWITCH节点判断值
     */
    private String switchValue;

    /**
     * SUSPEND节点暂停id
     */
    private String suspendId;

    /**
     * 节点执行是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String exceptionStack;

    /**
     * 节点执行耗时(ms)
     */
    private Long cost;
}
