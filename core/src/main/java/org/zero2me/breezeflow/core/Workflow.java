package org.zero2me.breezeflow.core;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.UUID;

import lombok.Setter;
import org.slf4j.Logger;

import org.zero2me.breezeflow.common.LogFactory;

/**
 * Represents a workflow definition in BreezeFlow.
 * 
 * A Workflow is the top-level container for a set of tasks that are executed
 * in a defined order. It provides the execution context, manages workflow state,
 * and coordinates task execution.
 * 
 * Workflows are typically extended by concrete implementations that define
 * specific task sequences in the buildWorkflow method.
 */
public class Workflow {
    /**
     * Unique identifier for this workflow instance.
     */
    @Getter
    private String id;

    /**
     * Configuration for the workflow session.
     */
    @Getter
    protected SessionConfig sessionConfig = new SessionConfig();

    /**
     * Context for storing and retrieving workflow variables.
     */
    @Getter
    protected SessionContext sessionContext = new SessionContext();

    /**
     * Repository for storing and retrieving workflow facts.
     */
    @Getter
    protected Facts facts = new Facts();

    /**
     * Listener for workflow events.
     */
    @Getter
    protected WorkflowListener listener = new DefaultWorkflowListener();

    /**
     * Root container for all tasks in this workflow.
     * All tasks are added to this container, which executes them sequentially.
     */
    @Getter
    protected SequentialContainer rootContainer;

    /**
     * Factory for creating task instances.
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected TaskFactory taskFactory;

    /**
     * Logger instance for this class.
     */
    private Logger logger = LogFactory.getLogger(Workflow.class);

    /**
     * Default constructor.
     * Creates a new workflow with default configuration and listener.
     */
    public Workflow() {
        init();
    }

    /**
     * Constructor with custom listener.
     * Creates a new workflow with default configuration and the specified listener.
     *
     * @param listener the workflow listener to use
     */
    public Workflow(WorkflowListener listener) {
        this.listener = listener;
        init();
    }

    /**
     * Initializes this workflow.
     * Creates a task factory and root container for tasks.
     */
    private void init() {
        id = genId();
        taskFactory = new TaskFactory(facts, sessionContext, sessionConfig, listener);
        rootContainer = (SequentialContainer)taskFactory.buildTask(SequentialContainer.class, "root_container");
        buildWorkflow();
    }

    /**
     * Generates a unique ID for this workflow.
     *
     * @return a unique workflow ID
     */
    private String genId() {
        return "workflow_" + UUID.randomUUID();
    }

    /**
     * Adds a task to this workflow.
     * Creates a new task of the specified class and adds it to the root container.
     *
     * @param taskClass the class of the task to create
     * @param name the name for the task
     * @return the created task instance
     */
    public Task addTask(Class<? extends Task> taskClass, String name) {
        Task newTask = taskFactory.buildTask(taskClass, name);
        rootContainer.addTask(newTask);

        return newTask;
    }

    /**
     * Builds a task without adding it to the workflow.
     * Creates a new task instance of the specified class with the given name.
     * This allows for manual task management and custom task container configurations.
     *
     * @param taskClass the class of the task to create
     * @param name the name for the task
     * @return the created task instance
     */
    public Task buildTask(Class<? extends Task> taskClass, String name) {
        Task newTask = taskFactory.buildTask(taskClass, name);
        return newTask;
    }

    /**
     * Adds an existing task to this workflow.
     * Adds the provided task instance to the root container.
     * This method is useful when you have already created a task using buildTask
     * or need to add a pre-configured task instance.
     *
     * @param task the task instance to add to the workflow
     */
    public void addTask(Task task) {
        rootContainer.addTask(task);
    }

    /**
     * Builds the workflow by adding tasks.
     * This method is intended to be overridden by subclasses to define
     * the specific task sequence for the workflow.
     */
    protected void buildWorkflow() {
        //Leave to the subclass to build the workflow
    }

    /**
     * Executes this workflow.
     * Runs all tasks in the root container and handles any exceptions.
     *
     * @throws WorkflowExecutionException if workflow execution fails
     */
    public void run() throws WorkflowExecutionException {
        logger.info("workflow {} started", id);
        listener.notify(WorkflowEventType.WORKFLOW_STARTED, String.format("Workflow %s started", id));
            
        try {
            rootContainer.run();
        } catch (Exception e) {
            logger.error("workflow {} failed: {}", id, e.getMessage());
            listener.notify(WorkflowEventType.WORKFLOW_FAILED, String.format("Workflow %s failed: %s", id, e.getMessage()));
            throw new WorkflowExecutionException(String.format("Workflow %s failed: %s", id, e.getMessage()), e);
        }

        listener.notify(WorkflowEventType.WORKFLOW_COMPLETED, String.format("Workflow %s completed", id));
        logger.info("workflow {} completed", id);
    }
}
