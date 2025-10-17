# BreezeFlow Tutorial Module

This module provides comprehensive examples and tutorials for using the BreezeFlow workflow engine. It demonstrates how to create tasks, containers, and workflows using the TaskFactory pattern.

## Overview

The tutorial module includes:

- **Task Examples**: SimpleTask, DataProcessingTask, ValidationTask
- **Workflow Examples**: Basic workflow creation and execution patterns
- **Best Practices**: Proper usage patterns and common pitfalls
- **Interactive Examples**: Runnable code examples with detailed explanations

## Quick Start

### Running the Tutorial

1. **Build the project**:
   ```bash
   mvn clean compile
   ```

2. **Run individual examples**:
   ```bash
   # Run SimpleWorkflowExample
   mvn exec:java -Dexec.mainClass="com.zero2me.breezeflow.tutorial.workflows.SimpleWorkflowExample"
   
   # Run task examples directly
   mvn exec:java -Dexec.mainClass="com.zero2me.breezeflow.tutorial.tasks.SimpleTask"
   ```

3. **Create an executable JAR and run**:
   ```bash
   mvn clean package
   java -jar target/breezeflow-tutorial-1.0.0-SNAPSHOT.jar
   ```


## Examples Overview

### 1. Task Examples

#### SimpleTask
A basic task that demonstrates:
- Configurable message output
- Configurable execution delay
- Configurable failure simulation
- Proper logging and error handling

```java
SimpleTask task = taskFactory.buildTask(SimpleTask.class, "MyTask");
task.setMessage("Hello World");
task.setDelayMs(100);
task.run();
```

#### DataProcessingTask
A more complex task that demonstrates:
- Data processing operations (SUM, AVERAGE, MAX, MIN, COUNT)
- Facts integration for input/output
- Configurable operation types

```java
DataProcessingTask task = taskFactory.buildTask(DataProcessingTask.class, "SumTask");
task.setOperation(DataProcessingTask.Operation.SUM);
task.setInputKey("numbers");
task.setOutputKey("result");
task.run();
```

#### ValidationTask
A validation task that demonstrates:
- Multiple validation rules (NOT_NULL, NOT_EMPTY, POSITIVE_NUMBER, EMAIL_FORMAT, MIN_LENGTH)
- Facts integration for validation data
- Structured validation results

```java
ValidationTask task = taskFactory.buildTask(ValidationTask.class, "EmailValidation");
task.setInputKey("email");
task.setOutputKey("validation");
task.setRule(ValidationTask.ValidationRule.EMAIL_FORMAT);
task.run();
```

### 2. Workflow Examples

#### SimpleWorkflowExample
Demonstrates workflow creation and execution:
- Task addition to workflows using TaskFactory
- Sequential execution within workflows
- Facts management across tasks
- Workflow lifecycle management
- Proper error handling

```java
// Create workflow and get TaskFactory
Workflow workflow = new Workflow();
TaskFactory taskFactory = workflow.getTaskFactory();

// Add tasks to workflow
workflow.addTask(SimpleTask.class, "Step1");
workflow.addTask(SimpleTask.class, "Step2");
workflow.addTask(SimpleTask.class, "Step3");

// Execute workflow
workflow.run();
```

#### Advanced Workflow with Custom Tasks
Demonstrates more complex workflow patterns:
- Custom task configuration
- Data processing workflows
- Validation workflows
- Mixed task types

```java
Workflow workflow = new Workflow();
TaskFactory taskFactory = workflow.getTaskFactory();

// Configure and add custom tasks
SimpleTask task1 = taskFactory.buildTask(SimpleTask.class, "CustomTask1");
task1.setMessage("Processing data...");
workflow.addTask(task1);

DataProcessingTask task2 = taskFactory.buildTask(DataProcessingTask.class, "DataProcessor");
task2.setOperation(DataProcessingTask.Operation.SUM);
workflow.addTask(task2);

workflow.run();
```

## Key Concepts

### TaskFactory Pattern
All tasks must be created through the TaskFactory obtained from a Workflow instance to ensure:
- Proper dependency injection
- Consistent configuration
- Controlled instantiation
- Error handling

```java
// ✅ Correct - Get TaskFactory from Workflow
Workflow workflow = new Workflow();
TaskFactory taskFactory = workflow.getTaskFactory();
SimpleTask task = taskFactory.buildTask(SimpleTask.class, "MyTask");

// ❌ Incorrect - Direct TaskFactory instantiation (will cause compilation error)
// TaskFactory taskFactory = new TaskFactory(facts, sessionContext, sessionConfig, listener); // Compilation error!

// ❌ Incorrect - Direct task instantiation (will cause compilation error)
// SimpleTask task = new SimpleTask(); // Compilation error!
```

### Facts Management
Facts provide a way to share data between tasks:
- Input data for tasks
- Output results from tasks
- Configuration parameters
- Validation results

```java
// Create workflow and get facts
Workflow workflow = new Workflow();
Facts facts = workflow.getFacts();

// Set input data
facts.put("numbers", Arrays.asList(1, 2, 3, 4, 5));
facts.put("email", "user@example.com");

// Get results
Number sum = facts.get("sum");
Map<String, Object> validation = facts.get("emailValidation");
```

### Thread Pool Configuration
ParallelContainer uses configurable thread pools:
- Set via SessionConfig
- Override via constructor
- Automatic fallback to available processors

```java
Workflow workflow = new Workflow();
workflow.getSessionConfig().setThreadPoolSize(4); // 4 threads for parallel execution
```

### Workflow Extension
The Workflow class supports extension for custom workflow types:

```java
public class MyCustomWorkflow extends Workflow {
    @Override
    public void buildWorkflow() {
        // Override this method to build your custom workflow
        TaskFactory taskFactory = getTaskFactory();
        
        // Add your custom workflow logic here
        addTask(taskFactory.buildTask(MyCustomTask.class, "CustomTask1"));
        addTask(taskFactory.buildTask(MyCustomTask.class, "CustomTask2"));
    }
}
```

### Multiple Ways to Add Tasks
The Workflow class provides flexibility in how tasks are added:

```java
Workflow workflow = new Workflow();
TaskFactory taskFactory = workflow.getTaskFactory();

// Method 1: Add task by class and name (creates task automatically)
workflow.addTask(SimpleTask.class, "Task1");

// Method 2: Add pre-configured task instance
SimpleTask task = taskFactory.buildTask(SimpleTask.class, "Task2");
task.setMessage("Custom message");
workflow.addTask(task);
```

### Creating Custom Tasks
To create your own task types, extend the Task class:

```java
public class MyCustomTask extends Task {
    private String customProperty;
    
    public void setCustomProperty(String value) {
        this.customProperty = value;
    }
    
    @Override
    protected boolean preCheck() {
        // Add validation logic here
        if (customProperty == null || customProperty.isEmpty()) {
            logger.warn("Custom property is not set");
            return false;
        }
        return true;
    }
    
    @Override
    protected void invoke() {
        // Add your task logic here
        logger.info("Executing custom task with property: {}", customProperty);
        
        // Example: Process data and store result in facts
        String result = "Processed: " + customProperty;
        facts.put("customResult", result);
    }
}
```

Then use it in your workflow:

```java
Workflow workflow = new Workflow();
TaskFactory taskFactory = workflow.getTaskFactory();

MyCustomTask customTask = taskFactory.buildTask(MyCustomTask.class, "MyCustomTask");
customTask.setCustomProperty("Hello World");
workflow.addTask(customTask);

workflow.run();
```

## Project Structure

```
tutorial/
├── src/main/java/com/zero2me/breezeflow/tutorial/
│   ├── tasks/
│   │   ├── SimpleTask.java              # Basic task example
│   │   ├── DataProcessingTask.java      # Data processing task example
│   │   └── ValidationTask.java          # Validation task example
│   └── workflows/
│       └── SimpleWorkflowExample.java   # Basic workflow example
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

## Dependencies

The tutorial module depends on:
- `breezeflow-core`: Core workflow engine
- `breezeflow-common`: Common utilities
- `slf4j-api`: Logging API
- `logback-classic`: Logging implementation
- `lombok`: Code generation
- `junit-jupiter`: Testing framework

## Best Practices

1. **Always get TaskFactory from Workflow**: Never instantiate TaskFactory or tasks directly
2. **Configure tasks properly**: Set all required parameters before execution
3. **Handle exceptions**: Wrap task execution in try-catch blocks
4. **Use appropriate execution patterns**: Choose parallel vs sequential based on requirements
5. **Manage facts carefully**: Ensure proper data flow between tasks
6. **Configure thread pools**: Set appropriate thread pool sizes for parallel execution
7. **Extend Task class properly**: Override both `preCheck()` and `invoke()` methods
8. **Use meaningful task names**: Provide descriptive names for better debugging

## Troubleshooting

### Common Issues

1. **Compilation Errors**: Ensure you're getting TaskFactory from Workflow instance
2. **Runtime Errors**: Check that all required facts are set before task execution
3. **Performance Issues**: Consider using parallel execution for independent tasks
4. **Memory Issues**: Monitor thread pool sizes and task complexity

### Getting Help

- Check the example code in this module
- Review the core module documentation
- Examine the task examples for usage patterns
- Run the individual example classes to see them in action
- Check the main project README for comprehensive documentation

## Contributing

When adding new examples:
1. Follow the existing naming conventions
2. Include comprehensive documentation
3. Add proper error handling
4. Update this README with new examples
5. Test all examples before committing
