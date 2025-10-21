package org.zero2me.breezeflow.tutorial.tasks;

import org.zero2me.breezeflow.core.Task;

/**
 * Task that calculates word count from previously read document content.
 * Preconditions:
 *  - Fact "DOC_PROCESS_DONE" must be present and true (document already processed/read).
 * Actions:
 *  - Retrieve content from SessionContext key "DOC_CONTENT".
 *  - Calculate word count (splitting on whitespace).
 *  - Store count in SessionContext under key "DOC_CONTENT_COUNT".
 *  - Set fact "DOC_CAL_WORD_COUNT_DONE" to true.
 */
public class CalculateWordCountTask extends Task {

	private static final String DOC_DONE_FACT = "DOC_PROCESS_DONE";
	private static final String DOC_CONTENT_KEY = "DOC_CONTENT";
	private static final String DOC_CONTENT_COUNT_KEY = "DOC_CONTENT_COUNT";
	private static final String WORD_COUNT_DONE_FACT = "DOC_CAL_WORD_COUNT_DONE";

	// Package-private constructor (TaskFactory usage pattern)
	public CalculateWordCountTask() {
	}

	@Override
	protected boolean preCheck() {
		Boolean done = facts.get(DOC_DONE_FACT);
		if (done == null || !done) {
			logger.warn("CalculateWordCountTask {}: preCheck failed - '{}' fact missing or false", getId(), DOC_DONE_FACT);
      facts.put(WORD_COUNT_DONE_FACT, false);
			return false;
		}

		return true;
	}

	@Override
	protected void invoke() {
		String content = (String) sessionContext.getVariable(DOC_CONTENT_KEY);
    if (content == null) {
      logger.warn("CalculateWordCountTask {}: DOC_CONTENT is null in session context", getId());
      facts.put(WORD_COUNT_DONE_FACT, false);
      return;
    }

		int wordCount = calculateWordCount(content);
		sessionContext.setVariable(DOC_CONTENT_COUNT_KEY, wordCount);
		facts.put(WORD_COUNT_DONE_FACT, true);
		logger.info("CalculateWordCountTask {}: calculated word count {}", getId(), wordCount);
	}

	private int calculateWordCount(String content) {
		if (content == null || content.isBlank()) {
			return 0;
		}
		// Split on one or more whitespace characters; filter empty tokens just in case
		String[] tokens = content.trim().split("\\s+");
		int count = 0;
		for (String token : tokens) {
			if (!token.isBlank()) {
				count++;
			}
		}
		return count;
	}
}
