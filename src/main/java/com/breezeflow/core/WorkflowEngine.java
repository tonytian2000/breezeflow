package com.breezeflow.core;

import com.breezeflow.task.Task;
import com.breezeflow.task.TaskResult;
import com.breezeflow.workflow.Workflow;
import com.breezeflow.workflow.WorkflowResult;
import com.breezeflow.workflow.WorkflowStage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The main workflow execution engine that handles both sequential and parallel task execution.
 */
public class WorkflowEngine {
    private final ExecutorService executorService;
    private final boolean ownsExecutor;
    
    /**
     * Creates a workflow engine with a default thread pool.
     */
    public WorkflowEngine() {
        this.executorService = Executors.newCachedThreadPool();
        ((ThreadPoolExecutor) executorService).setKeepAliveTime(60, TimeUnit.SECONDS);
        this.ownsExecutor = true;
    }
    
    /**
     * Creates a workflow engine with a custom executor service.
     */
    public WorkflowEngine(ExecutorService executorService) {
        this.executorService = executorService;
        this.ownsExecutor = false;
    }
    
    /**
     * Executes a workflow and returns the result.
     */
    public WorkflowResult execute(Workflow workflow) {
        Instant startTime = Instant.now();
        List<TaskResult> allResults = new ArrayList<>();
        
        try {
            for (WorkflowStage stage : workflow.getStages()) {
                List<TaskResult> stageResults = executeStage(stage);
                allResults.addAll(stageResults);
                
                // Check if any task in this stage failed and we should stop
                boolean stageHasFailure = stageResults.stream().anyMatch(result -> !result.isSuccess());
                if (stageHasFailure) {
                    Instant endTime = Instant.now();
                    return new WorkflowResult(workflow.getId(), false, allResults, startTime, endTime,
                                            "Workflow stopped due to task failure in stage");
                }
            }
            
            Instant endTime = Instant.now();
            return new WorkflowResult(workflow.getId(), true, allResults, startTime, endTime, null);
            
        } catch (Exception e) {
            Instant endTime = Instant.now();
            return new WorkflowResult(workflow.getId(), false, allResults, startTime, endTime, 
                                    "Workflow execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Executes a single workflow stage (either sequential or parallel).
     */
    private List<TaskResult> executeStage(WorkflowStage stage) throws InterruptedException, ExecutionException {
        if (stage.isSequential()) {
            return executeSequentialTasks(stage.getTasks());
        } else {
            return executeParallelTasks(stage.getTasks());
        }
    }
    
    /**
     * Executes tasks sequentially (one after another).
     */
    private List<TaskResult> executeSequentialTasks(List<Task> tasks) {
        List<TaskResult> results = new ArrayList<>();
        for (Task task : tasks) {
            try {
                TaskResult result = task.execute();
                results.add(result);
                
                // Stop execution if a task fails
                if (!result.isSuccess()) {
                    break;
                }
            } catch (Exception e) {
                Instant now = Instant.now();
                TaskResult failureResult = TaskResult.failure(task.getId(), 
                    "Task execution threw exception: " + e.getMessage(), now, now);
                results.add(failureResult);
                break;
            }
        }
        return results;
    }
    
    /**
     * Executes tasks in parallel (concurrently).
     */
    private List<TaskResult> executeParallelTasks(List<Task> tasks) throws InterruptedException, ExecutionException {
        List<Future<TaskResult>> futures = new ArrayList<>();
        
        // Submit all tasks for parallel execution
        for (Task task : tasks) {
            Future<TaskResult> future = executorService.submit(() -> {
                try {
                    return task.execute();
                } catch (Exception e) {
                    Instant now = Instant.now();
                    return TaskResult.failure(task.getId(), 
                        "Task execution threw exception: " + e.getMessage(), now, now);
                }
            });
            futures.add(future);
        }
        
        // Collect results
        List<TaskResult> results = new ArrayList<>();
        for (Future<TaskResult> future : futures) {
            results.add(future.get());
        }
        
        return results;
    }
    
    /**
     * Shuts down the workflow engine and its executor service.
     * Only shuts down the executor if this engine owns it.
     */
    public void shutdown() {
        if (ownsExecutor) {
            executorService.shutdown();
        }
    }
    
    /**
     * Attempts to stop all actively executing tasks and shuts down the executor service.
     * Only shuts down the executor if this engine owns it.
     */
    public void shutdownNow() {
        if (ownsExecutor) {
            executorService.shutdownNow();
        }
    }
}