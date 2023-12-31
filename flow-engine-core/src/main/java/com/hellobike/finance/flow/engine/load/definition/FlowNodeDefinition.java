package com.hellobike.finance.flow.engine.load.definition;

import lombok.Builder;
import lombok.Getter;

/**
 * @author xulei
 */
@Getter
@Builder
public class FlowNodeDefinition {

    /**
     * 节点名称(流程内唯一)
     */
    private final String name;

    /**
     * 节点类型
     *
     * @see com.hellobike.finance.flow.engine.common.NodeTypeEnum
     */
    private final String type;
}
