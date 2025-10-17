package com.zero2me.breezeflow.core;

import lombok.Data;
import lombok.Getter;

import java.util.UUID;
import org.slf4j.Logger;

import com.zero2me.breezeflow.common.LogFactory;

/**
 * Represents a workflow definition in BreezeFlow.
 */
@Data
public class Workflow {
    @Getter
    private String id;

    @Getter
    private SessionConfig sessionConfig = new SessionConfig();

    @Getter
    private SessionContext sessionContext = new SessionContext();

    @Getter
    private Facts facts = new Facts();

    @Getter
    private WorkflowListener listener = new DefaultWorkflowListener();

    protected SequentialContainer rootContainer;

    protected TaskFactory taskFactory;

    private Logger logger = LogFactory.getLogger(Workflow.class);

    public Workflow() {
        init();
    }

    public Workflow(WorkflowListener listener) {
        this.listener = listener;
        init();
    }

    private void init() {
        id = genId();
        taskFactory = new TaskFactory(facts, sessionContext, sessionConfig, listener);
        rootContainer = (SequentialContainer)taskFactory.buildTask(SequentialContainer.class, "root_container");
    }

    private String genId() {
        return "workflow_" + UUID.randomUUID();
    }

    /**
     * Gets the TaskFactory instance for this workflow.
     * This is the only way to create Task instances.
     * 
     * @return the TaskFactory instance
     */
    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public void addTask(Class<? extends Task> taskClass, String name) {
        Task newTask = taskFactory.buildTask(taskClass, name);
        rootContainer.addTask(newTask);
    }

    public void addTask(Task task) {
        rootContainer.addTask(task);
    }

    public void buildWorkflow() {
        //Leave to the subclass to build the workflow
    }

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
