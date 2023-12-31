package com.hellobike.finance.flow.engine.load;

import com.hellobike.finance.flow.engine.load.definition.FlowDefinition;
import com.hellobike.finance.flow.engine.utils.PathMatchingPatternResolver;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author xulei
 */
public class FlowLoaderTest {

    @Test
    public void flowLoaderTest() throws Exception {
        PathMatchingPatternResolver resolver = new PathMatchingPatternResolver();
        File[] files = resolver.getMatchFiles("flow/70004/loan.xml");
        for (File file : files) {
            FlowLoader flowLoader = FlowLoaderFactory.getFlowLoader(file.getName());
            byte[] content = Files.readAllBytes(file.toPath());
            FlowDefinition definition = flowLoader.load(new String(content, StandardCharsets.UTF_8), null);
            assert definition != null;
            assert definition.getName() != null;
            assert definition.getFlowNodeDefinitions() != null;
            assert definition.getFlowLineDefinitions() != null;
        }
    }
}
