# BreezeFlow Tutorial Module

This module demonstrates a document processing workflow built on the BreezeFlow engine. It focuses on the real tasks present in this module and clarifies the distinction between Facts and the SessionContext.

## Concepts

- **Facts**: Lightweight boolean (or simple state) flags used to indicate task completion or conditional readiness. Downstream tasks read facts in their `preCheck()` to decide whether they should run. Example facts: `DOC_PROCESS_DONE`, `DOC_CAL_WORD_COUNT_DONE`, `FIND_KEYWORD_DONE`, `PRINT_SUMMARY_DONE`, `EMAIL_SENT_DONE`.
- **SessionContext**: Key/value store for input and output data that tasks produce and consume (e.g. raw document content, counts, summary text). Example variables: `DOC_CONTENT`, `DOC_CONTENT_COUNT`, `SEARCH_KEY`, `FIND_KEYWORD_COUNT`, `TASK_SUMMARY`.
- **Task Execution Pattern**: Each task implements `preCheck()` (guard conditions) and `invoke()` (main logic). If `preCheck()` returns false the task skips execution logic.
- **Containers**: `SequentialContainer` executes tasks in order; `ParallelContainer` executes child tasks concurrently using a thread pool defined by `SessionConfig`.

## Available Tasks

| Task | Responsibility | Sets Facts | Produces Session Variables | Consumes |
|------|----------------|------------|----------------------------|----------|
| ReadDocumentTask | Read `demo.txt` from classpath | `DOC_PROCESS_DONE=true` | `DOC_CONTENT` | — |
| CalculateWordCountTask | Count words in document | `DOC_CAL_WORD_COUNT_DONE=true` | `DOC_CONTENT_COUNT` | `DOC_CONTENT`, fact `DOC_PROCESS_DONE` |
| FindKeywordCountTask | Count keyword occurrences | `FIND_KEYWORD_DONE=true` | `FIND_KEYWORD_COUNT` | `DOC_CONTENT`, `SEARCH_KEY`, fact `DOC_PROCESS_DONE` |
| PrintSummaryTask | Build and persist summary text | `PRINT_SUMMARY_DONE=true` | `TASK_SUMMARY` | `DOC_CONTENT_COUNT`, `FIND_KEYWORD_COUNT`, `SEARCH_KEY`, facts from analysis |
| SendEmailTask | Simulate sending summary (log) | `EMAIL_SENT_DONE=true/false` | — (uses existing) | `TASK_SUMMARY`, fact `PRINT_SUMMARY_DONE` |

## DemoWorkflow

Workflow structure:

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

Execution phases:

1. Read & flag: load file, set `DOC_PROCESS_DONE`.
2. Parallel analysis: word and keyword counts (facts set upon completion).
3. Summary: build `TASK_SUMMARY`, set `PRINT_SUMMARY_DONE`.
4. Email simulation: log send, set `EMAIL_SENT_DONE`.

## Running

Build tutorial module:

```bash
mvn -q -f tutorial/pom.xml clean compile
```

Run demo workflow:

```bash
mvn -q -f tutorial/pom.xml exec:java -Dexec.mainClass="org.zero2me.breezeflow.tutorial.DemoWorkflow"
```

Default keyword is set in `DemoWorkflow.main` (`SEARCH_KEY="license"`). Change by editing the main method or setting a different variable before invoking `run()`.

Package and run JAR:

```bash
mvn -q -f tutorial/pom.xml clean package
java -jar tutorial/target/breezeflow-tutorial-1.0.0-SNAPSHOT.jar
```

## Data Flow Summary

| Variable | Source Task | Purpose | Downstream Users |
|----------|-------------|---------|------------------|
| `DOC_CONTENT` | ReadDocumentTask | Raw document text | Word/Keyword tasks |
| `DOC_CONTENT_COUNT` | CalculateWordCountTask | Total words | PrintSummaryTask |
| `SEARCH_KEY` | Seeded in main | Keyword to count | FindKeywordCountTask, PrintSummaryTask |
| `FIND_KEYWORD_COUNT` | FindKeywordCountTask | Occurrence count | PrintSummaryTask |
| `TASK_SUMMARY` | PrintSummaryTask | Formatted summary | SendEmailTask |

| Fact | Set By | Meaning | Checked By |
|------|--------|---------|------------|
| `DOC_PROCESS_DONE` | ReadDocumentTask | Document content available | CalculateWordCountTask, FindKeywordCountTask |
| `DOC_CAL_WORD_COUNT_DONE` | CalculateWordCountTask | Word count computed | PrintSummaryTask |
| `FIND_KEYWORD_DONE` | FindKeywordCountTask | Keyword count computed | PrintSummaryTask |
| `PRINT_SUMMARY_DONE` | PrintSummaryTask | Summary ready | SendEmailTask |
| `EMAIL_SENT_DONE` | SendEmailTask | Email simulation performed | (terminal) |

## Code Excerpt

```java
public void buildWorkflow() {
    rootContainer.addTask(taskFactory.buildTask(ReadDocumentTask.class, "read_document"));
    ParallelContainer parallel = taskFactory.buildTask(ParallelContainer.class, "analyze_document");
    parallel.addTask(taskFactory.buildTask(CalculateWordCountTask.class, "calc_word_count"));
    parallel.addTask(taskFactory.buildTask(FindKeywordCountTask.class, "find_keyword_count"));
    rootContainer.addTask(parallel);
    SequentialContainer post = taskFactory.buildTask(SequentialContainer.class, "post_processing");
    post.addTask(taskFactory.buildTask(PrintSummaryTask.class, "print_summary"));
    post.addTask(taskFactory.buildTask(SendEmailTask.class, "send_email"));
    rootContainer.addTask(post);
}
```

## Sample Output (Truncated)

```text
... ReadDocumentTask : resource 'demo.txt' read successfully (8866 bytes)
... CalculateWordCountTask : calculated word count 1210
... FindKeywordCountTask : keyword 'license' occurred 22 times
... PrintSummaryTask :
=== Document Processing Summary ===
Total Word Count       : 1210
Search Keyword         : license
Keyword Occurrence Count: 22
===================================
... SendEmailTask : sending email to test@email.com with summary content length 160
```

## Extending the Demo

Add new analysis by creating a Task subclass:

1. Define `preCheck()` to guard on required facts/variables.
2. Perform computation in `invoke()`.
3. Store results in `SessionContext` and set a completion fact.
4. Attach task to existing parallel or post-processing container.
5. Optionally enhance `PrintSummaryTask` to include new metrics.

## Project Structure

```text
tutorial/
  ├── src/main/java/com/zero2me/breezeflow/tutorial/
  │   ├── tasks/
  │   │   ├── ReadDocumentTask.java
  │   │   ├── CalculateWordCountTask.java
  │   │   ├── FindKeywordCountTask.java
  │   │   ├── PrintSummaryTask.java
  │   │   └── SendEmailTask.java
  │   └── DemoWorkflow.java
  ├── pom.xml
  └── README.md
```

## Best Practices

1. Keep `preCheck()` side-effect free except for setting failure facts (if desired).
2. Only set facts that represent completion or gating conditions.
3. Use descriptive variable keys; group related metrics with consistent prefixes.
4. Avoid storing large raw data redundantly—use a single source variable (e.g., `DOC_CONTENT`).
5. Limit logging of large content; prefer length summaries.
6. Keep tasks single-purpose; compose with containers for complexity.
7. Fail fast in `invoke()` if critical variables are unexpectedly null (after passing `preCheck()`).
8. Consider adding integration tests covering full workflow execution.

## Troubleshooting

| Symptom | Likely Cause | Resolution |
|---------|--------------|------------|
| Task skipped | `preCheck()` returned false | Ensure required fact/variable produced by prior task |
| Word/keyword counts zero | Wrong `SEARCH_KEY` or empty content | Verify `SEARCH_KEY` value and document load |
| Summary missing | Facts not set by analysis tasks | Check logs for warnings from analysis tasks |
| Email not "sent" | `PRINT_SUMMARY_DONE` false or no `TASK_SUMMARY` | Confirm summary task ran successfully |

## Contributing

1. Add new tasks under `tutorial/tasks` with clear naming.
2. Update this README’s tables for new facts/variables.
3. Include unit tests for `preCheck()` and `invoke()` logic.
4. Keep public API changes in core modules documented separately.
5. Maintain consistency in fact naming: `<DOMAIN>_<ACTION>_DONE`.

---
Last updated: 2025-10-21
