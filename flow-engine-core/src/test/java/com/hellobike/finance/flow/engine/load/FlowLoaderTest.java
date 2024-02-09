package com.hellobike.finance.flow.engine.load;

import com.hellobike.finance.flow.engine.FlowEngineExecutor;
import com.hellobike.finance.flow.engine.common.FlowConfiguration;
import com.hellobike.finance.flow.engine.execute.FlowResponse;
import org.junit.jupiter.api.Test;

/**
 * @author 徐磊080
 */
public class FlowLoaderTest {

    @Test
    public void flowLoaderTest() throws Exception {
        FlowConfiguration configuration = new FlowConfiguration();
        configuration.setFlowDefinitionPath("flow/70004/loan.xml");
        FlowEngineExecutor engineExecutor = new FlowEngineExecutor(configuration);

        LoanContext context = new LoanContext();
        FlowResponse response = engineExecutor.execute("70001LoanFlow", context);
        System.out.println(response);


        FlowResponse response1 = engineExecutor.resume("70001LoanFlow", "123", "E");
        System.out.println(response1);
    }
}
