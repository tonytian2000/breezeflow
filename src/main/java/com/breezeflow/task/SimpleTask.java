package com.breezeflow.task;

import java.util.function.Supplier;

/**
 * A simple task implementation that executes a provided function.
 */
public class SimpleTask extends AbstractTask {
    private final Supplier<Object> taskFunction;
    
    public SimpleTask(String name, Supplier<Object> taskFunction) {
        super(name);
        this.taskFunction = taskFunction;
    }
    
    public SimpleTask(String id, String name, Supplier<Object> taskFunction) {
        super(id, name);
        this.taskFunction = taskFunction;
    }
    
    @Override
    protected Object doExecute() throws Exception {
        return taskFunction.get();
    }
    
    /**
     * Creates a simple task that returns a string result.
     */
    public static SimpleTask of(String name, String result) {
        return new SimpleTask(name, () -> result);
    }
    
    /**
     * Creates a simple task that sleeps for the specified duration and returns a result.
     */
    public static SimpleTask withDelay(String name, long delayMs, String result) {
        return new SimpleTask(name, () -> {
            try {
                Thread.sleep(delayMs);
                return result;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Task interrupted", e);
            }
        });
    }
}