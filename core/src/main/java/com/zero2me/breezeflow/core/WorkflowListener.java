package com.zero2me.breezeflow.core;


public abstract class WorkflowListener {
    public abstract void notify(WorkflowEventTypeEnum event, String description);
}
