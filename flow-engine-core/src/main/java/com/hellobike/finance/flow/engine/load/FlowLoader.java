package com.hellobike.finance.flow.engine.load;

import com.hellobike.finance.flow.engine.config.FlowConfiguration;
import com.hellobike.finance.flow.engine.exception.FlowLoadException;
import com.hellobike.finance.flow.engine.load.definition.FlowDefinition;

/**
 * @author xulei
 */
public interface FlowLoader {

    /**
     * 支持文件后缀
     */
    String supportFileSuffix();

    /**
     * 加载流程定义
     *
     * @param content 文件内容
     * @param configuration 流程全局配置
     */
    FlowDefinition load(String content, FlowConfiguration configuration) throws FlowLoadException;

}
