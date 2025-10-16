package com.breezeflow.task;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for tasks that provides common functionality.
 */
public abstract class AbstractTask implements Task {
    private final String id;
    private final String name;
    
    protected AbstractTask(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
    
    protected AbstractTask(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
    public final TaskResult execute() throws TaskExecutionException {
        Instant startTime = Instant.now();
        try {
            Object result = doExecute();
            Instant endTime = Instant.now();
            return TaskResult.success(id, result, startTime, endTime);
        } catch (Exception e) {
            Instant endTime = Instant.now();
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return TaskResult.failure(id, errorMessage, startTime, endTime);
        }
    }
    
    /**
     * Subclasses must implement this method to define their specific execution logic.
     * 
     * @return The result of the task execution
     * @throws Exception if the task execution fails
     */
    protected abstract Object doExecute() throws Exception;
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return String.format("%s[id=%s, name='%s']", getClass().getSimpleName(), id, name);
    }
}