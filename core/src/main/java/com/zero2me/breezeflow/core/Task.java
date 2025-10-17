package com.zero2me.breezeflow.core;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import org.slf4j.Logger;

import com.zero2me.breezeflow.common.LogFactory;

@Data
public abstract class Task {
    protected Logger logger = LogFactory.getLogger(Task.class);
    
    @Getter
    private String id;

    @Setter
    @Getter
    private String name = "TASK";
    
    protected Facts facts;
    protected SessionContext sessionContext;
    protected SessionConfig sessionConfig;
    protected WorkflowListener listener;

    // Protected constructor - only TaskFactory and subclasses can create Task instances
    protected Task() {
        id = genId();
    }

    // Protected constructor - only TaskFactory and subclasses can create Task instances
    protected Task(Facts facts, SessionContext sessionContext,
                   SessionConfig sessionConfig, WorkflowListener listener) {
        id = genId();
        inject(facts, sessionContext, sessionConfig, listener);
    }

    public void inject(Facts facts, SessionContext sessionContext, SessionConfig sessionConfig, WorkflowListener listener) {
        this.facts = facts;
        this.sessionContext = sessionContext;
        this.sessionConfig = sessionConfig;
        this.listener = listener;
    }

    private String genId() {
        return "task_" + UUID.randomUUID();
    }

    public final void run() throws WorkflowExecutionException {
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

    protected abstract boolean preCheck();

    protected abstract void invoke();
}
