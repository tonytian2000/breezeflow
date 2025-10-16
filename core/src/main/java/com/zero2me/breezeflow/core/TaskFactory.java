package com.zero2me.breezeflow.core;

import com.zero2me.breezeflow.common.LogFactory;
import org.slf4j.Logger;

public class TaskFactory {
    private Logger logger = LogFactory.getLogger(TaskFactory.class);

    private SessionConfig sessionConfig;
    private SessionContext sessionContext;
    private Facts facts;

    public TaskFactory(Facts facts, SessionContext sessionContext, SessionConfig sessionConfig) {
        this.facts = facts;
        this.sessionContext = sessionContext;
        this.sessionConfig = sessionConfig;
    }

    public Task buildTask(Class<? extends Task> taskClass, String name) {
        Task newTask = null;

        try {
            newTask = taskClass.getDeclaredConstructor().newInstance();
            newTask.setName(name);
            newTask.inject(facts, sessionContext, sessionConfig);
        } catch (Exception ex) {
            logger.error("build task failed with exception: " + ex);
        }

        return newTask;
    }
}
