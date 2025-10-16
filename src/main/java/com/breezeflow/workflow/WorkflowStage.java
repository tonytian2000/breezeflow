package com.breezeflow.workflow;

import com.breezeflow.task.Task;
import java.util.Collections;
import java.util.List;

/**
 * Represents a stage in a workflow that contains tasks to be executed.
 * A stage can be either sequential or parallel.
 */
public class WorkflowStage {
    private final List<Task> tasks;
    private final ExecutionMode executionMode;
    
    public enum ExecutionMode {
        SEQUENTIAL, // Tasks run one after another
        PARALLEL    // Tasks run concurrently
    }
    
    private WorkflowStage(List<Task> tasks, ExecutionMode executionMode) {
        this.tasks = Collections.unmodifiableList(tasks);
        this.executionMode = executionMode;
    }
    
    /**
     * Creates a sequential stage where tasks run one after another.
     */
    public static WorkflowStage sequential(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException("Tasks list cannot be null or empty");
        }
        return new WorkflowStage(tasks, ExecutionMode.SEQUENTIAL);
    }
    
    /**
     * Creates a parallel stage where tasks run concurrently.
     */
    public static WorkflowStage parallel(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException("Tasks list cannot be null or empty");
        }
        return new WorkflowStage(tasks, ExecutionMode.PARALLEL);
    }
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    public ExecutionMode getExecutionMode() {
        return executionMode;
    }
    
    public boolean isParallel() {
        return executionMode == ExecutionMode.PARALLEL;
    }
    
    public boolean isSequential() {
        return executionMode == ExecutionMode.SEQUENTIAL;
    }
    
    @Override
    public String toString() {
        return String.format("WorkflowStage{mode=%s, tasks=%d}", executionMode, tasks.size());
    }
}