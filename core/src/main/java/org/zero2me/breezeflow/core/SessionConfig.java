package org.zero2me.breezeflow.core;

import lombok.Data;

/**
 * Configuration class for workflow execution sessions.
 * This class holds configuration parameters that control the behavior
 * of workflow execution, such as thread pool size for parallel tasks.
 */
@Data
public class SessionConfig {
  /**
   * The size of the thread pool used for parallel task execution.
   * Default value is 2 threads.
   */
  private int threadPoolSize = 2;
}
