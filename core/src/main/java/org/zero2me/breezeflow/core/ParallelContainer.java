package org.zero2me.breezeflow.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import lombok.Getter;

/**
 * A container that executes tasks in parallel using a thread pool.
 * 
 * ParallelContainer is a specialized Task that can contain and execute other tasks
 * concurrently. It uses a configurable thread pool to run tasks in parallel, which
 * can significantly improve performance for independent tasks.
 */
public class ParallelContainer extends Task {
    /**
     * List of tasks to be executed in parallel.
     */
    @Getter
    private List<Task> tasks = new ArrayList<>();
    
    /**
     * The size of the thread pool to use for parallel execution.
     * If set to a value <= 0, the container will use the thread pool size from
     * the session configuration, or the number of available processors if not configured.
     */
    private int threadPoolSize = -1;

    /**
     * Package-private constructor - only TaskFactory can create ParallelContainer instances.
     * This enforces the factory pattern for task creation.
     * The threadPoolSize will be set from sessionConfig during injection.
     */
    ParallelContainer() {
        // threadPoolSize will be set from sessionConfig during injection
    }

    /**
     * Adds a task to this container.
     * Tasks will be executed in parallel when the container is invoked.
     *
     * @param task the task to add
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Executes all tasks in this container in parallel using a thread pool.
     * The method determines the appropriate thread pool size, creates the pool,
     * submits all tasks for execution, and waits for all tasks to complete.
     * If any task fails, an exception is thrown.
     * 
     * @throws RuntimeException if any task fails during execution or if execution is interrupted
     */
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

    /**
     * Performs pre-execution checks.
     * For ParallelContainer, this always returns true as there are no
     * specific preconditions for execution.
     * 
     * @return true, indicating that the container can always be executed
     */
    @Override
    protected boolean preCheck() {
        return true;
    }
}
