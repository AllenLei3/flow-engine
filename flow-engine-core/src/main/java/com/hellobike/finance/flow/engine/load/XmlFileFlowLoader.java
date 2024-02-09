package com.hellobike.finance.flow.engine.load;

import com.hellobike.finance.flow.engine.common.FlowConstants;
import com.hellobike.finance.flow.engine.common.FlowNodeType;
import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.exception.FlowLoadException;
import com.hellobike.finance.flow.engine.definition.FlowDefinition;
import com.hellobike.finance.flow.engine.definition.FlowLineDefinition;
import com.hellobike.finance.flow.engine.definition.FlowNodeDefinition;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 徐磊080
 */
public class XmlFileFlowLoader implements FlowLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFileFlowLoader.class);

    @Override
    public FlowDefinition load(String content, FlowConfiguration configuration) throws FlowLoadException {
        Document document;
        try {
            document = DocumentHelper.parseText(content);
        } catch (DocumentException e) {
            throw new FlowLoadException(e);
        }
        Element rootElement = document.getRootElement();
        String flowName = rootElement.attributeValue(FlowConstants.NAME);
        if (flowName == null) {
            throw new FlowLoadException("The flow element must have name attribute!");
        }
        List<FlowNodeDefinition> nodeDefinitions = new ArrayList<>();
        List<FlowLineDefinition> lineDefinitions = new ArrayList<>();

        // 解析node
        Element nodesElement = rootElement.element(FlowConstants.NODES);
        if (nodesElement != null) {
            List<Element> nodeList = nodesElement.elements(FlowConstants.NODE);
            String name, className, type, description;
            for (Element e : nodeList) {
                name = e.attributeValue(FlowConstants.NAME);
                if (name == null) {
                    throw new FlowLoadException("The node element must have name attribute! flowName:" + flowName);
                }
                className = e.attributeValue(FlowConstants.CLASS_NAME);
                type = e.attributeValue(FlowConstants.TYPE);
                description = e.attributeValue(FlowConstants.DESC);
                FlowNodeDefinition nodeDefinition = FlowNodeDefinition.builder()
                        .name(name)
                        .className(className == null ? name : className)
                        .type(type == null ? FlowNodeType.COMMON.name() : type)
                        .description(description)
                        .build();
                nodeDefinitions.add(nodeDefinition);
            }
        } else {
            throw new FlowLoadException("The flow element must have nodes element");
        }

        // 解析line
        Element lineElement = rootElement.element(FlowConstants.LINES);
        if (lineElement != null) {
            List<Element> lineList = lineElement.elements(FlowConstants.LINE);
            String sourceNodeName, targetNodeName, switchValue;
            boolean defaultLine;
            for (Element e : lineList) {
                sourceNodeName = e.attributeValue(FlowConstants.SOURCE_NODE_NAME);
                if (sourceNodeName == null) {
                    throw new FlowLoadException("The line element must have sourceNodeName attribute! flowName:" + flowName);
                }
                targetNodeName = e.attributeValue(FlowConstants.TARGET_NODE_NAME);
                if (targetNodeName == null) {
                    throw new FlowLoadException("The line element must have targetNodeName attribute! flowName:" + flowName);
                }
                switchValue = e.attributeValue(FlowConstants.SWITCH_VALUE);
                defaultLine = Boolean.TRUE.toString().equals(e.attributeValue(FlowConstants.DEFAULT_LINE));
                FlowLineDefinition lineDefinition = FlowLineDefinition.builder()
                        .sourceNodeName(sourceNodeName)
                        .targetNodeName(targetNodeName)
                        .switchValue(switchValue)
                        .defaultLine(defaultLine)
                        .build();
                lineDefinitions.add(lineDefinition);
            }
        }
        FlowDefinition definition = FlowDefinition.builder()
                .name(flowName)
                .flowNodeDefinitions(nodeDefinitions)
                .flowLineDefinitions(lineDefinitions)
                .build();
        LOGGER.info("Load FlowDefinition [" + flowName + "] Success!");
        return definition;
    }
}
