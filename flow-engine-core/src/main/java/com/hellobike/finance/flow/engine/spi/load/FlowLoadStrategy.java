package com.hellobike.finance.flow.engine.spi.load;

/**
 * @author 徐磊080
 */
public class FlowLoadStrategy implements LoadStrategy {

    @Override
    public String directory() {
        return "META-INF/flow/";
    }

    @Override
    public boolean overridden() {
        return true;
    }
}
