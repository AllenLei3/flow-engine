package com.hellobike.finance.flow.engine.definition;

import lombok.Builder;
import lombok.Getter;

/**
 * @author 徐磊080
 */
@Getter
@Builder
public class FlowLineDefinition extends BaseDefinition {

    /**
     * 来源节点名称
     */
    private String sourceNodeName;

    /**
     * 目标节点名称
     */
    private String targetNodeName;

    /**
     * 分支条件值,只有匹配分支节点返回值时才走该分支
     */
    private String switchValue;

    /**
     * 是否是默认分支,当所有分支都不满足时默认走该分支
     */
    private Boolean defaultLine;
}
