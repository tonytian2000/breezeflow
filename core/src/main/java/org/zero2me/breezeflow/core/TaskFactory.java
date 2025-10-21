package org.zero2me.breezeflow.core;

import org.zero2me.breezeflow.common.LogFactory;
import lombok.Getter;
import org.slf4j.Logger;

/**
 * Factory for creating task instances.
 * 
 * TaskFactory implements the factory pattern for creating Task instances.
 * It ensures that all tasks are properly initialized with the required
 * dependencies (facts, session context, session config, and listener).
 * 
 * This is the only way to create Task instances in the BreezeFlow system.
 */
public class TaskFactory {
    /**
     * Logger instance for this class.
     */
    private Logger logger = LogFactory.getLogger(TaskFactory.class);

    /**
     * Session configuration for tasks.
     */
    @Getter
    private SessionConfig sessionConfig;
    
    /**
     * Session context for tasks.
     */
    @Getter
    private SessionContext sessionContext;
    
    /**
     * Facts repository for tasks.
     */
    @Getter
    private Facts facts;
    
    /**
     * Workflow listener for tasks.
     */
    @Getter
    private WorkflowListener listener;

    /**
     * Package-private constructor - only Workflow can create TaskFactory instances.
     * This enforces the factory pattern for task creation.
     *
     * @param facts the facts repository
     * @param sessionContext the session context
     * @param sessionConfig the session configuration
     * @param listener the workflow listener
     */
    TaskFactory(Facts facts, SessionContext sessionContext,
                SessionConfig sessionConfig, WorkflowListener listener) {
        this.facts = facts;
        this.sessionContext = sessionContext;
        this.sessionConfig = sessionConfig;
        this.listener = listener;
    }

    /**
     * Builds a task with a custom name and returns it.
     * This is the only way to create Task instances.
     * 
     * @param taskClass the class of the task to create
     * @param name the name for the task
     * @return a new Task instance
     * @throws RuntimeException if task creation fails
     */
    public <T extends Task> T buildTask(Class<T> taskClass, String name) {
        T newTask = null;

        try {
            // Get the package-private constructor
            newTask = taskClass.getDeclaredConstructor().newInstance();
            newTask.setName(name);
            newTask.inject(facts, sessionContext, sessionConfig, listener);
            logger.debug("Successfully built task: {} of type: {}", name, taskClass.getSimpleName());
        } catch (Exception ex) {
            logger.error("Failed to build task: {} of type: {} with exception: {}", 
                        name, taskClass.getSimpleName(), ex.getMessage(), ex);
            throw new RuntimeException("Failed to create task: " + name, ex);
        }

        return newTask;
    }

}
