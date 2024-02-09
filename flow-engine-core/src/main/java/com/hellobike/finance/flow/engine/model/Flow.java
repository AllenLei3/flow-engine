package com.hellobike.finance.flow.engine.model;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.definition.FlowDefinition;
import com.hellobike.finance.flow.engine.definition.FlowLineDefinition;
import com.hellobike.finance.flow.engine.definition.FlowNodeDefinition;
import com.hellobike.finance.flow.engine.exception.FlowBuildException;
import com.hellobike.finance.flow.engine.exception.FlowParseException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.execute.FlowResponse;
import com.hellobike.finance.flow.engine.execute.FlowStatus;
import com.hellobike.finance.flow.engine.model.line.FlowLine;
import com.hellobike.finance.flow.engine.model.node.FlowNode;
import com.hellobike.finance.flow.engine.spi.FlowServiceLoader;
import com.hellobike.finance.flow.engine.spi.instance.InstanceFactory;
import com.hellobike.finance.flow.engine.utils.StringUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 徐磊080
 */
@Getter
public final class Flow implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(Flow.class);

    /**
     * 流程唯一名称
     */
    private String name;

    /**
     * 流程默认起点
     */
    private FlowNode origin;

    /**
     * 流程节点
     */
    private List<FlowNode> nodes;

    /**
     * 流程连线
     */
    private List<FlowLine> lines;

    /**
     * 构建流程实体
     *
     * @param definition 流程定义
     * @param configuration 全局配置
     * @throws FlowParseException 流程解析异常
     */
    public void build(FlowDefinition definition, FlowConfiguration configuration)
            throws FlowParseException, FlowBuildException {
        // 解析定义
        parseFlowDefinition(definition);
        parseFlowLine(definition, configuration);
        parseFlowNode(definition, configuration);
        checkFlowElementDuplicate();
        // 构建实体
        lines.forEach(line -> line.build(this, configuration));
        nodes.forEach(node -> node.build(this, configuration));
        // DAG
        findOriginNode();
        checkDAGLoop();
    }

    /**
     * 流程执行
     *
     * @param node 起始执行节点
     * @param context 执行上下文
     * @return 流程执行结果
     */
    public FlowResponse execute(FlowNode node, FlowContext context) {
        FlowResponse response = new FlowResponse();
        response.setFlowName(name);
        response.setStatus(FlowStatus.RUN);
        // 递归调用节点
        FlowNode curNode = node;
        while (curNode != null && FlowStatus.RUN.equals(response.getStatus())) {
            curNode = curNode.executeAndGetNextNode(context, response);
        }
        if (response.getStatus().equals(FlowStatus.RUN)) {
            response.setStatus(FlowStatus.END);
        }
        return response;
    }

    /**
     * 根据节点名称获取DAG中节点对象
     *
     * @param flowNodeName 节点名称
     * @return 节点对象实体
     */
    public FlowNode searchNode(String flowNodeName) {
        return dfs(flowNodeName, origin);
    }

    private FlowNode dfs(String flowNodeName, FlowNode parent) {
        if (parent.getName().equals(flowNodeName)) {
            return parent;
        }
        while (!parent.getTargetLines().isEmpty()) {
            for (FlowLine line : parent.getTargetLines()) {
                FlowNode node = dfs(flowNodeName, line.getTargetNode());
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }

    private void parseFlowDefinition(FlowDefinition definition) throws FlowParseException {
        if (StringUtils.isBlank(definition.getName())) {
            throw new FlowParseException("流程名称不能为空!");
        }
        this.name = definition.getName();
    }

    private void parseFlowLine(FlowDefinition definition, FlowConfiguration configuration) throws FlowParseException {
        if (definition.getFlowLineDefinitions() == null) {
            return;
        }
        List<FlowLine> lines = new ArrayList<>();
        for (FlowLineDefinition lineDefinition : definition.getFlowLineDefinitions()) {
            FlowLine line = new FlowLine();
            try {
                line.parse(lineDefinition, configuration);
                lines.add(line);
            } catch (FlowParseException e) {
                throw new FlowParseException("解析流程[" + name + "]连线异常!", e);
            }
        }
        this.lines = lines;
    }

    private void parseFlowNode(FlowDefinition definition, FlowConfiguration configuration) throws FlowParseException {
        if (definition.getFlowNodeDefinitions() == null || definition.getFlowNodeDefinitions().isEmpty()) {
            throw new FlowParseException("流程[" + name + "]必须至少有一个节点!");
        }
        List<FlowNode> flowNodes = new ArrayList<>();
        InstanceFactory instanceFactory = FlowServiceLoader.getLoader(InstanceFactory.class).getExtension();
        for (FlowNodeDefinition flowNodeDefinition : definition.getFlowNodeDefinitions()) {
            FlowNode flowNode = (FlowNode) instanceFactory.getInstance(flowNodeDefinition.getClassName());
            if (flowNode == null) {
                throw new FlowParseException("流程[" + name + "]实例化[" + flowNodeDefinition.getType() + "]节点异常!");
            }
            try {
                flowNode.parse(flowNodeDefinition, configuration);
                flowNodes.add(flowNode);
            } catch (FlowParseException e) {
                throw new FlowParseException("流程[" + name + "]解析节点异常!", e);
            }
        }
        this.nodes = flowNodes;
    }

    private void checkFlowElementDuplicate() throws FlowParseException {
        Set<FlowElement<?>> elements = new HashSet<>();
        for (FlowNode node : nodes) {
            if (elements.contains(node)) {
                throw new FlowParseException("流程 [" + name + "] 存在重复的节点名称! NAME: " + node.getName());
            }
            elements.add(node);
        }
        for (FlowLine line : lines) {
            if (elements.contains(line)) {
                throw new FlowParseException("流程 [" + name + "] 存在重复的连线! " +
                        "SOURCE: " + line.getSourceNodeName() + ", TARGET: " + line.getTargetNodeName());
            }
            elements.add(line);
        }
        elements.clear();
    }

    private void findOriginNode() throws FlowBuildException {
        // 获取DAG中入度为0的节点
        List<FlowNode> noSourceNodes = nodes.stream()
                .filter(node -> node.getSourceLines() == null || node.getSourceLines().isEmpty())
                .collect(Collectors.toList());
        if (noSourceNodes.isEmpty()) {
            throw new FlowBuildException("流程[" + name + "]不存在起始节点!");
        }
        if (noSourceNodes.size() > 1) {
            throw new FlowBuildException("流程[" + name + "]存在多个起始节点!");
        }
        this.origin = noSourceNodes.get(0);
    }

    private void checkDAGLoop() throws FlowBuildException {
        Stack<FlowNode> flowNodeStack = new Stack<>();
        Set<FlowNode> visitedSet = new HashSet<>();
        dfs(origin, node -> false, flowNodeStack, visitedSet, true);
        // 判断是否联通
        if (nodes.size() != visitedSet.size()) {
            throw new FlowBuildException("流程 [" + name + "] 必须是一个完整联通的流程!");
        }
    }

    private void dfs(FlowNode flowNode, Function<FlowNode, Boolean> function,
                     Stack<FlowNode> flowNodeStack,
                     Set<FlowNode> visitedSet, Boolean isDirect) throws FlowBuildException {
        visitedSet.add(flowNode);
        flowNodeStack.push(flowNode);
        // 遍历结束
        if (function.apply(flowNode) ||
                (isDirect ? flowNode.getTargetLines().isEmpty() : flowNode.getSourceLines().isEmpty())) {
            flowNodeStack.pop();
            return;
        }
        for (FlowLine line : isDirect ? flowNode.getTargetLines() : flowNode.getSourceLines()) {
            FlowNode next = isDirect ? line.getTargetNode() : line.getSourceNode();
            if (containsLoop(next, flowNodeStack, visitedSet)) {
                throw new FlowBuildException("节点 [" + flowNode.getName() + "] 异常, 流程不能存在闭环");
            }
            dfs(next, function, flowNodeStack, visitedSet, isDirect);
        }
        flowNodeStack.pop();
    }

    private boolean containsLoop(FlowNode flowNode, Stack<FlowNode> flowNodeStack, Set<FlowNode> visitedSet) {
        if (visitedSet.contains(flowNode)) {
            return -1 != flowNodeStack.search(flowNode);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flow flow = (Flow) o;
        return Objects.equals(name, flow.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
