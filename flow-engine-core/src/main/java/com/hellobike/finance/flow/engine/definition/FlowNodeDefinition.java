package com.hellobike.finance.flow.engine.definition;

import lombok.Builder;
import lombok.Getter;

/**
 * @author 徐磊080
 */
@Getter
@Builder
public class FlowNodeDefinition extends BaseDefinition {

    /**
     * 节点名称(流程内唯一)
     */
    private String name;

    /**
     * 节点类型
     *
     * @see com.hellobike.finance.flow.engine.common.FlowNodeType
     */
    private String type;

    /**
     * 类全限定名
     */
    private String className;

    /**
     * 节点业务描述
     */
    private String description;
}
