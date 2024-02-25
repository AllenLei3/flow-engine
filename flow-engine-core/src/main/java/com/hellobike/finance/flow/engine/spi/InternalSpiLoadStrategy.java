package com.hellobike.finance.flow.engine.spi;

/**
 * @author 徐磊080
 */
public class InternalSpiLoadStrategy implements SpiLoadStrategy {

    @Override
    public int getPriority() {
        return MAX_PRIORITY;
    }

    @Override
    public String directory() {
        return "META-INF/flow/internal/";
    }
}
