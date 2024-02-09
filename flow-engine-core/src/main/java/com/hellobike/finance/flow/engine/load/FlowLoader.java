package com.hellobike.finance.flow.engine.load;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.exception.FlowLoadException;
import com.hellobike.finance.flow.engine.definition.FlowDefinition;
import com.hellobike.finance.flow.engine.spi.SPI;

/**
 * @author 徐磊080
 */
@SPI
public interface FlowLoader {

    /**
     * 加载流程定义文件
     *
     * @param content 文件内容
     * @param configuration 流程全局配置
     */
    FlowDefinition load(String content, FlowConfiguration configuration) throws FlowLoadException;

}
