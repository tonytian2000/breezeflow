package com.breezeflow.core;

import com.breezeflow.task.SimpleTask;
import com.breezeflow.task.TaskResult;
import com.breezeflow.workflow.Workflow;
import com.breezeflow.workflow.WorkflowResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class WorkflowEngineTest {
    
    private WorkflowEngine engine;
    
    @BeforeEach
    public void setUp() {
        engine = new WorkflowEngine();
    }
    
    @AfterEach
    public void tearDown() {
        engine.shutdown();
    }
    
    @Test
    public void testSequentialExecution() {
        Workflow workflow = new Workflow("seq-wf", "Sequential Workflow");
        workflow.addSequentialTasks(Arrays.asList(
            SimpleTask.withDelay("Task 1", 100, "result1"),
            SimpleTask.withDelay("Task 2", 100, "result2"),
            SimpleTask.withDelay("Task 3", 100, "result3")
        ));
        
        long startTime = System.currentTimeMillis();
        WorkflowResult result = engine.execute(workflow);
        long endTime = System.currentTimeMillis();
        
        assertTrue(result.isSuccess());
        assertEquals(3, result.getTaskResults().size());
        assertEquals(3, result.getSuccessfulTaskCount());
        assertEquals(0, result.getFailedTaskCount());
        
        // Sequential execution should take at least 300ms
        assertTrue(endTime - startTime >= 300);
        
        // Check results are in order
        List<TaskResult> taskResults = result.getTaskResults();
        assertEquals("result1", taskResults.get(0).getData());
        assertEquals("result2", taskResults.get(1).getData());
        assertEquals("result3", taskResults.get(2).getData());
    }
    
    @Test
    public void testParallelExecution() {
        Workflow workflow = new Workflow("par-wf", "Parallel Workflow");
        workflow.addParallelTasks(Arrays.asList(
            SimpleTask.withDelay("Task 1", 200, "result1"),
            SimpleTask.withDelay("Task 2", 200, "result2"),
            SimpleTask.withDelay("Task 3", 200, "result3")
        ));
        
        long startTime = System.currentTimeMillis();
        WorkflowResult result = engine.execute(workflow);
        long endTime = System.currentTimeMillis();
        
        assertTrue(result.isSuccess());
        assertEquals(3, result.getTaskResults().size());
        assertEquals(3, result.getSuccessfulTaskCount());
        assertEquals(0, result.getFailedTaskCount());
        
        // Parallel execution should take less than 600ms (much closer to 200ms)
        assertTrue(endTime - startTime < 600);
        assertTrue(endTime - startTime >= 200);
    }
    
    @Test
    public void testMixedWorkflow() {
        Workflow workflow = new Workflow("mixed-wf", "Mixed Workflow");
        
        // Sequential initialization
        workflow.addTask(SimpleTask.of("Init", "initialized"));
        
        // Parallel processing
        workflow.addParallelTasks(Arrays.asList(
            SimpleTask.withDelay("Process A", 100, "processedA"),
            SimpleTask.withDelay("Process B", 100, "processedB")
        ));
        
        // Sequential cleanup
        workflow.addTask(SimpleTask.of("Cleanup", "cleaned"));
        
        WorkflowResult result = engine.execute(workflow);
        
        assertTrue(result.isSuccess());
        assertEquals(4, result.getTaskResults().size());
        assertEquals(4, result.getSuccessfulTaskCount());
        assertEquals(0, result.getFailedTaskCount());
        
        // Check execution order - first and last should be in specific order
        List<TaskResult> taskResults = result.getTaskResults();
        assertEquals("initialized", taskResults.get(0).getData());
        assertEquals("cleaned", taskResults.get(3).getData());
        
        // Middle two can be in any order due to parallel execution
        boolean foundA = false, foundB = false;
        for (int i = 1; i <= 2; i++) {
            Object data = taskResults.get(i).getData();
            if ("processedA".equals(data)) foundA = true;
            if ("processedB".equals(data)) foundB = true;
        }
        assertTrue(foundA && foundB);
    }
    
    @Test
    public void testFailureInSequentialStage() {
        Workflow workflow = new Workflow("fail-seq-wf", "Failing Sequential Workflow");
        workflow.addSequentialTasks(Arrays.asList(
            SimpleTask.of("Task 1", "result1"),
            new SimpleTask("Failing Task", () -> {
                throw new RuntimeException("Task failed");
            }),
            SimpleTask.of("Task 3", "result3") // This should not execute
        ));
        
        WorkflowResult result = engine.execute(workflow);
        
        assertFalse(result.isSuccess());
        assertEquals(2, result.getTaskResults().size()); // Should stop after failure
        assertEquals(1, result.getSuccessfulTaskCount());
        assertEquals(1, result.getFailedTaskCount());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    public void testFailureInParallelStage() {
        Workflow workflow = new Workflow("fail-par-wf", "Failing Parallel Workflow");
        workflow.addParallelTasks(Arrays.asList(
            SimpleTask.of("Task 1", "result1"),
            new SimpleTask("Failing Task", () -> {
                throw new RuntimeException("Task failed");
            }),
            SimpleTask.of("Task 3", "result3")
        ));
        
        WorkflowResult result = engine.execute(workflow);
        
        assertFalse(result.isSuccess());
        assertEquals(3, result.getTaskResults().size()); // All tasks should execute
        assertEquals(2, result.getSuccessfulTaskCount());
        assertEquals(1, result.getFailedTaskCount());
    }
    
    @Test
    public void testCustomExecutorService() {
        WorkflowEngine customEngine = new WorkflowEngine(Executors.newFixedThreadPool(2));
        
        Workflow workflow = new Workflow("custom-wf", "Custom Executor Workflow");
        workflow.addParallelTasks(Arrays.asList(
            SimpleTask.of("Task 1", "result1"),
            SimpleTask.of("Task 2", "result2")
        ));
        
        WorkflowResult result = customEngine.execute(workflow);
        
        assertTrue(result.isSuccess());
        assertEquals(2, result.getTaskResults().size());
        
        // Don't shutdown the custom engine since we don't own the executor
    }
    
    @Test
    public void testEmptyWorkflow() {
        Workflow workflow = new Workflow("empty-wf", "Empty Workflow");
        
        WorkflowResult result = engine.execute(workflow);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getTaskResults().isEmpty());
        assertEquals(0, result.getSuccessfulTaskCount());
        assertEquals(0, result.getFailedTaskCount());
    }
}