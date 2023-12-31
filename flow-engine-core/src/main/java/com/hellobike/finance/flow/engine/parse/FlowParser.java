package com.hellobike.finance.flow.engine.parse;

import com.hellobike.finance.flow.engine.config.FlowConfiguration;
import com.hellobike.finance.flow.engine.load.definition.FlowDefinition;
import com.hellobike.finance.flow.engine.model.Flow;

/**
 * @author xulei
 */
public interface FlowParser {

    /**
     * 将流程定义解析为流程实体
     *
     * @param definition 流程定义
     * @param configuration 流程全局配置
     * @return 流程实体
     */
    Flow parse(FlowDefinition definition, FlowConfiguration configuration);

}
