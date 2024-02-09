package com.hellobike.finance.flow.engine.model.node;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.definition.FlowNodeDefinition;
import com.hellobike.finance.flow.engine.exception.FlowBuildException;
import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.exception.FlowParseException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.execute.FlowNodeResult;
import com.hellobike.finance.flow.engine.execute.FlowResponse;
import com.hellobike.finance.flow.engine.execute.FlowStatus;
import com.hellobike.finance.flow.engine.model.FlowElement;
import com.hellobike.finance.flow.engine.model.Flow;
import com.hellobike.finance.flow.engine.model.line.FlowLine;
import com.hellobike.finance.flow.engine.utils.StringUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 徐磊080
 */
@Getter
public abstract class FlowNode implements FlowElement<FlowNodeDefinition> {

    private static final Logger LOG = LoggerFactory.getLogger(FlowNode.class);

    /**
     * 节点名称(流程内唯一)
     */
    protected String name;

    /**
     * 节点类型
     *
     * @see com.hellobike.finance.flow.engine.common.FlowNodeType
     */
    protected String type;

    /**
     * 指向当前节点的线
     */
    protected List<FlowLine> sourceLines;

    /**
     * 从当前节点指出的线
     */
    protected List<FlowLine> targetLines;

    @Override
    public void parse(FlowNodeDefinition definition, FlowConfiguration configuration) throws FlowParseException {
        if (StringUtils.isBlank(definition.getName())) {
            throw new FlowParseException("节点名称不能为空");
        }
        if (StringUtils.isBlank(definition.getType())) {
            throw new FlowParseException("节点类型不能为空");
        }
        this.name = definition.getName();
        this.type = definition.getType();
    }

    @Override
    public void build(Flow flow, FlowConfiguration configuration) throws FlowBuildException {
        List<FlowLine> sourceLines = new ArrayList<>();
        List<FlowLine> targetLines = new ArrayList<>();
        for (FlowLine line : flow.getLines()) {
            if (name.equals(line.getSourceNodeName())) {
                targetLines.add(line);
            } else if (name.equals(line.getTargetNodeName())){
                sourceLines.add(line);
            }
        }
        this.sourceLines = sourceLines;
        this.targetLines = targetLines;
    }

    /**
     * 执行当前节点, 并返回要执行的下一个节点
     *
     * @param context 执行上下文
     * @param response 流程执行结果
     * @return 要执行的下一个节点
     */
    public FlowNode executeAndGetNextNode(FlowContext context, FlowResponse response) {
        LOG.debug("flow [{}] prepare to execute node [{}]", response.getFlowName(), name);
        long start = System.currentTimeMillis();
        response.setCurrentFlowNodeName(name);

        FlowNodeResult nodeResult = new FlowNodeResult();
        nodeResult.setNodeName(name);
        response.getNodeResults().add(nodeResult);
        // 执行节点自身逻辑
        try {
            execute(context);
            nodeExtension(context, nodeResult);
            nodeResult.setSuccess(true);
            response.setStatus(getNodeExecuteStatus());
        } catch (Exception e) {
            LOG.error("flow [{}] execute node [{}] occur Error!", response.getFlowName(), name, e);
            nodeResult.setSuccess(false);
            nodeResult.setException(e);
            response.setStatus(FlowStatus.FAIL);
            return null;
        } finally {
            nodeResult.setCost(System.currentTimeMillis() - start);
        }
        // 返回下一个待执行节点
        return getNextNode(context);
    }

    /**
     * 节点执行实现
     *
     * @param context 执行上下文
     * @throws FlowExecuteException 节点执行异常
     */
    protected abstract void execute(FlowContext context) throws FlowExecuteException;

    /**
     * 自定义扩展逻辑
     */
    protected void nodeExtension(FlowContext context, FlowNodeResult nodeResult) {}

    /**
     * 获取节点执行状态
     */
    protected FlowStatus getNodeExecuteStatus() {
        return FlowStatus.RUN;
    }

    /**
     * 获取当前节点后续要执行的节点
     *
     * @param context 执行上下文
     */
    protected FlowNode getNextNode(FlowContext context) {
        return targetLines.isEmpty() ? null : targetLines.get(0).getTargetNode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowNode flowNode = (FlowNode) o;
        return Objects.equals(name, flowNode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}