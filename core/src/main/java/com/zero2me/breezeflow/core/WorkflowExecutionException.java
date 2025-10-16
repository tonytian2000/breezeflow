package com.zero2me.breezeflow.core;

/**
 * Exception thrown when workflow execution fails.
 */
public class WorkflowExecutionException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new WorkflowExecutionException with the specified detail message.
     *
     * @param message the detail message
     */
    public WorkflowExecutionException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new WorkflowExecutionException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public WorkflowExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
