package com.zero2me.breezeflow.core;

import java.util.LinkedList;
import java.util.List;

public class SequentialContainer extends Task {
    private List<Task> tasks = new LinkedList<>();

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
        tasks.forEach(task -> {
            task.run();
        });
    }

    @Override
    protected boolean preCheck() {
        return true;
    }
}
