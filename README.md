# BreezeFlow

A lightweight, flexible workflow engine for Java applications, built with Maven and designed for JDK 11.

## Overview

BreezeFlow is a lightweight workflow engine for building data processing pipelines in Java. It provides:
- **Simple API** for defining workflows and tasks
- **Sequential & Parallel** task execution
- **Data Sharing** between tasks via Facts and SessionContext
- **Extensible** task and workflow framework

## Architecture   
![alt text](https://github.com/tonytian2000/breezeflow/blob/main/workflow.png?raw=true)

## Features

- **Simple API**: Easy-to-use workflow builder methods
- **Parallel & Sequential Execution**: Run tasks concurrently or in order
- **Data Sharing**: Facts for conditions, SessionContext for data
- **Thread Pool Management**: Configurable parallel execution
- **Extensible**: Create custom tasks and workflows
- **Comprehensive Logging**: Built-in SLF4J/Logback support
- **JDK 11+**: Modern Java support

## Module Structure

- **Core Module** (`breezeflow-core`): The workflow engine
- **Tutorial Module** (`breezeflow-tutorial`): Examples and best practices

See the [tutorial README](tutorial/README.md) for detailed examples.

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

If use library directly:  
first copy the breezeflow-1.0.0-SNAPSHOT.jar to the lib directory.   
then add below into the POM.xml.  
```xml
<repositories>
    <repository>
        <id>local-maven-repo</id>
        <url>file:///${project.basedir}/lib</url>
    </repository>
</repositories>

<dependency>
    <groupId>org.zero2me</groupId>
    <artifactId>breezeflow</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/breezeflow-1.0.0-SNAPSHOT.jar</systemPath>
</dependency>
```

### Basic Usage

```java
import org.zero2me.breezeflow.core.*;

// Create a workflow
Workflow workflow = new Workflow();

// Add tasks using the Workflow API
workflow.addTask(SimpleTask.class, "Task1");
workflow.addTask(SimpleTask.class, "Task2");

// Execute the workflow
workflow.run();
```

### Parallel Execution

```java
import org.zero2me.breezeflow.core.*;

// Create workflow and configure thread pool
Workflow workflow = new Workflow();
workflow.getSessionConfig().setThreadPoolSize(4);

// Create parallel container
ParallelContainer parallel = (ParallelContainer) workflow.buildTask(
    ParallelContainer.class, "ParallelTasks");

// Add tasks to parallel container
parallel.addTask(workflow.buildTask(SimpleTask.class, "Task1"));
parallel.addTask(workflow.buildTask(SimpleTask.class, "Task2"));
parallel.addTask(workflow.buildTask(SimpleTask.class, "Task3"));

// Add parallel container to workflow
workflow.addTask(parallel);

// Execute workflow
workflow.run();
```

## Building the Project

### Prerequisites
- JDK 11 or higher
- Maven 3.6+

### Commands

```bash
# Build all modules
mvn clean package

# Run tests
mvn test

# Install locally
mvn clean install
```

### Project Structure
```
breezeflow/
├── pom.xml                 # Parent POM
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

Extend the `Task` class and implement two methods:

```java
public class MyCustomTask extends Task {
    @Override
    protected boolean preCheck() {
        // Check if this task should run
        // Return false to skip execution
        return facts.get("PREREQUISITE_DONE") == Boolean.TRUE;
    }
    
    @Override
    protected void invoke() {
        // Your task logic here
        logger.info("Executing: {}", getName());
        
        // Get data from session
        String input = (String) sessionContext.getVariable("INPUT");
        
        // Process and store result
        String result = process(input);
        sessionContext.setVariable("OUTPUT", result);
        
        // Set completion fact
        facts.put("MY_TASK_DONE", Boolean.TRUE);
    }
}
```

Use your custom task:

```java
Workflow workflow = new Workflow();
workflow.addTask(MyCustomTask.class, "MyTask");
workflow.run();
```

### Creating Custom Workflows

Extend `Workflow` and override `buildWorkflow()`:

```java
public class MyCustomWorkflow extends Workflow {
    @Override
    protected void buildWorkflow() {
        // Build your workflow here
        addTask(MyCustomTask.class, "Task1");
        addTask(MyCustomTask.class, "Task2");
        
        // Add parallel tasks
        ParallelContainer parallel = (ParallelContainer) 
            buildTask(ParallelContainer.class, "ParallelPhase");
        parallel.addTask(buildTask(MyCustomTask.class, "Task3"));
        parallel.addTask(buildTask(MyCustomTask.class, "Task4"));
        addTask(parallel);
    }
}

// Usage
MyCustomWorkflow workflow = new MyCustomWorkflow();
workflow.run();
```

### Logging Configuration

Logging is configured via Logback: `core/src/main/resources/logback.xml`

## Examples

See the [tutorial module](tutorial/) for complete examples including:
- Document processing workflow
- Parallel task execution
- Facts and SessionContext usage
- Custom task implementations

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Submit a pull request

## Key Concepts

### Task Lifecycle

Each task has two execution phases:

1. **`preCheck()`**: Validates if the task should run
   - Check facts/conditions
   - Return `false` to skip execution
   - Should be side-effect free

2. **`invoke()`**: Performs the actual work
   - Access data from SessionContext
   - Process and store results
   - Set completion facts

### Data Sharing

**Facts**: Boolean flags for task dependencies
```java
// Set fact
facts.put("DOC_PROCESSED", Boolean.TRUE);

// Check fact in preCheck()
return facts.get("DOC_PROCESSED") == Boolean.TRUE;
```

**SessionContext**: Data storage for passing values between tasks
```java
// Store data
sessionContext.setVariable("DOC_CONTENT", content);

// Retrieve data
String content = (String) sessionContext.getVariable("DOC_CONTENT");
```

## Best Practices

1. **Use Workflow API**: Always use `workflow.addTask()` and `workflow.buildTask()` - never access internal components directly
2. **Keep preCheck() pure**: No side effects except setting failure facts
3. **Use descriptive names**: Clear fact and variable naming (e.g., `DOC_PROCESSED`, `WORD_COUNT`)
4. **Single responsibility**: One task, one purpose
5. **Fail fast**: Validate critical data early in `invoke()`
6. **Proper logging**: Log actions, not large data dumps
7. **Test workflows**: Add integration tests for complete workflows

---

**Version**: 1.0.0-SNAPSHOT | **License**: MIT
