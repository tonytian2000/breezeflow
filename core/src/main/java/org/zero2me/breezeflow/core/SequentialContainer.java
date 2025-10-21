package org.zero2me.breezeflow.core;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

/**
 * A container that executes tasks sequentially in the order they were added.
 * 
 * SequentialContainer is a specialized Task that can contain and execute other tasks
 * in a sequential manner. Each task is executed only after the previous task has completed.
 * If any task fails, the execution of the container is halted.
 */
public class SequentialContainer extends Task {
    /**
     * List of tasks to be executed sequentially.
     */
    @Getter
    private List<Task> tasks = new LinkedList<>();
    
    /**
     * Package-private constructor - only TaskFactory can create SequentialContainer instances.
     * This enforces the factory pattern for task creation.
     */
    SequentialContainer() {
        super();
    }

    /**
     * Adds a task to this container.
     * Tasks will be executed in the order they are added.
     *
     * @param task the task to add
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Gets the number of tasks in this container.
     *
     * @return the number of tasks
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Finds a task by its ID.
     *
     * @param id the ID of the task to find
     * @return the task with the specified ID, or null if not found
     */
    public Task getTaskById(String id) {
        return tasks.stream().filter(t -> t.getId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }

    /**
     * Executes all tasks in this container sequentially.
     * Tasks are executed in the order they were added. If a task fails,
     * execution is halted and an exception is thrown.
     * 
     * @throws RuntimeException if any task fails during execution
     */
    @Override
    public void invoke() {
        if (tasks.isEmpty()) {
            return;
        }

        logger.info("Starting sequential container ({}:{}) execution with {} tasks.", 
                        getId(), getName(), tasks.size());
        listener.notify(WorkflowEventType.TASK_STARTED, 
                String.format("Sequential container %s:%s started", getId(), getName()));

        for (Task task : tasks) {
            try {
                task.run();
            } catch (WorkflowExecutionException e) {
                logger.error("Sequential task execution failed for task: {}", task.getId(), e);
                throw new RuntimeException("Sequential task execution failed for task: " + task.getId(), e);
            }
        }

        logger.info("Completed sequential container ({}:{}) execution.", getId(), getName());
        listener.notify(WorkflowEventType.TASK_COMPLETED, 
                String.format("Sequential container %s:%s completed", getId(), getName()));
    }

    /**
     * Performs pre-execution checks.
     * For SequentialContainer, this always returns true as there are no
     * specific preconditions for execution.
     * 
     * @return true, indicating that the container can always be executed
     */
    @Override
    protected boolean preCheck() {
        return true;
    }
}
