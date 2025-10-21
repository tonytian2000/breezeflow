package org.zero2me.breezeflow.core;

/**
 * Enumeration of workflow event types that can occur during workflow execution.
 * These events are used to notify listeners about the progress and state of workflows and tasks.
 */
public enum WorkflowEventType {
    /**
     * Event fired when a workflow starts execution.
     */
    WORKFLOW_STARTED,
    
    /**
     * Event fired when a workflow completes successfully.
     */
    WORKFLOW_COMPLETED,
    
    /**
     * Event fired when a workflow fails during execution.
     */
    WORKFLOW_FAILED,
    
    /**
     * Event fired when a task starts execution.
     */
    TASK_STARTED,
    
    /**
     * Event fired when a task completes successfully.
     */
    TASK_COMPLETED,
    
    /**
     * Event fired when a task fails during execution.
     */
    TASK_FAILED,
    
    /**
     * Event fired when a workflow or task is terminated prematurely.
     */
    TERMINATION
}
