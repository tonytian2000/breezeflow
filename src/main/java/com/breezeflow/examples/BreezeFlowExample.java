package com.breezeflow.examples;

import com.breezeflow.core.WorkflowEngine;
import com.breezeflow.task.SimpleTask;
import com.breezeflow.task.TaskResult;
import com.breezeflow.workflow.Workflow;
import com.breezeflow.workflow.WorkflowResult;

import java.util.Arrays;

/**
 * Example demonstrating the BreezeFlow workflow engine capabilities.
 */
public class BreezeFlowExample {
    
    public static void main(String[] args) {
        WorkflowEngine engine = new WorkflowEngine();
        
        try {
            // Example 1: Simple sequential workflow
            System.out.println("=== Example 1: Sequential Workflow ===");
            runSequentialWorkflow(engine);
            
            System.out.println("\n=== Example 2: Parallel Workflow ===");
            runParallelWorkflow(engine);
            
            System.out.println("\n=== Example 3: Mixed Workflow ===");
            runMixedWorkflow(engine);
            
            System.out.println("\n=== Example 4: Data Processing Pipeline ===");
            runDataProcessingPipeline(engine);
            
        } finally {
            engine.shutdown();
        }
    }
    
    private static void runSequentialWorkflow(WorkflowEngine engine) {
        Workflow workflow = new Workflow("seq-example", "Sequential Example");
        
        workflow.addSequentialTasks(Arrays.asList(
            SimpleTask.withDelay("Load Data", 100, "Data loaded"),
            SimpleTask.withDelay("Validate Data", 150, "Data validated"),
            SimpleTask.withDelay("Process Data", 200, "Data processed"),
            SimpleTask.withDelay("Save Results", 100, "Results saved")
        ));
        
        long startTime = System.currentTimeMillis();
        WorkflowResult result = engine.execute(workflow);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Workflow Result: " + result);
        System.out.println("Total Execution Time: " + (endTime - startTime) + "ms");
        
        for (TaskResult taskResult : result.getTaskResults()) {
            System.out.println("- " + taskResult);
        }
    }
    
    private static void runParallelWorkflow(WorkflowEngine engine) {
        Workflow workflow = new Workflow("par-example", "Parallel Example");
        
        workflow.addParallelTasks(Arrays.asList(
            SimpleTask.withDelay("Process File A", 300, "File A processed"),
            SimpleTask.withDelay("Process File B", 250, "File B processed"),
            SimpleTask.withDelay("Process File C", 200, "File C processed"),
            SimpleTask.withDelay("Process File D", 150, "File D processed")
        ));
        
        long startTime = System.currentTimeMillis();
        WorkflowResult result = engine.execute(workflow);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Workflow Result: " + result);
        System.out.println("Total Execution Time: " + (endTime - startTime) + "ms");
        System.out.println("(Should be ~300ms instead of 900ms due to parallel execution)");
        
        for (TaskResult taskResult : result.getTaskResults()) {
            System.out.println("- " + taskResult);
        }
    }
    
    private static void runMixedWorkflow(WorkflowEngine engine) {
        Workflow workflow = new Workflow("mixed-example", "Mixed Sequential/Parallel Example");
        
        // Phase 1: Sequential initialization
        workflow.addTask(SimpleTask.withDelay("Initialize System", 100, "System initialized"));
        
        // Phase 2: Parallel data gathering
        workflow.addParallelTasks(Arrays.asList(
            SimpleTask.withDelay("Fetch User Data", 200, "User data fetched"),
            SimpleTask.withDelay("Fetch Product Data", 150, "Product data fetched"),
            SimpleTask.withDelay("Fetch Analytics Data", 180, "Analytics data fetched")
        ));
        
        // Phase 3: Sequential processing and cleanup
        workflow.addSequentialTasks(Arrays.asList(
            SimpleTask.withDelay("Merge Data", 100, "Data merged"),
            SimpleTask.withDelay("Generate Report", 150, "Report generated"),
            SimpleTask.withDelay("Cleanup Resources", 50, "Resources cleaned")
        ));
        
        long startTime = System.currentTimeMillis();
        WorkflowResult result = engine.execute(workflow);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Workflow Result: " + result);
        System.out.println("Total Execution Time: " + (endTime - startTime) + "ms");
        
        for (TaskResult taskResult : result.getTaskResults()) {
            System.out.println("- " + taskResult);
        }
    }
    
    private static void runDataProcessingPipeline(WorkflowEngine engine) {
        Workflow workflow = new Workflow("pipeline-example", "Data Processing Pipeline");
        
        // Step 1: Data ingestion
        workflow.addTask(SimpleTask.of("Ingest Raw Data", "Raw data ingested"));
        
        // Step 2: Parallel data validation and enrichment
        workflow.addParallelTasks(Arrays.asList(
            SimpleTask.withDelay("Validate Schema", 100, "Schema validated"),
            SimpleTask.withDelay("Check Data Quality", 150, "Data quality checked"),
            SimpleTask.withDelay("Enrich with Metadata", 120, "Metadata enriched")
        ));
        
        // Step 3: Sequential transformation pipeline
        workflow.addSequentialTasks(Arrays.asList(
            SimpleTask.withDelay("Clean Data", 80, "Data cleaned"),
            SimpleTask.withDelay("Transform Format", 100, "Format transformed"),
            SimpleTask.withDelay("Apply Business Rules", 90, "Business rules applied")
        ));
        
        // Step 4: Parallel output generation
        workflow.addParallelTasks(Arrays.asList(
            SimpleTask.withDelay("Generate JSON Export", 60, "JSON export generated"),
            SimpleTask.withDelay("Generate CSV Export", 70, "CSV export generated"),
            SimpleTask.withDelay("Generate Database Insert", 80, "Database insert generated"),
            SimpleTask.withDelay("Generate Summary Report", 50, "Summary report generated")
        ));
        
        // Step 5: Final cleanup
        workflow.addTask(SimpleTask.of("Archive Processing Logs", "Logs archived"));
        
        long startTime = System.currentTimeMillis();
        WorkflowResult result = engine.execute(workflow);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Workflow Result: " + result);
        System.out.println("Total Execution Time: " + (endTime - startTime) + "ms");
        System.out.println("Pipeline processed " + result.getSuccessfulTaskCount() + " tasks successfully");
    }
}