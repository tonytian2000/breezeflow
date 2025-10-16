package com.breezeflow.task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleTaskTest {
    
    @Test
    public void testSimpleTaskExecution() throws TaskExecutionException {
        SimpleTask task = SimpleTask.of("Test Task", "Hello World");
        
        TaskResult result = task.execute();
        
        assertTrue(result.isSuccess());
        assertEquals("Hello World", result.getData());
        assertEquals(task.getId(), result.getTaskId());
        assertNotNull(result.getStartTime());
        assertNotNull(result.getEndTime());
        assertTrue(result.getEndTime().isAfter(result.getStartTime()) || 
                  result.getEndTime().equals(result.getStartTime()));
    }
    
    @Test
    public void testTaskWithDelay() throws TaskExecutionException {
        SimpleTask task = SimpleTask.withDelay("Delay Task", 100, "Delayed Result");
        
        long startTime = System.currentTimeMillis();
        TaskResult result = task.execute();
        long endTime = System.currentTimeMillis();
        
        assertTrue(result.isSuccess());
        assertEquals("Delayed Result", result.getData());
        assertTrue(endTime - startTime >= 100); // Should take at least 100ms
    }
    
    @Test
    public void testTaskWithException() throws TaskExecutionException {
        SimpleTask task = new SimpleTask("Exception Task", () -> {
            throw new RuntimeException("Test exception");
        });
        
        TaskResult result = task.execute();
        
        assertFalse(result.isSuccess());
        assertNull(result.getData());
        assertTrue(result.getErrorMessage().contains("Test exception"));
    }
    
    @Test
    public void testTaskProperties() {
        SimpleTask task = new SimpleTask("custom-id", "Custom Task", () -> "result");
        
        assertEquals("custom-id", task.getId());
        assertEquals("Custom Task", task.getName());
    }
}