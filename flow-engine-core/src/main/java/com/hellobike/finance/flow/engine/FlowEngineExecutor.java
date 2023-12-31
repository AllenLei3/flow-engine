package com.hellobike.finance.flow.engine;

import com.hellobike.finance.flow.engine.config.FlowConfiguration;
import com.hellobike.finance.flow.engine.model.Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xulei
 */
public class FlowEngineExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowEngineExecutor.class);

    private final FlowConfiguration configuration;
    private final Map<String, Flow> FLOW_CACHE = new HashMap<>();

    public FlowEngineExecutor(FlowConfiguration configuration) {
        this.configuration = configuration;
    }


}
