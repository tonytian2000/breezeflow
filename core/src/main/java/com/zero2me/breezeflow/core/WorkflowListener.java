package com.zero2me.breezeflow.core;

import org.slf4j.Logger;

import com.zero2me.breezeflow.common.LogFactory;

public abstract class WorkflowListener {
    private Logger logger = LogFactory.getLogger(WorkflowListener.class);
    
    public void notify(WorkflowEventType event, String description) {
        try {
            handle(event, description);
        } catch (Exception e) {
            logger.error("handle event {} failed: {}", event, description, e);
        }
    }

    protected abstract void handle(WorkflowEventType event, String description) throws Exception;
}
