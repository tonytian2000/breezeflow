# BreezeFlow

A lightweight, flexible workflow engine for Java applications, built with Maven and designed for JDK 11.

## Overview

BreezeFlow is a lightweight workflow engine library that provides a clean API for defining and executing workflows. It features a controlled task creation pattern through TaskFactory and supports both sequential and parallel task execution. The library enables you to build complex data processing pipelines with clear separation of concerns, data sharing between tasks, and flexible execution patterns. It is designed to be used as a dependency in other Java projects and supports cloud artifactory deployment.

## Architecture   
![alt text](https://github.com/tonytian2000/breezeflow/blob/main/workflow.png?raw=true)

## Features

- **Multi-module Architecture**: Clean separation of concerns with `common`, `core`, and `tutorial` modules
- **JDK 11 Support**: Built and tested with Java 11
- **SLF4J Logging**: Comprehensive logging with SLF4J and Logback
- **TaskFactory Pattern**: Controlled task creation and dependency injection
- **Parallel & Sequential Execution**: Support for both parallel and sequential task execution
- **Thread Pool Management**: Configurable thread pools for parallel execution
- **Extensible Workflow System**: Easy-to-extend workflow and task framework
- **Data Sharing Mechanism**: Facts for task completion flags and SessionContext for data exchange
- **Task Execution Pattern**: Consistent preCheck() and invoke() pattern for all tasks
- **Comprehensive Tutorials**: Complete tutorial module with real-world examples
- **Cloud Ready**: Configured for deployment to cloud artifactory repositories

## Module Structure

### Core Module (`breezeflow-core`)
Contains the main workflow engine implementation:
- `Workflow`: Main workflow class with task management
- `TaskFactory`: Controlled task creation and dependency injection
- `Task`: Abstract base class for all tasks
- `ParallelContainer`: Parallel task execution container
- `SequentialContainer`: Sequential task execution container
- `SessionConfig`: Configuration management including thread pool settings
- `Facts`: Task execution condition variable
- `SessionContext`: Data sharing mechanism between tasks
- `WorkflowListener`: Event notification system

### Tutorial Module (`breezeflow-tutorial`)
Comprehensive examples and tutorials:
- Document processing workflow example
- Task examples (ReadDocumentTask, CalculateWordCountTask, FindKeywordCountTask, etc.)
- Container examples (ParallelContainer, SequentialContainer)
- Workflow examples and patterns
- Best practices and usage patterns

## Quick Start

### Adding as a Dependency (NOT SUPPORT YET, PLEASE WAIT!!!)

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>org.zero2me</groupId>
    <artifactId>breezeflow</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import org.zero2me.breezeflow.core.*;

// Create a workflow
Workflow workflow = new Workflow();

// Get TaskFactory from workflow (this is the only way to create tasks)
TaskFactory taskFactory = workflow.getTaskFactory();

// Create and configure tasks
SimpleTask task1 = taskFactory.buildTask(SimpleTask.class, "Task1");
SimpleTask task2 = taskFactory.buildTask(SimpleTask.class, "Task2");

// Add tasks to workflow
workflow.addTask(task1);
workflow.addTask(task2);

// Execute the workflow
workflow.run();
```

### Advanced Usage with Parallel Execution

```java
import org.zero2me.breezeflow.core.*;

// Create workflow and configure thread pool
Workflow workflow = new Workflow();
workflow.getSessionConfig().setThreadPoolSize(4);

TaskFactory taskFactory = workflow.getTaskFactory();

// Create parallel container
ParallelContainer parallelContainer = taskFactory.buildTask(ParallelContainer.class, "ParallelTasks");

// Add tasks to parallel container
parallelContainer.addTask(taskFactory.buildTask(SimpleTask.class, "ParallelTask1"));
parallelContainer.addTask(taskFactory.buildTask(SimpleTask.class, "ParallelTask2"));
parallelContainer.addTask(taskFactory.buildTask(SimpleTask.class, "ParallelTask3"));

// Add parallel container to workflow
workflow.addTask(parallelContainer);

// Execute workflow
workflow.run();
```

## Building the Project

### Prerequisites
- JDK 11 or higher
- Maven 3.6 or higher

### Build Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package all modules
mvn clean package

# Install to local repository
mvn clean install

# Generate sources and javadoc
mvn clean package -DskipTests
```

## Deployment

### Cloud Artifactory Configuration

1. Update the `distributionManagement` section in the root `pom.xml` with your artifactory URLs
2. Set environment variables:
   ```bash
   export ARTIFACTORY_USERNAME=your-username
   export ARTIFACTORY_PASSWORD=your-password
   export GPG_PASSPHRASE=your-gpg-passphrase  # For signed releases
   ```

### Deployment Commands

```bash
# Deploy snapshots
mvn clean deploy

# Deploy release (with GPG signing)
mvn clean deploy -Prelease
```

## Development

### Project Structure
```
breezeflow/
├── pom.xml                 # Parent POM
├── common/                 # Common utilities module
│   ├── pom.xml
│   └── src/
├── core/                   # Core implementation module
│   ├── pom.xml
│   └── src/
│       └── main/java/com/zero2me/breezeflow/core/
│           ├── Workflow.java
│           ├── TaskFactory.java
│           ├── Task.java
│           ├── ParallelContainer.java
│           ├── SequentialContainer.java
│           ├── SessionConfig.java
│           ├── Facts.java
├── tutorial/               # Tutorial and examples module
│   ├── pom.xml
│   ├── README.md
│   └── src/
└── .mvn/
    └── settings.xml        # Maven settings for deployment
```

### Creating Custom Tasks

1. Extend the `Task` class:
   ```java
   public class MyCustomTask extends Task {
       @Override
       protected boolean preCheck() {
           // Add validation logic here
           return true;
       }
       
       @Override
       protected void invoke() {
           // Add your task logic here
           logger.info("Executing custom task: {}", getName());
       }
   }
   ```

2. Use TaskFactory to create instances:
   ```java
   Workflow workflow = new Workflow();
   TaskFactory taskFactory = workflow.getTaskFactory();
   MyCustomTask task = taskFactory.buildTask(MyCustomTask.class, "MyTask");
   workflow.addTask(task);
   workflow.run();
   ```

### Extending Workflow

You can extend the `Workflow` class to create custom workflow types:

```java
public class MyCustomWorkflow extends Workflow {
    @Override
    public void buildWorkflow() {
        // Add your custom workflow logic here
        addTask(taskFactory.buildTask(MyCustomTask.class, "CustomTask1"));
        addTask(taskFactory.buildTask(MyCustomTask.class, "CustomTask2"));
    }
}
```

### Workflow Features

The `Workflow` class provides several ways to add tasks:

```java
Workflow workflow = new Workflow();
TaskFactory taskFactory = workflow.getTaskFactory();

// Method 1: Add task by class and name (creates task automatically)
workflow.addTask(SimpleTask.class, "Task1");

// Method 2: Add pre-configured task instance
SimpleTask task = taskFactory.buildTask(SimpleTask.class, "Task2");
workflow.addTask(task);

// Method 3: Build workflow in subclass
public class MyWorkflow extends Workflow {
    @Override
    public void buildWorkflow() {
        // Custom workflow building logic
        TaskFactory taskFactory = getTaskFactory();
        addTask(taskFactory.buildTask(MyCustomTask.class, "CustomTask"));
    }
}
```

### Logging Configuration

Logging is configured via Logback. The configuration file is located at:
`core/src/main/resources/logback.xml`

You can customize logging levels and appenders as needed.

## Tutorial and Examples

The project includes a comprehensive tutorial module with examples and best practices:

### Running Tutorials

```bash
# Build tutorial module
mvn -q -f tutorial/pom.xml clean compile

# Run demo workflow
mvn -q -f tutorial/pom.xml exec:java -Dexec.mainClass="org.zero2me.breezeflow.tutorial.DemoWorkflow"

# Package and run JAR
mvn -q -f tutorial/pom.xml clean package
java -jar tutorial/target/breezeflow-tutorial-1.0.0-SNAPSHOT.jar
```

### Demo Workflow Example

The tutorial includes a document processing workflow that demonstrates key BreezeFlow concepts:

```text
Root SequentialContainer
  ├─ read_document (ReadDocumentTask)
  ├─ analyze_document (ParallelContainer)
  │    ├─ calc_word_count (CalculateWordCountTask)
  │    └─ find_keyword_count (FindKeywordCountTask)
  └─ post_processing (SequentialContainer)
       ├─ print_summary (PrintSummaryTask)
       └─ send_email (SendEmailTask)
```

### Tutorial Features

- **Task Examples**: ReadDocumentTask, CalculateWordCountTask, FindKeywordCountTask, etc.
- **Container Examples**: ParallelContainer and SequentialContainer usage
- **Workflow Examples**: Document processing workflow with parallel analysis
- **Best Practices**: Proper usage patterns and common pitfalls

## Testing

The project includes unit tests using JUnit 5. Run tests with:

```bash
mvn test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

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
// TaskFactory taskFactory = new TaskFactory(facts, sessionContext, sessionConfig, listener);

// ❌ Incorrect - Direct task instantiation (will cause compilation error)
// SimpleTask task = new SimpleTask();
```

### Task Execution Pattern
Each task implements two key methods:
- `preCheck()`: Guard conditions that determine if the task should execute
- `invoke()`: Main logic that performs the task's work

```java
public class MyCustomTask extends Task {
    @Override
    protected boolean preCheck() {
        // Check if required facts are set
        return facts.get("PREREQUISITE_TASK_DONE") == Boolean.TRUE;
    }
    
    @Override
    protected void invoke() {
        // Get data from session context
        String input = (String) sessionContext.get("INPUT_DATA");
        
        // Process data
        String result = processData(input);
        
        // Store result in session context
        sessionContext.put("PROCESSED_RESULT", result);
        
        // Set fact indicating completion
        facts.put("MY_TASK_DONE", Boolean.TRUE);
    }
}
```

### Data Sharing Mechanisms

BreezeFlow provides two complementary mechanisms for sharing data between tasks:

#### Facts
Lightweight boolean flags used to indicate task completion or conditional readiness:
- Downstream tasks check facts in their `preCheck()` to decide whether they should run
- Typically boolean values with naming convention `<DOMAIN>_<ACTION>_DONE`
- Used for workflow control flow and task dependencies

```java
// Set a fact indicating task completion
facts.put("DOC_PROCESS_DONE", Boolean.TRUE);

// Check a fact in preCheck()
if (facts.get("DOC_PROCESS_DONE") != Boolean.TRUE) {
    return false; // Skip execution
}
```

#### SessionContext
Key/value store for input and output data that tasks produce and consume:
- Stores actual data values (document content, counts, processing results)
- Used for passing data between tasks
- Can store any serializable object

```java
// Store data in session context
sessionContext.put("DOC_CONTENT", documentText);
sessionContext.put("DOC_CONTENT_COUNT", wordCount);

// Retrieve data from session context
String content = (String) sessionContext.get("DOC_CONTENT");
Integer count = (Integer) sessionContext.get("DOC_CONTENT_COUNT");
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

## Best Practices

1. Keep `preCheck()` side-effect free except for setting failure facts (if desired).
2. Only set facts that represent completion or gating conditions.
3. Use descriptive variable keys; group related metrics with consistent prefixes.
4. Avoid storing large raw data redundantly—use a single source variable.
5. Limit logging of large content; prefer length summaries.
6. Keep tasks single-purpose; compose with containers for complexity.
7. Fail fast in `invoke()` if critical variables are unexpectedly null (after passing `preCheck()`).
8. Consider adding integration tests covering full workflow execution.

## Version History

- **1.0.0-SNAPSHOT**: Initial release with TaskFactory pattern, parallel/sequential execution, and comprehensive tutorial module
