package com.hellobike.finance.flow.engine.springboot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 徐磊080
 */
@Getter
@Setter
@Component
@ConfigurationProperties("flow.engine")
public class FlowEngineProperties {

    /**
     * 流程定义文件路径,支持通配符匹配
     */
    private String definitionPath;

    /**
     * 自动生成流程图文件
     */
    private Boolean autoGeneratorFlowGraphFile;
}
