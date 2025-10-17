package com.zero2me.breezeflow.tutorial.tasks;

import com.zero2me.breezeflow.core.Task;

/**
 * A simple task implementation for tutorial purposes.
 * This task demonstrates basic task functionality with configurable behavior.
 */
public class SimpleTask extends Task {
    
    private long delayMs = 2000; // Default delay
    
    // Package-private constructor - only TaskFactory can create instances
    public SimpleTask() {
        // No need to call super() explicitly
    }
    
    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }
    
    public long getDelayMs() {
        return delayMs;
    }
    
    @Override
    protected boolean preCheck() {
        return true;
    }
    
    @Override
    protected void invoke() {
        logger.info("SimpleTask {}: Starting execution", getId());
        
        // Simulate some work with configurable delay
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Task interrupted", e);
            }
        }
        
        logger.info("SimpleTask {}: Completed successfully", getId());
    }
}
