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
        for (Task task : tasks) {
            try {
                task.run();
            } catch (WorkflowExecutionException e) {
                logger.error("Sequential task execution failed for task: {}", task.getId(), e);
                throw new RuntimeException("Sequential task execution failed for task: " + task.getId(), e);
            }
        }
    }

    @Override
    protected boolean preCheck() {
        return true;
    }
}
