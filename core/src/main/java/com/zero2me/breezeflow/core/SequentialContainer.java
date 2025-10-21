package com.zero2me.breezeflow.core;

import java.util.LinkedList;
import java.util.List;

public class SequentialContainer extends Task {
    private List<Task> tasks = new LinkedList<>();
    
    // Package-private constructor - only TaskFactory can create SequentialContainer instances
    SequentialContainer() {
        super();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public Task getTaskById(String id) {
        return tasks.stream().filter(t -> t.getId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }

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

    @Override
    protected boolean preCheck() {
        return true;
    }
}
