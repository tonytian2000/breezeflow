# BreezeFlow

A simple yet powerful Java workflow engine that makes it easy to build workflows with tasks supporting both parallel and sequential execution.

## Features

- **Simple API**: Easy to use workflow builder pattern
- **Parallel Execution**: Tasks can run concurrently to improve performance
- **Sequential Execution**: Tasks can run one after another for ordered processing
- **Mixed Workflows**: Combine sequential and parallel stages in the same workflow
- **Thread-Safe**: Built with concurrent execution in mind
- **Comprehensive Results**: Detailed execution results with timing information
- **Flexible Task Model**: Simple task interface that's easy to implement
- **Error Handling**: Graceful error handling with detailed failure information

## Quick Start

### Maven Dependency

Add BreezeFlow to your Maven project:

```xml
<dependency>
    <groupId>com.breezeflow</groupId>
    <artifactId>breezeflow</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Basic Usage

```java
import com.breezeflow.core.WorkflowEngine;
import com.breezeflow.task.SimpleTask;
import com.breezeflow.workflow.Workflow;
import com.breezeflow.workflow.WorkflowResult;

// Create a workflow engine
WorkflowEngine engine = new WorkflowEngine();

// Build a simple workflow
Workflow workflow = new Workflow("my-workflow", "My First Workflow");

// Add sequential tasks
workflow.addTask(SimpleTask.of("Load Data", "Data loaded"));
workflow.addTask(SimpleTask.of("Process Data", "Data processed"));

// Execute the workflow
WorkflowResult result = engine.execute(workflow);

// Check results
System.out.println("Success: " + result.isSuccess());
System.out.println("Duration: " + result.getDuration());

// Don't forget to shutdown
engine.shutdown();
```

## Examples

### Sequential Workflow

Tasks run one after another in order:

```java
Workflow workflow = new Workflow("sequential", "Sequential Processing");

workflow.addSequentialTasks(Arrays.asList(
    SimpleTask.withDelay("Load Data", 100, "Data loaded"),
    SimpleTask.withDelay("Validate Data", 150, "Data validated"),
    SimpleTask.withDelay("Process Data", 200, "Data processed")
));

WorkflowResult result = engine.execute(workflow);
// Total time: ~450ms (100 + 150 + 200)
```

### Parallel Workflow

Tasks run concurrently to improve performance:

```java
Workflow workflow = new Workflow("parallel", "Parallel Processing");

workflow.addParallelTasks(Arrays.asList(
    SimpleTask.withDelay("Process File A", 300, "File A processed"),
    SimpleTask.withDelay("Process File B", 250, "File B processed"),
    SimpleTask.withDelay("Process File C", 200, "File C processed")
));

WorkflowResult result = engine.execute(workflow);
// Total time: ~300ms (runs concurrently, takes as long as the slowest task)
```

### Mixed Workflow

Combine sequential and parallel execution:

```java
Workflow workflow = new Workflow("mixed", "Mixed Processing");

// Phase 1: Sequential initialization
workflow.addTask(SimpleTask.of("Initialize", "System initialized"));

// Phase 2: Parallel data processing
workflow.addParallelTasks(Arrays.asList(
    SimpleTask.withDelay("Fetch User Data", 200, "Users loaded"),
    SimpleTask.withDelay("Fetch Product Data", 150, "Products loaded"),
    SimpleTask.withDelay("Fetch Analytics", 180, "Analytics loaded")
));

// Phase 3: Sequential finalization
workflow.addSequentialTasks(Arrays.asList(
    SimpleTask.withDelay("Merge Data", 100, "Data merged"),
    SimpleTask.withDelay("Generate Report", 150, "Report generated")
));

WorkflowResult result = engine.execute(workflow);
```

## Core Concepts

### Tasks

Tasks are units of work that implement the `Task` interface:

```java
public interface Task {
    TaskResult execute() throws TaskExecutionException;
    String getId();
    String getName();
}
```

For simple tasks, extend `AbstractTask`:

```java
public class MyTask extends AbstractTask {
    public MyTask(String name) {
        super(name);
    }
    
    @Override
    protected Object doExecute() throws Exception {
        // Your task logic here
        return "Task completed";
    }
}
```

### Workflows

Workflows contain stages of tasks that can be executed sequentially or in parallel:

- `addTask(task)` - Adds a single sequential task
- `addSequentialTasks(tasks)` - Adds multiple tasks to run sequentially  
- `addParallelTasks(tasks)` - Adds multiple tasks to run in parallel
- `addStage(stage)` - Adds a custom workflow stage

### Results

Execution results provide comprehensive information:

```java
WorkflowResult result = engine.execute(workflow);

// Check overall success
boolean success = result.isSuccess();

// Get execution timing
Duration totalTime = result.getDuration();

// Get individual task results
List<TaskResult> taskResults = result.getTaskResults();

// Get success/failure counts
int successful = result.getSuccessfulTaskCount();
int failed = result.getFailedTaskCount();
```

## Building and Testing

### Prerequisites

- Java 11 or higher
- Maven 3.6+

### Build

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Run Example

```bash
mvn clean compile
java -cp target/classes com.breezeflow.examples.BreezeFlowExample
```

## Architecture

BreezeFlow is designed with simplicity and performance in mind:

- **WorkflowEngine**: The main execution engine handling both sequential and parallel execution
- **Workflow**: Contains ordered stages of tasks
- **WorkflowStage**: Represents a group of tasks with an execution mode (sequential/parallel)
- **Task**: The basic unit of work
- **TaskResult/WorkflowResult**: Comprehensive execution results

## Thread Safety

BreezeFlow is designed to be thread-safe:
- Multiple workflows can be executed concurrently
- Each workflow execution is independent
- Task execution is isolated
- Results are immutable once created

## Error Handling

- Tasks that fail in sequential stages stop further execution
- Tasks that fail in parallel stages don't affect other parallel tasks
- Comprehensive error information is provided in results
- Exceptions are caught and wrapped in TaskResult objects

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.