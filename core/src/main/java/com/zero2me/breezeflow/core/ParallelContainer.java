package com.zero2me.breezeflow.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
public class ParallelContainer extends Task {
    @Getter
    private List<Task> tasks = new ArrayList<>();
    
    private int threadPoolSize = -1;

    // Package-private constructor - only TaskFactory can create ParallelContainer instances
    ParallelContainer() {
        // threadPoolSize will be set from sessionConfig during injection
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public void invoke() {
        if (tasks.isEmpty()) {
            return;
        }

        // Determine thread pool size
        int poolSize = threadPoolSize;
        if (poolSize <= 0 && sessionConfig != null) {
            poolSize = sessionConfig.getThreadPoolSize();
        }
        if (poolSize <= 0) {
            poolSize = Runtime.getRuntime().availableProcessors();
        }

        // Create thread pool
        logger.info("Creating thread pool with size: {}", poolSize);
        ExecutorService executor = createThreadPool(poolSize);
        
        try {
            logger.info("Starting parallel container ({}:{}) execution with {} tasks.", 
                            getId(), getName(), tasks.size());
            listener.notify(WorkflowEventType.TASK_STARTED, 
                    String.format("Parallel container %s:%s started", getId(), getName()));
                    
            // Submit all tasks to the thread pool
            List<Future<?>> futures = new ArrayList<>();
            for (Task task : tasks) {
                Future<?> future = executor.submit(() -> {
                    try {
                        task.run();
                    } catch (Exception e) {
                        throw new RuntimeException("Task execution failed: " + task.getId(), e);
                    }
                });
                futures.add(future);
            }
            
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException("Parallel task execution failed", e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Parallel execution interrupted", e);
                }
            }

            logger.info("Completed parallel container ({}:{}) execution.", getId(), getName());
            listener.notify(WorkflowEventType.TASK_COMPLETED, 
                    String.format("Parallel container %s:%s completed", getId(), getName()));
        } finally {
            // Shutdown the thread pool
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Creates a thread pool with the specified size.
     * 
     * @param poolSize the size of the thread pool
     * @return the created ExecutorService
     */
    private ExecutorService createThreadPool(int poolSize) {
        return new ThreadPoolExecutor(
            poolSize,                    // core pool size
            poolSize,                    // maximum pool size
            60L,                         // keep alive time
            TimeUnit.SECONDS,            // time unit
            new LinkedBlockingQueue<>(), // work queue
            new ThreadFactory() {
                private int threadNumber = 1;
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "breezeflow-parallel-" + threadNumber++);
                    thread.setDaemon(false);
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
        );
    }

    @Override
    protected boolean preCheck() {
        return true;
    }
}
