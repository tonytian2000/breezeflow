package com.breezeflow.task;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents the result of a task execution.
 */
public class TaskResult {
    private final String taskId;
    private final boolean success;
    private final Object data;
    private final String errorMessage;
    private final Instant startTime;
    private final Instant endTime;
    
    private TaskResult(String taskId, boolean success, Object data, String errorMessage, 
                      Instant startTime, Instant endTime) {
        this.taskId = taskId;
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    /**
     * Creates a successful task result.
     */
    public static TaskResult success(String taskId, Object data, Instant startTime, Instant endTime) {
        return new TaskResult(taskId, true, data, null, startTime, endTime);
    }
    
    /**
     * Creates a failed task result.
     */
    public static TaskResult failure(String taskId, String errorMessage, Instant startTime, Instant endTime) {
        return new TaskResult(taskId, false, null, errorMessage, startTime, endTime);
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public Object getData() {
        return data;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public Instant getStartTime() {
        return startTime;
    }
    
    public Instant getEndTime() {
        return endTime;
    }
    
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }
    
    @Override
    public String toString() {
        return String.format("TaskResult{taskId='%s', success=%s, duration=%s}", 
                           taskId, success, getDuration());
    }
}