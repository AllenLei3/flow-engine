package com.hellobike.finance.flow.engine.spi;

/**
 * @author 徐磊080
 */
public class ExtensionSpiLoadStrategy implements SpiLoadStrategy {

    @Override
    public int getPriority() {
        return MIN_PRIORITY;
    }

    @Override
    public String directory() {
        return "META-INF/flow/extension/";
    }

    @Override
    public boolean overridden() {
        return true;
    }
}
