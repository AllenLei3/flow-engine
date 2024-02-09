package com.hellobike.finance.flow.engine.exception;

/**
 * 流程执行异常
 *
 * @author 徐磊080
 */
public class FlowExecuteException extends RuntimeException {

    public FlowExecuteException() {
    }

    public FlowExecuteException(String message) {
        super(message);
    }

    public FlowExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowExecuteException(Throwable cause) {
        super(cause);
    }

    public FlowExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
