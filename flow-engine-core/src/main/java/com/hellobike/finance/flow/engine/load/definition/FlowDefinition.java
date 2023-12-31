package com.hellobike.finance.flow.engine.load.definition;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author xulei
 */
@Getter
@Builder
public class FlowDefinition {

    /**
     * 流程名称(全局唯一)
     */
    private String name;

    /**
     * 流程节点
     */
    private List<FlowNodeDefinition> flowNodeDefinitions;

    /**
     * 流程连线
     */
    private List<FlowLineDefinition> flowLineDefinitions;
}
