package com.breezeflow.task;

/**
 * Exception thrown when a task execution fails.
 */
public class TaskExecutionException extends Exception {
    
    public TaskExecutionException(String message) {
        super(message);
    }
    
    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}