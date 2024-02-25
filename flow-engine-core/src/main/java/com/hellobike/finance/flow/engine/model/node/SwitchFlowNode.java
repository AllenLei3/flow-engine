package com.hellobike.finance.flow.engine.model.node;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.exception.FlowBuildException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.execute.FlowNodeResult;
import com.hellobike.finance.flow.engine.execute.FlowResponse;
import com.hellobike.finance.flow.engine.model.Flow;
import com.hellobike.finance.flow.engine.model.line.FlowLine;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 判断节点
 * 入线: 零、一个、多个
 * 出线: 一个、多个
 *
 * @author 徐磊080
 */
public abstract class SwitchFlowNode extends FlowNode {

    /**
     * 默认分支
     */
    protected FlowLine defaultLine;

    @Override
    public void build(Flow flow, FlowConfiguration configuration) throws FlowBuildException {
        super.build(flow, configuration);
        if (targetLines.isEmpty()) {
            throw new FlowBuildException("SWITCH节点 [" + name + "] 至少有一条出线!");
        }
        List<FlowLine> defaultLines = targetLines.stream()
                .filter(line -> Boolean.TRUE.equals(line.getDefaultLine()))
                .collect(Collectors.toList());
        if (defaultLines.size() > 1) {
            throw new FlowBuildException("SWITCH节点 [" + name + "] 最多只能有一条默认分支!");
        }
        defaultLine = defaultLines.isEmpty() ? null : defaultLines.get(0);
    }

    @Override
    protected void nodeExtension(FlowContext context, FlowNodeResult nodeResult, FlowResponse response) {
        nodeResult.setSwitchValue(switchValue(context));
    }

    @Override
    protected FlowNode getNextNode(FlowContext context) {
        // TODO 后续可抽象 eg:脚本
        String switchValue = switchValue(context);
        for (FlowLine targetLine : targetLines) {
            if (Objects.equals(switchValue, targetLine.getSwitchValue())) {
                return targetLine.getTargetNode();
            }
        }
        return defaultLine == null ? null : defaultLine.getTargetNode();
    }

    /**
     * 返回分支条件,用于选择下游节点
     */
    protected abstract String switchValue(FlowContext context);
}
