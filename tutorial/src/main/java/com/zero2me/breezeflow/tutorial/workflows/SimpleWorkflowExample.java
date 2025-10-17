package com.zero2me.breezeflow.tutorial.workflows;

import com.zero2me.breezeflow.core.*;
import com.zero2me.breezeflow.tutorial.tasks.SimpleTask;
import com.zero2me.breezeflow.tutorial.tasks.DataProcessingTask;
import com.zero2me.breezeflow.tutorial.tasks.ValidationTask;
/**
 * Example demonstrating how to create and execute a basic workflow.
 * This example shows the fundamental concepts of workflow creation and execution.
 */
public class SimpleWorkflowExample {
    
    public static void main(String[] args) {
        System.out.println("=== Basic Workflow Example ===\n");
        
        try {
            demonstrateWorkflow();
        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates a simple workflow with sequential task execution.
     */
    private static void demonstrateWorkflow() throws WorkflowExecutionException {
        System.out.println("--- Simple Sequential Workflow ---");
        
        // Create a workflow
        Workflow workflow = new Workflow();
        TaskFactory taskFactory = workflow.getTaskFactory();
        
        // Add tasks directly to the workflow (they will be executed sequentially)
        workflow.addTask(SimpleTask.class, "first-task");

        SequentialContainer seqContainer =
            taskFactory.buildTask(SequentialContainer.class, "sequential-container");
        seqContainer.addTask(taskFactory.buildTask(SimpleTask.class, "simple-1"));
        seqContainer.addTask(taskFactory.buildTask(SimpleTask.class, "simple-2"));

        workflow.addTask(seqContainer);

        ParallelContainer parContainer =
            taskFactory.buildTask(ParallelContainer.class, "parallel-container");
        parContainer.addTask(taskFactory.buildTask(DataProcessingTask.class, "data-processing"));
        parContainer.addTask(taskFactory.buildTask(ValidationTask.class, "validation"));
        
        workflow.addTask(parContainer);

        workflow.addTask(SimpleTask.class, "last-task");
        
        System.out.println("Created workflow wit tasks");
          
        // Execute the workflow
        System.out.println("Starting workflow execution...");
        long startTime = System.currentTimeMillis();
 
        workflow.run();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Workflow completed in " + (endTime - startTime) + " ms");
        System.out.println();
    }
}