package com.hellobike.finance.flow.engine.exception;

/**
 * 流程加载异常
 *
 * @author 徐磊080
 */
public class FlowLoadException extends Exception {

    public FlowLoadException() {
    }

    public FlowLoadException(String message) {
        super(message);
    }

    public FlowLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowLoadException(Throwable cause) {
        super(cause);
    }

    public FlowLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
