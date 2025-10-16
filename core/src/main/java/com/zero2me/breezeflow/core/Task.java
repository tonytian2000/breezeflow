package com.zero2me.breezeflow.core;


import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
public abstract class Task {
    @Getter
    private String id;

    private String name = "TASK";

    protected Facts facts;
    protected SessionContext sessionContext;
    protected SessionConfig sessionConfig;

    public Task() {
        id = genId();
    }

    public Task(Facts facts, SessionContext sessionContext, SessionConfig sessionConfig) {
        id = genId();
        inject(facts, sessionContext, sessionConfig);
    }

    public void inject(Facts facts, SessionContext sessionContext, SessionConfig sessionConfig) {
        this.facts = facts;
        this.sessionContext = sessionContext;
        this.sessionConfig = sessionConfig;
    }

    private String genId() {
        return "task_" + UUID.randomUUID();
    }

    public final void run() {
        if (preCheck()) {
            invoke();
        }
    }

    protected abstract boolean preCheck();

    protected abstract void invoke();
}
