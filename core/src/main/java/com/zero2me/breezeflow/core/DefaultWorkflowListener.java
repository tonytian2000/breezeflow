package com.zero2me.breezeflow.core;

import org.slf4j.Logger;

import com.zero2me.breezeflow.common.LogFactory;

public class DefaultWorkflowListener extends WorkflowListener {
  private Logger logger = LogFactory.getLogger(DefaultWorkflowListener.class);
  
  @Override
  protected void handle(WorkflowEventType event, String description) throws Exception {
    // do nothing
  }
}
