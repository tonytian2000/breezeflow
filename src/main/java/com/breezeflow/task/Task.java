package com.breezeflow.task;

/**
 * Represents a unit of work that can be executed in a workflow.
 * Tasks can be executed sequentially or in parallel depending on the workflow configuration.
 */
public interface Task {
    
    /**
     * Executes the task and returns the result.
     * 
     * @return The result of task execution
     * @throws TaskExecutionException if task execution fails
     */
    TaskResult execute() throws TaskExecutionException;
    
    /**
     * Gets the unique identifier for this task.
     * 
     * @return The task ID
     */
    String getId();
    
    /**
     * Gets the human-readable name of this task.
     * 
     * @return The task name
     */
    String getName();
}