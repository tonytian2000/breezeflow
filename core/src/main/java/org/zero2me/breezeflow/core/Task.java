package org.zero2me.breezeflow.core;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import org.slf4j.Logger;

import org.zero2me.breezeflow.common.LogFactory;

/**
 * Abstract base class for all workflow tasks.
 * 
 * Task is the fundamental unit of work in the BreezeFlow workflow system.
 * It defines the core lifecycle and execution model for all tasks, including
 * pre-execution checks, execution, and post-execution handling.
 * 
 * All concrete task implementations must extend this class and implement
 * the abstract methods to provide specific behavior.
 */
public abstract class Task {
    /**
     * Logger instance for this class.
     */
    protected Logger logger = LogFactory.getLogger(Task.class);
    
    private final String SESSION_TERMINATED = "SYS_SESSION_TERMINATED";

    /**
     * Unique identifier for this task instance.
     */
    @Getter
    private String id;

    /**
     * Name of this task, used for logging and identification.
     * Default value is "TASK".
     */
    @Getter
    @Setter
    private String name = "TASK";
    
    /**
     * Facts repository for storing and retrieving workflow facts.
     */
    protected Facts facts;
    
    /**
     * Session context for storing and retrieving workflow variables.
     */
    protected SessionContext sessionContext;
    
    /**
     * Session configuration for workflow execution.
     */
    protected SessionConfig sessionConfig;
    
    /**
     * Workflow listener for event notifications.
     */
    protected WorkflowListener listener;

    /**
     * Protected constructor - only TaskFactory and subclasses can create Task instances.
     * This enforces the factory pattern for task creation.
     */
    protected Task() {
        id = genId();
    }

    /**
     * Protected constructor with dependencies - only TaskFactory and subclasses can create Task instances.
     * This constructor initializes the task with all required dependencies.
     *
     * @param facts the facts repository
     * @param sessionContext the session context
     * @param sessionConfig the session configuration
     * @param listener the workflow listener
     */
    protected Task(Facts facts, SessionContext sessionContext,
                   SessionConfig sessionConfig, WorkflowListener listener) {
        id = genId();
        inject(facts, sessionContext, sessionConfig, listener);
    }

    /**
     * Injects dependencies into this task.
     * This method is called by the TaskFactory to provide the task with
     * access to shared workflow resources.
     *
     * @param facts the facts repository
     * @param sessionContext the session context
     * @param sessionConfig the session configuration
     * @param listener the workflow listener
     */
    public void inject(Facts facts, SessionContext sessionContext, SessionConfig sessionConfig, WorkflowListener listener) {
        this.facts = facts;
        this.sessionContext = sessionContext;
        this.sessionConfig = sessionConfig;
        this.listener = listener;
    }

    /**
     * Generates a unique ID for this task.
     *
     * @return a unique task ID
     */
    private String genId() {
        return "task_" + UUID.randomUUID();
    }

    /**
     * Executes this task.
     * This method implements the template method pattern, defining the
     * standard lifecycle of a task:
     * 1. Pre-execution check
     * 2. Execution
     * 3. Post-execution handling
     * 
     * Subclasses should not override this method, but instead implement
     * the abstract methods preCheck() and invoke().
     *
     * @throws WorkflowExecutionException if task execution fails
     */
    public final void run() throws WorkflowExecutionException {
        Object terminated = sessionContext.getVariable(SESSION_TERMINATED);
        if (terminated != null && (boolean)terminated) {
            logger.info("task {}:{} skipped due to session termination", id, name);
            return;
        }

        if (preCheck()) {
            logger.info("task {}:{} start", id, name);
            listener.notify(WorkflowEventType.TASK_STARTED, String.format("Task %s:%s started", id, name));
            try {
                invoke();
            } catch (Exception e) {
                logger.error("task {}:{} failed: {}", id, name, e.getMessage());
                listener.notify(WorkflowEventType.TASK_FAILED, String.format("Task %s:%s failed: %s", id, name, e.getMessage()));
                throw new WorkflowExecutionException(String.format("Task %s:%s failed: %s", id, name, e.getMessage()));
            }
            listener.notify(WorkflowEventType.TASK_COMPLETED, String.format("Task %s:%s completed", id, name));
            logger.info("task {}:{} completed", id, name);   
        } else {
            logger.error("task {}:{} pre check failed", id, name);
        }
    }

    /**
     * Performs pre-execution checks to determine if this task can be executed.
     * Implementations should check any preconditions required for task execution,
     * such as the presence of required facts or variables.
     *
     * @return true if the task can be executed, false otherwise
     */
    protected abstract boolean preCheck();

    /**
     * Implements the actual task execution logic.
     * This method is called by run() after preCheck() returns true.
     * Implementations should perform the actual work of the task here.
     */
    protected abstract void invoke();

    /**
     * Gracefully terminates the current workflow session.
     */
    protected void terminate() {
        listener.notify(WorkflowEventType.TERMINATION, String.format("Task %s:%s terminated", id, name));
        logger.info("task {}:{} terminated", id, name);
        sessionContext.setVariable(SESSION_TERMINATED, true);
    }
}
