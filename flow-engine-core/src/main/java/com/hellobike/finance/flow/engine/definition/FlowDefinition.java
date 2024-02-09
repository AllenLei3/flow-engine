package com.hellobike.finance.flow.engine.definition;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author 徐磊080
 */
@Getter
@Builder
public class FlowDefinition extends BaseDefinition {

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
