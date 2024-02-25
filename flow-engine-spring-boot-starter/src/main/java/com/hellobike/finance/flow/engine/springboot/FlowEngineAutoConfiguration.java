package com.hellobike.finance.flow.engine.springboot;

import com.hellobike.finance.flow.engine.FlowEngineExecutor;
import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author 徐磊080
 */
@Configuration
@AutoConfigureAfter(FlowEnginePropertyAutoConfiguration.class)
@ConditionalOnBean(FlowConfiguration.class)
@ConditionalOnProperty(prefix = "flow.engine", name = "enable", havingValue = "true", matchIfMissing = true)
@Import(SpringAware.class)
public class FlowEngineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FlowEngineExecutor flowEngineExecutor(FlowConfiguration flowConfiguration, SpringAware springAware) {
        return new FlowEngineExecutor(flowConfiguration);
    }
}
