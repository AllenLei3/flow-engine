package com.hellobike.finance.flow.engine.spi.load;

/**
 * @author 徐磊080
 */
public class InternalLoadStrategy implements LoadStrategy {

    @Override
    public int getPriority() {
        return MAX_PRIORITY;
    }

    @Override
    public String directory() {
        return "META-INF/flow/internal/";
    }
}
