package com.hellobike.finance.flow.engine.exception;

/**
 * 流程解析异常
 *
 * @author 徐磊080
 */
public class FlowParseException extends Exception {

    public FlowParseException() {
    }

    public FlowParseException(String message) {
        super(message);
    }

    public FlowParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowParseException(Throwable cause) {
        super(cause);
    }

    public FlowParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
