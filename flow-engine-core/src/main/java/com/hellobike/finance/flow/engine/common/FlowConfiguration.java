package com.hellobike.finance.flow.engine.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 徐磊080
 */
@Getter
@Setter
public class FlowConfiguration {

    /**
     * 流程定义文件路径,支持通配符匹配
     */
    private String flowDefinitionPath;

    /**
     * 自动生成流程图文件
     */
    private Boolean autoGeneratorFlowGraphFile = true;
}
