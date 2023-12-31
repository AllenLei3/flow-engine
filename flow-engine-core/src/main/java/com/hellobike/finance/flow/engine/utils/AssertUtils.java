package com.hellobike.finance.flow.engine.utils;

/**
 * @author xulei
 */
public class AssertUtils {

    public static void notNull(boolean expression, String error) {
        if (expression) {
            throw new IllegalArgumentException(error);
        }
    }
}
