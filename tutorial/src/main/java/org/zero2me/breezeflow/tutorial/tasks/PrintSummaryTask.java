package org.zero2me.breezeflow.tutorial.tasks;

import org.zero2me.breezeflow.core.Task;

/**
 * Task that prints a summary of processed document information.
 * Preconditions: At least one of the facts "FIND_KEYWORD_DONE" or "DOC_CAL_WORD_COUNT_DONE" must be true.
 * Data retrieved from SessionContext:
 *   - DOC_CONTENT_COUNT (Integer) - may be null if word count not calculated.
 *   - SEARCH_KEY (String) - keyword used in search (optional).
 *   - FIND_KEYWORD_COUNT (Integer) - keyword count (optional).
 * Action: Log a structured summary and set fact "PRINT_SUMMARY_DONE" = true.
 */
public class PrintSummaryTask extends Task {

	private static final String WORD_COUNT_DONE_FACT = "DOC_CAL_WORD_COUNT_DONE";
	private static final String KEYWORD_DONE_FACT = "FIND_KEYWORD_DONE";
	private static final String PRINT_DONE_FACT = "PRINT_SUMMARY_DONE";

	private static final String DOC_CONTENT_COUNT_KEY = "DOC_CONTENT_COUNT";
	private static final String SEARCH_KEY_KEY = "SEARCH_KEY";
	private static final String FIND_KEYWORD_COUNT_KEY = "FIND_KEYWORD_COUNT";
  	private static final String TASK_SUMMARY_KEY = "TASK_SUMMARY";

	// Package-private constructor
	public PrintSummaryTask() {}

	@Override
	protected boolean preCheck() {
		Boolean wordCountDone = facts.get(WORD_COUNT_DONE_FACT);
		Boolean keywordDone = facts.get(KEYWORD_DONE_FACT);
		if ((wordCountDone == null || !wordCountDone) && (keywordDone == null || !keywordDone)) {
			logger.warn("PrintSummaryTask {}: preCheck failed - neither '{}' nor '{}' facts are true", getId(), WORD_COUNT_DONE_FACT, KEYWORD_DONE_FACT);
			facts.put(PRINT_DONE_FACT, false);
      		return false;
		}

		return true;
	}

	@Override
	protected void invoke() {
		Integer wordCount = (Integer) sessionContext.getVariable(DOC_CONTENT_COUNT_KEY);
		String searchKey = (String) sessionContext.getVariable(SEARCH_KEY_KEY);
		Integer keywordCount = (Integer) sessionContext.getVariable(FIND_KEYWORD_COUNT_KEY);

		StringBuilder summary = new StringBuilder();
		summary.append("=== Document Processing Summary ===\n");
		if (wordCount != null) {
			summary.append("Total Word Count       : ").append(wordCount).append('\n');
		} else {
			summary.append("Total Word Count       : (not calculated)\n");
		}
		if (searchKey != null && !searchKey.isBlank()) {
			summary.append("Search Keyword         : ").append(searchKey).append('\n');
			if (keywordCount != null) {
				summary.append("Keyword Occurrence Count: ").append(keywordCount).append('\n');
			} else {
				summary.append("Keyword Occurrence Count: (not calculated)\n");
			}
		} else {
			summary.append("Search Keyword         : (none)\n");
		}
		summary.append("===================================");

		logger.info("PrintSummaryTask {}: \n{}", getId(), summary.toString());
    	// Store summary in session context for potential downstream use
		sessionContext.setVariable(TASK_SUMMARY_KEY, summary.toString());
		facts.put(PRINT_DONE_FACT, true);
	}
}
