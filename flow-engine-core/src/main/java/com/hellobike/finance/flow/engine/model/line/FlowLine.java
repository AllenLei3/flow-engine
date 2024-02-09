package com.hellobike.finance.flow.engine.model.line;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.definition.FlowLineDefinition;
import com.hellobike.finance.flow.engine.exception.FlowBuildException;
import com.hellobike.finance.flow.engine.exception.FlowParseException;
import com.hellobike.finance.flow.engine.model.FlowElement;
import com.hellobike.finance.flow.engine.model.Flow;
import com.hellobike.finance.flow.engine.model.node.FlowNode;
import com.hellobike.finance.flow.engine.utils.StringUtils;
import lombok.Getter;

import java.util.Objects;
import java.util.Optional;

/**
 * @author 徐磊080
 */
@Getter
public class FlowLine implements FlowElement<FlowLineDefinition> {

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

    /**
     * 来源节点
     */
    private FlowNode sourceNode;

    /**
     * 目标节点
     */
    private FlowNode targetNode;

    @Override
    public void parse(FlowLineDefinition definition, FlowConfiguration configuration) throws FlowParseException {
        if (StringUtils.isBlank(definition.getSourceNodeName())) {
            throw new FlowParseException("来源节点名称不能为空");
        }
        if (StringUtils.isBlank(definition.getTargetNodeName())) {
            throw new FlowParseException("目标节点名称不能为空");
        }
        this.sourceNodeName = definition.getSourceNodeName();
        this.targetNodeName = definition.getTargetNodeName();
        this.switchValue = definition.getSwitchValue();
        this.defaultLine = definition.getDefaultLine();
    }

    @Override
    public void build(Flow flow, FlowConfiguration configuration) throws FlowBuildException {
        Optional<FlowNode> sourceNode = flow.getNodes()
                .stream()
                .filter(node -> sourceNodeName.equals(node.getName()))
                .findFirst();
        Optional<FlowNode> targetNode = flow.getNodes()
                .stream()
                .filter(node -> targetNodeName.equals(node.getName()))
                .findFirst();
        if (!sourceNode.isPresent()) {
            throw new FlowBuildException("连线来源节点不存在!");
        }
        if (!targetNode.isPresent()) {
            throw new FlowBuildException("连线目标节点不存在!");
        }
        this.sourceNode = sourceNode.get();
        this.targetNode = targetNode.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowLine flowLine = (FlowLine) o;
        return Objects.equals(sourceNodeName, flowLine.sourceNodeName) &&
                Objects.equals(targetNodeName, flowLine.targetNodeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceNodeName, targetNodeName);
    }
}
