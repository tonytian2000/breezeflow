package com.breezeflow.workflow;

import com.breezeflow.task.TaskResult;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a workflow execution.
 */
public class WorkflowResult {
    private final String workflowId;
    private final boolean success;
    private final List<TaskResult> taskResults;
    private final Instant startTime;
    private final Instant endTime;
    private final String errorMessage;
    
    public WorkflowResult(String workflowId, boolean success, List<TaskResult> taskResults,
                         Instant startTime, Instant endTime, String errorMessage) {
        this.workflowId = workflowId;
        this.success = success;
        this.taskResults = Collections.unmodifiableList(taskResults);
        this.startTime = startTime;
        this.endTime = endTime;
        this.errorMessage = errorMessage;
    }
    
    public String getWorkflowId() {
        return workflowId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public List<TaskResult> getTaskResults() {
        return taskResults;
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
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Gets the number of successfully executed tasks.
     */
    public int getSuccessfulTaskCount() {
        return (int) taskResults.stream().mapToInt(result -> result.isSuccess() ? 1 : 0).sum();
    }
    
    /**
     * Gets the number of failed tasks.
     */
    public int getFailedTaskCount() {
        return taskResults.size() - getSuccessfulTaskCount();
    }
    
    @Override
    public String toString() {
        return String.format("WorkflowResult{workflowId='%s', success=%s, tasks=%d/%d successful, duration=%s}", 
                           workflowId, success, getSuccessfulTaskCount(), taskResults.size(), getDuration());
    }
}