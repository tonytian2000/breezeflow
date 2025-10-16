package com.zero2me.breezeflow.core;

import lombok.Data;
import lombok.Getter;

import java.util.UUID;

/**
 * Represents a workflow definition in BreezeFlow.
 */
@Data
public class Workflow {
    @Getter
    private String id;

    private SessionConfig sessionConfig = new SessionConfig();
    private SessionContext sessionContext = new SessionContext();
    private Facts facts = new Facts();

    private WorkflowListener listener;

    private SequentialContainer rootContainer;
    private TaskFactory taskFactory;

    public Workflow() {
        init();
    }

    private void init() {
        id = genId();
        taskFactory = new TaskFactory(facts, sessionContext, sessionConfig);
        rootContainer = (SequentialContainer)taskFactory.buildTask(SequentialContainer.class, "root_container");
    }

    private String genId() {
        return "workflow_" + UUID.randomUUID();
    }

    public void addTask(Class<? extends Task> taskClass, String name) {
        Task newTask = taskFactory.buildTask(taskClass, name);
        rootContainer.addTask(newTask);
    }

    public void run() {
        rootContainer.run();
    }

    public void setListener(WorkflowListener listener) {
        this.listener = listener;
    }
}
