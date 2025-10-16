package com.breezeflow.workflow;

import com.breezeflow.task.SimpleTask;
import com.breezeflow.task.Task;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorkflowTest {
    
    @Test
    public void testWorkflowCreation() {
        Workflow workflow = new Workflow("wf-1", "Test Workflow");
        
        assertEquals("wf-1", workflow.getId());
        assertEquals("Test Workflow", workflow.getName());
        assertTrue(workflow.getStages().isEmpty());
        assertTrue(workflow.getAllTasks().isEmpty());
    }
    
    @Test
    public void testAddSingleTask() {
        Workflow workflow = new Workflow("wf-1", "Test Workflow");
        Task task = SimpleTask.of("Task 1", "result1");
        
        workflow.addTask(task);
        
        assertEquals(1, workflow.getStages().size());
        assertEquals(1, workflow.getAllTasks().size());
        assertTrue(workflow.getStages().get(0).isSequential());
        assertEquals(task, workflow.getAllTasks().get(0));
    }
    
    @Test
    public void testAddSequentialTasks() {
        Workflow workflow = new Workflow("wf-1", "Test Workflow");
        List<Task> tasks = Arrays.asList(
            SimpleTask.of("Task 1", "result1"),
            SimpleTask.of("Task 2", "result2")
        );
        
        workflow.addSequentialTasks(tasks);
        
        assertEquals(1, workflow.getStages().size());
        assertEquals(2, workflow.getAllTasks().size());
        assertTrue(workflow.getStages().get(0).isSequential());
    }
    
    @Test
    public void testAddParallelTasks() {
        Workflow workflow = new Workflow("wf-1", "Test Workflow");
        List<Task> tasks = Arrays.asList(
            SimpleTask.of("Task 1", "result1"),
            SimpleTask.of("Task 2", "result2")
        );
        
        workflow.addParallelTasks(tasks);
        
        assertEquals(1, workflow.getStages().size());
        assertEquals(2, workflow.getAllTasks().size());
        assertTrue(workflow.getStages().get(0).isParallel());
    }
    
    @Test
    public void testMixedWorkflow() {
        Workflow workflow = new Workflow("wf-1", "Mixed Workflow");
        
        // Add sequential stage
        workflow.addSequentialTasks(Arrays.asList(
            SimpleTask.of("Init Task", "initialized")
        ));
        
        // Add parallel stage
        workflow.addParallelTasks(Arrays.asList(
            SimpleTask.of("Parallel Task 1", "parallel1"),
            SimpleTask.of("Parallel Task 2", "parallel2")
        ));
        
        // Add final sequential task
        workflow.addTask(SimpleTask.of("Cleanup Task", "cleaned"));
        
        assertEquals(3, workflow.getStages().size());
        assertEquals(4, workflow.getAllTasks().size());
        
        assertTrue(workflow.getStages().get(0).isSequential());
        assertTrue(workflow.getStages().get(1).isParallel());
        assertTrue(workflow.getStages().get(2).isSequential());
    }
}