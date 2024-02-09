package com.hellobike.finance.flow.engine.exception;

/**
 * 流程构建异常
 *
 * @author 徐磊080
 */
public class FlowBuildException extends RuntimeException {

    public FlowBuildException() {
    }

    public FlowBuildException(String message) {
        super(message);
    }

    public FlowBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowBuildException(Throwable cause) {
        super(cause);
    }

    public FlowBuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
