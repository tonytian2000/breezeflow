package com.breezeflow.workflow;

import com.breezeflow.task.Task;
import java.util.*;

/**
 * Represents a workflow that contains a collection of tasks to be executed.
 * Tasks can be organized into sequential stages or parallel groups.
 */
public class Workflow {
    private final String id;
    private final String name;
    private final List<WorkflowStage> stages;
    
    public Workflow(String id, String name) {
        this.id = id;
        this.name = name;
        this.stages = new ArrayList<>();
    }
    
    /**
     * Adds a sequential stage containing a single task.
     */
    public Workflow addTask(Task task) {
        stages.add(WorkflowStage.sequential(Collections.singletonList(task)));
        return this;
    }
    
    /**
     * Adds a sequential stage containing multiple tasks that will run one after another.
     */
    public Workflow addSequentialTasks(List<Task> tasks) {
        stages.add(WorkflowStage.sequential(tasks));
        return this;
    }
    
    /**
     * Adds a parallel stage containing multiple tasks that will run concurrently.
     */
    public Workflow addParallelTasks(List<Task> tasks) {
        stages.add(WorkflowStage.parallel(tasks));
        return this;
    }
    
    /**
     * Adds a custom workflow stage.
     */
    public Workflow addStage(WorkflowStage stage) {
        stages.add(stage);
        return this;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public List<WorkflowStage> getStages() {
        return Collections.unmodifiableList(stages);
    }
    
    /**
     * Gets all tasks in this workflow across all stages.
     */
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        for (WorkflowStage stage : stages) {
            allTasks.addAll(stage.getTasks());
        }
        return allTasks;
    }
    
    @Override
    public String toString() {
        return String.format("Workflow{id='%s', name='%s', stages=%d}", id, name, stages.size());
    }
}