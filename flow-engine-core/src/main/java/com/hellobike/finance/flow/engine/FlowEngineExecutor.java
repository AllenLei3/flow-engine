package com.hellobike.finance.flow.engine;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.definition.FlowDefinition;
import com.hellobike.finance.flow.engine.exception.FlowBuildException;
import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.exception.FlowLoadException;
import com.hellobike.finance.flow.engine.exception.FlowParseException;
import com.hellobike.finance.flow.engine.execute.FlowContext;
import com.hellobike.finance.flow.engine.execute.FlowResponse;
import com.hellobike.finance.flow.engine.execute.FlowStatus;
import com.hellobike.finance.flow.engine.load.FlowLoader;
import com.hellobike.finance.flow.engine.model.Flow;
import com.hellobike.finance.flow.engine.model.node.FlowNode;
import com.hellobike.finance.flow.engine.spi.FlowServiceLoader;
import com.hellobike.finance.flow.engine.storage.FlowContextStorage;
import com.hellobike.finance.flow.engine.utils.GeneratorMermaidUtils;
import com.hellobike.finance.flow.engine.utils.PathMatchingPatternResolver;
import com.hellobike.finance.flow.engine.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 徐磊080
 */
public class FlowEngineExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(FlowEngineExecutor.class);

    private final FlowConfiguration configuration;
    private final PathMatchingPatternResolver pathMatchingPatternResolver;
    private final Map<String, Flow> FLOW_CACHE = new HashMap<>();
    private final Map<String, FlowDefinition> FLOW_DEFINITION_CACHE = new HashMap<>();

    public FlowEngineExecutor(FlowConfiguration configuration) throws FlowLoadException, FlowParseException, FlowBuildException {
        this.configuration = configuration;
        this.pathMatchingPatternResolver = new PathMatchingPatternResolver();
        loadDefinition();
        for (Map.Entry<String, FlowDefinition> entry : FLOW_DEFINITION_CACHE.entrySet()) {
            Flow flow = new Flow();
            flow.build(entry.getValue(), configuration);
            FLOW_CACHE.put(flow.getName(), flow);
            LOG.info("Build Flow [" + flow.getName() + "] Success!");
        }
    }

    /**
     * 发起流程调用
     *
     * @param flowName 流程名称
     * @param context 流程执行上下文
     * @return 流程执行结果
     */
    public FlowResponse execute(String flowName, FlowContext context) {
        if (StringUtils.isBlank(flowName)) {
            throw new IllegalArgumentException("流程名称不能为空");
        }
        Flow flow = FLOW_CACHE.get(flowName);
        if (flow == null) {
            throw new IllegalArgumentException("流程[" + flowName + "]不存在!");
        }
        FlowResponse response = flow.execute(flow.getOrigin(), context);
        storageContext(response, context);
        return response;
    }

    /**
     * 发起流程调用
     *
     * @param flowName 流程名称
     * @param startNodeName 指定流程起点
     * @param context 流程执行上下文
     * @return 流程执行结果
     */
    public FlowResponse execute(String flowName, String startNodeName, FlowContext context) {
        if (StringUtils.isBlank(flowName)) {
            throw new IllegalArgumentException("流程名称不能为空");
        }
        if (StringUtils.isBlank(startNodeName)) {
            throw new IllegalArgumentException("起始节点不能为空");
        }
        Flow flow = FLOW_CACHE.get(flowName);
        if (flow == null) {
            throw new IllegalArgumentException("流程[" + flowName + "]不存在!");
        }
        FlowNode startNode = flow.searchNode(startNodeName);
        if (startNode == null) {
            throw new IllegalArgumentException("流程[" + flowName + "]不存在[" + startNodeName + "]节点!");
        }
        FlowResponse response = flow.execute(startNode, context);
        storageContext(response, context);
        return response;
    }

    /**
     * 唤起流程继续执行，用于流程暂停后唤醒继续执行
     *
     * @param flowName 流程名称
     * @param suspendId 暂停id
     * @param resumeNodeName 暂停继续的节点名称
     * @return 流程执行结果
     */
    public FlowResponse resume(String flowName, String suspendId, String resumeNodeName) {
        if (StringUtils.isBlank(flowName)) {
            throw new IllegalArgumentException("流程名称不能为空");
        }
        if (StringUtils.isBlank(suspendId)) {
            throw new IllegalArgumentException("暂停ID不能为空");
        }
        FlowContext context = restoreContext(flowName, suspendId);
        return execute(flowName, resumeNodeName, context);
    }

    private void storageContext(FlowResponse response, FlowContext context) {
        if (!FlowStatus.SUSPEND.equals(response.getStatus())) {
            return;
        }
        if (response.getNodeResults().isEmpty()) {
            return;
        }
        String flowName = response.getFlowName();
        String suspendId = response.getNodeResults().get(response.getNodeResults().size() - 1).getSuspendId();
        if (StringUtils.isBlank(suspendId)) {
            LOG.error("the flow [{}] is in SUSPEND status, but suspendId is blank!", flowName);
            return;
        }
        try {
            FlowContextStorage storage = FlowServiceLoader.getLoader(FlowContextStorage.class).getExtension();
            LOG.info("try to storage flow [{}] context!, suspendNode:{}, suspendId:{}", flowName,
                    response.getCurrentFlowNodeName(), suspendId);
            storage.saveContext(flowName, suspendId, context);
        } catch (Exception e) {
            throw new FlowExecuteException("storage flow [" + flowName + "] context error!", e);
        }
    }

    private FlowContext restoreContext(String flowName, String suspendId) {
        try {
            FlowContextStorage storage = FlowServiceLoader.getLoader(FlowContextStorage.class).getExtension();
            LOG.info("try to restore flow [{}] context! suspendId:{}", flowName, suspendId);
            return storage.restoreContext(flowName, suspendId);
        } catch (Exception e) {
            throw new FlowExecuteException("storage flow [" + flowName + "] context error!", e);
        }
    }

    private void loadDefinition() throws FlowLoadException {
        File[] files;
        try {
            files = pathMatchingPatternResolver.getMatchFiles(configuration.getFlowDefinitionPath());
        } catch (FileNotFoundException e) {
            throw new FlowLoadException("获取流程定义文件异常!", e);
        }
        if (files == null || files.length == 0) {
            throw new FlowLoadException("无匹配的流程定义文件!");
        }
        Map<String, FlowLoader> flowLoaderMap = FlowServiceLoader.getLoader(FlowLoader.class).getExtensionList();
        for (File file : files) {
            String suffix = file.getName().substring(file.getName().lastIndexOf('.') + 1);
            FlowLoader loader = flowLoaderMap.get(suffix.toLowerCase());
            if (loader == null) {
                LOG.warn("[{}]文件类型不支持,过滤加载!", file.getName());
                continue;
            }
            try {
                byte[] content = Files.readAllBytes(file.toPath());
                FlowDefinition definition = loader.load(new String(content, StandardCharsets.UTF_8), configuration);
                FLOW_DEFINITION_CACHE.put(definition.getName(), definition);
                if (configuration.getAutoGeneratorFlowGraphFile()) {
                    GeneratorMermaidUtils.generatorMermaidFile(file, definition);
                }
            } catch (IOException e) {
                LOG.warn("读取[{}]文件内容异常!", file.getName());
            } catch (FlowLoadException loadException) {
                LOG.warn("加载[{}]文件内容异常!", file.getName());
            }
        }
    }
}
