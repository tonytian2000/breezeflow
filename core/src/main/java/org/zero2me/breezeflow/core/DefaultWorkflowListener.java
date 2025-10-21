package org.zero2me.breezeflow.core;

import org.slf4j.Logger;

import org.zero2me.breezeflow.common.LogFactory;

/**
 * Default implementation of the WorkflowListener that logs workflow events.
 * This class provides a simple implementation that logs all workflow events
 * at the INFO level. It can be extended or replaced with custom implementations
 * to handle workflow events differently.
 */
public class DefaultWorkflowListener extends WorkflowListener {
  /**
   * Logger instance for this class.
   */
  private Logger logger = LogFactory.getLogger(DefaultWorkflowListener.class);
  
  /**
   * Handles workflow events by logging them at the INFO level.
   * 
   * @param event the type of workflow event
   * @param description a description of the event
   * @throws Exception if an error occurs while handling the event
   */
  @Override
  protected void handle(WorkflowEventType event, String description) throws Exception {
    // Simply log the event and description
    logger.info("Event: " + event + " Description: " + description);
  }
}
