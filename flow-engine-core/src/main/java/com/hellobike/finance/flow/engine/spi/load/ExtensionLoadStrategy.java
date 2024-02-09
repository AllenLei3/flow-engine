package com.hellobike.finance.flow.engine.spi.load;

/**
 * @author 徐磊080
 */
public class ExtensionLoadStrategy implements LoadStrategy {

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
