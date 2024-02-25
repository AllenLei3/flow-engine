package com.hellobike.finance.flow.engine.springboot;

import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 徐磊080
 */
@Configuration
@EnableConfigurationProperties(FlowEngineProperties.class)
public class FlowEnginePropertyAutoConfiguration {

    @Bean
    public FlowConfiguration flowConfiguration(FlowEngineProperties property) {
        FlowConfiguration configuration = new FlowConfiguration();
        configuration.setFlowDefinitionPath(property.getDefinitionPath());
        configuration.setAutoGeneratorFlowGraphFile(property.getAutoGeneratorFlowGraphFile());
        return configuration;
    }
}
