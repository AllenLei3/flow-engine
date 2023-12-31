package com.hellobike.finance.flow.engine.load.impl;

import com.hellobike.finance.flow.engine.common.FlowConstants;
import com.hellobike.finance.flow.engine.config.FlowConfiguration;
import com.hellobike.finance.flow.engine.exception.FlowLoadException;
import com.hellobike.finance.flow.engine.load.FlowLoader;
import com.hellobike.finance.flow.engine.load.definition.FlowDefinition;
import com.hellobike.finance.flow.engine.load.definition.FlowLineDefinition;
import com.hellobike.finance.flow.engine.load.definition.FlowNodeDefinition;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xulei
 */
public class XMLFlowLoader implements FlowLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLFlowLoader.class);

    @Override
    public String supportFileSuffix() {
        return "xml";
    }

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
            String name, type;
            for (Element e : nodeList) {
                name = e.attributeValue(FlowConstants.NAME);
                if (name == null) {
                    throw new FlowLoadException("The node element must have name attribute! flowName:" + flowName);
                }
                type = e.attributeValue(FlowConstants.TYPE);
                FlowNodeDefinition nodeDefinition = FlowNodeDefinition.builder()
                        .name(name)
                        .type(type)
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
                defaultLine = "true".equals(e.attributeValue(FlowConstants.DEFAULT_LINE));
                FlowLineDefinition lineDefinition = FlowLineDefinition.builder()
                        .sourceNodeName(sourceNodeName)
                        .targetNodeName(targetNodeName)
                        .switchValue(switchValue)
                        .defaultLine(defaultLine)
                        .build();
                lineDefinitions.add(lineDefinition);
            }
        } else {
            throw new FlowLoadException("The flow element must have lines element");
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
