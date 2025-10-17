package com.zero2me.breezeflow.core;

import com.zero2me.breezeflow.common.LogFactory;
import lombok.Data;
import org.slf4j.Logger;

@Data
public class TaskFactory {
    private Logger logger = LogFactory.getLogger(TaskFactory.class);

    private SessionConfig sessionConfig;
    private SessionContext sessionContext;
    private Facts facts;
    private WorkflowListener listener;

    // Package-private constructor - only Workflow can create TaskFactory instances
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
