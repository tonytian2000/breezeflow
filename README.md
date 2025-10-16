# BreezeFlow

A multi-module Java library for workflow management and execution, built with Maven and designed for JDK 17.

## Overview

BreezeFlow is a lightweight workflow engine library that provides a clean API for defining and executing workflows. It's designed to be used as a dependency in other Java projects and supports cloud artifactory deployment.

## Features

- **Multi-module Architecture**: Clean separation of concerns with `common`, `api`, and `core` modules
- **JDK 17 Support**: Built and tested with Java 17
- **SLF4J Logging**: Comprehensive logging with SLF4J and Logback
- **Async Execution**: Support for both synchronous and asynchronous workflow execution
- **Extensible**: Plugin-based architecture for custom step handlers
- **Cloud Ready**: Configured for deployment to cloud artifactory repositories

## Module Structure

### Common Module (`breezeflow-common`)
Contains shared utilities and common functionality:
- `LoggerFactory`: Centralized logger creation
- `ValidationUtils`: Common validation utilities

### API Module (`breezeflow-api`)
Defines the public interfaces and contracts:
- `WorkflowEngine`: Main engine interface
- `Workflow`: Workflow definition interface
- `WorkflowStep`: Individual step interface
- `StepHandler`: Handler interface for custom steps
- `ExecutionContext`: Execution context interface
- Result and status enums

### Core Module (`breezeflow-core`)
Contains the implementation:
- `DefaultWorkflowEngine`: Main engine implementation
- `LoggingStepHandler`: Sample step handler
- Internal execution tracking classes

## Quick Start

### Adding as a Dependency

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.zero2me.breezeflow</groupId>
    <artifactId>breezeflow-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import com.zero2me.breezeflow.core.DefaultWorkflowEngine;
import com.zero2me.breezeflow.core.LoggingStepHandler;
import com.zero2me.breezeflow.api.WorkflowEngine;

// Create engine
WorkflowEngine engine = new DefaultWorkflowEngine();

// Register step handlers
engine.registerStepHandler("logging", new LoggingStepHandler());

// Execute workflow (implementation depends on your workflow definition)
// WorkflowResult result = engine.execute(yourWorkflow);
```

## Building the Project

### Prerequisites
- JDK 17 or higher
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
├── api/                    # API interfaces module
│   ├── pom.xml
│   └── src/
├── core/                   # Core implementation module
│   ├── pom.xml
│   └── src/
└── .mvn/
    └── settings.xml        # Maven settings for deployment
```

### Adding New Step Handlers

1. Implement the `StepHandler` interface
2. Register your handler with the engine:
   ```java
   engine.registerStepHandler("your-step-type", new YourStepHandler());
   ```

### Logging Configuration

Logging is configured via Logback. The configuration file is located at:
`core/src/main/resources/logback.xml`

You can customize logging levels and appenders as needed.

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

## Version History

- **1.0.0-SNAPSHOT**: Initial release with basic workflow engine functionality