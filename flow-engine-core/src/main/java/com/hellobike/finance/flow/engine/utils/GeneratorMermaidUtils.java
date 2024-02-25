package com.hellobike.finance.flow.engine.utils;

import com.hellobike.finance.flow.engine.common.FlowNodeType;
import com.hellobike.finance.flow.engine.definition.FlowDefinition;
import com.hellobike.finance.flow.engine.definition.FlowNodeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 徐磊080
 */
public class GeneratorMermaidUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorMermaidUtils.class);

    /**
     * 生成{@code FlowDefinition}对应的Mermaid流程图文件
     *
     * @param file 原始流程定义文件
     * @param definition 流程定义
     */
    public static void generatorMermaidFile(File file, FlowDefinition definition) {
        try {
            List<String> contentLines = new ArrayList<>();
            contentLines.add("```mermaid");
            contentLines.add("flowchart TD");
            contentLines.addAll(generatorMermaidFileContent(definition));
            contentLines.add("```");

            Path mermaidFilePath = Paths.get(getMermaidFilePath(file));
            if (!Files.exists(mermaidFilePath)) {
                Files.createFile(mermaidFilePath);
            }
            String content = String.join("\n", contentLines);
            Files.write(mermaidFilePath, content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.warn("生成Mermaid流程图文件异常! file:{}", file.getName(), e);
        }
    }

    private static List<String> generatorMermaidFileContent(FlowDefinition definition) {
        List<String> mermaidContent = new ArrayList<>();

        Map<String, FlowNodeDefinition> nodeDefinitionMap = definition.getFlowNodeDefinitions()
                .stream()
                .collect(Collectors.toMap(FlowNodeDefinition::getName, Function.identity()));

        definition.getFlowLineDefinitions().forEach(line -> {
            FlowNodeDefinition sourceNode = nodeDefinitionMap.get(line.getSourceNodeName());
            FlowNodeDefinition targetNode = nodeDefinitionMap.get(line.getTargetNodeName());
            String sourceComment = getNodeComment(FlowNodeType.valueOf(sourceNode.getType()));
            String targetComment = getNodeComment(FlowNodeType.valueOf(targetNode.getType()));
            String sourceName = sourceNode.getName() + (sourceNode.getDescription() == null ? "" : "\\n" + sourceNode.getDescription());
            String targetName = targetNode.getName() + (targetNode.getDescription() == null ? "" : "\\n" + targetNode.getDescription());
            String lineComment = "";
            if (line.getSwitchValue() != null && line.getDefaultLine()) {
                lineComment += " |" + line.getSwitchValue() + "\\n默认|";
            } else if (line.getSwitchValue() != null) {
                lineComment += " |" + line.getSwitchValue() + "|";
            } else if (line.getDefaultLine()) {
                lineComment += " |默认|";
            }
            String print = sourceNode.getName() + String.format(sourceComment, sourceName)
                    + " "
                    + getNodeLine(FlowNodeType.valueOf(sourceNode.getType()))
                    + " "
                    + lineComment
                    + " "
                    + targetNode.getName() + String.format(targetComment, targetName);
            mermaidContent.add(print);
        });
        return mermaidContent;
    }

    private static String getNodeComment(FlowNodeType type) {
        switch (type) {
            case SWITCH:
                return "{%s}";
            default:
                return "[%s]";
        }
    }

    private static String getNodeLine(FlowNodeType type) {
        switch (type) {
            case SUSPEND:
                return "-.->";
            default:
                return "-->";
        }
    }

    private static String getMermaidFilePath(File file) {
        String originalPath = file.getPath();
        boolean testDir = originalPath.contains("target/test-classes/");
        String targetClz = testDir ? "target/test-classes/" : "target/classes/";
        String targetDir = testDir ? "src/test/resources" : "src/main/resources";
        // 拼接文件所在目录
        String prefix = originalPath.substring(0, originalPath.lastIndexOf(targetClz));
        String suffix = originalPath.substring(originalPath.lastIndexOf(targetClz) + targetClz.length() - 1);
        suffix = suffix.substring(0, suffix.lastIndexOf("."))+".md";
        return prefix + targetDir + suffix;

    }
}
