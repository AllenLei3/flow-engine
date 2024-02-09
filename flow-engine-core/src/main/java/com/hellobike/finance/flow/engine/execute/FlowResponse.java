package com.hellobike.finance.flow.engine.execute;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @author 徐磊080
 */
@Getter
@Setter
public class FlowResponse {

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程运行状态
     */
    private FlowStatus status;

    /**
     * 当前执行节点名称
     */
    private String currentFlowNodeName;

    /**
     * 流程节点执行结果
     */
    private ArrayList<FlowNodeResult> nodeResults = new ArrayList<>();
}
