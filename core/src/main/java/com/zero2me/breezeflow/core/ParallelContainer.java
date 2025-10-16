package com.zero2me.breezeflow.core;

import java.util.ArrayList;
import java.util.List;

public class ParallelContainer extends Task {
    public List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public void invoke() {
        if (tasks.size() == 0) return;

        tasks.parallelStream().forEach(task -> {
            task.run();
        });
    }

    @Override
    protected boolean preCheck() {
        return true;
    }
}
