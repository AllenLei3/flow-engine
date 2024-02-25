package com.hellobike.finance.flow.engine.spi.load;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.exception.FlowLoadException;
import com.hellobike.finance.flow.engine.definition.FlowDefinition;
import com.hellobike.finance.flow.engine.spi.SPI;

/**
 * @author 徐磊080
 */
@SPI
public interface FlowDefinitionLoader {

    String NODES = "nodes";
    String NODE = "node";
    String NAME = "name";
    String CLASS_NAME = "className";
    String TYPE = "type";
    String DESCRIPTION = "description";
    String LINES = "lines";
    String LINE = "line";
    String SOURCE_NODE_NAME = "sourceNodeName";
    String TARGET_NODE_NAME = "targetNodeName";
    String SWITCH_VALUE = "switchValue";
    String DEFAULT_LINE = "defaultLine";

    /**
     * 加载流程定义文件
     *
     * @param content 文件内容
     * @param configuration 流程全局配置
     */
    FlowDefinition load(String content, FlowConfiguration configuration) throws FlowLoadException;

}
