package org.zero2me.breezeflow.core;

import org.slf4j.Logger;

import org.zero2me.breezeflow.common.LogFactory;

/**
 * Abstract base class for workflow event listeners.
 * 
 * WorkflowListener provides a mechanism for reacting to workflow events such as
 * workflow start/completion, task execution, and failures. Implementations can
 * provide custom handling for these events, such as logging, monitoring, or
 * triggering external systems.
 */
public abstract class WorkflowListener {
    /**
     * Logger instance for this class.
     */
    private Logger logger = LogFactory.getLogger(WorkflowListener.class);
    
    /**
     * Notifies the listener of a workflow event.
     * This method safely calls the abstract handle method and catches any exceptions.
     * 
     * @param event the type of workflow event
     * @param description a description of the event
     */
    public void notify(WorkflowEventType event, String description) {
        try {
            handle(event, description);
        } catch (Exception e) {
            logger.error("handle event {} failed: {}", event, description, e);
        }
    }

    /**
     * Handles a workflow event.
     * Implementations should provide specific behavior for different event types.
     * 
     * @param event the type of workflow event
     * @param description a description of the event
     * @throws Exception if an error occurs while handling the event
     */
    protected abstract void handle(WorkflowEventType event, String description) throws Exception;
}
