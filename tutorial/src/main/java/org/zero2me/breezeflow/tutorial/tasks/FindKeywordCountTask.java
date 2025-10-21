package org.zero2me.breezeflow.tutorial.tasks;

import org.zero2me.breezeflow.core.Task;

/**
 * Task that finds the occurrence count of a given keyword within the document content.
 * Preconditions (preCheck):
 *  - Fact "DOC_PROCESS_DONE" must be true (document content prepared).
 *  - SessionContext must contain non-blank String variable "SEARCH_KEY".
 *  - SessionContext must contain non-blank String variable "DOC_CONTENT".
 * Actions (invoke):
 *  - Retrieve keyword (SEARCH_KEY) and content (DOC_CONTENT).
 *  - Count occurrences (case-insensitive) of whole-word matches.
 *  - Store the count into SessionContext under key "FIND_KEYWORD_COUNT".
 *  - Set fact "FIND_KEYWORD_DONE" to true.
 */
public class FindKeywordCountTask extends Task {

	private static final String DOC_DONE_FACT = "DOC_PROCESS_DONE";
	private static final String SEARCH_KEY_KEY = "SEARCH_KEY";
	private static final String DOC_CONTENT_KEY = "DOC_CONTENT";
	private static final String RESULT_KEY = "FIND_KEYWORD_COUNT";
	private static final String DONE_FACT = "FIND_KEYWORD_DONE";

	// Package-private constructor (TaskFactory usage pattern)
	public FindKeywordCountTask() {}

	@Override
	protected boolean preCheck() {
		Boolean docDone = facts.get(DOC_DONE_FACT);
		if (docDone == null || !docDone) {
			logger.warn("FindKeywordCountTask {}: preCheck failed - '{}' fact missing or false", getId(), DOC_DONE_FACT);
			facts.put(DONE_FACT, false);
      return false;
		}
		
		return true;
	}

	@Override
	protected void invoke() {
		Object keywordObj = sessionContext.getVariable(SEARCH_KEY_KEY);
		if (!(keywordObj instanceof String) || ((String) keywordObj).isBlank()) {
      facts.put(DONE_FACT, false);
			logger.warn("FindKeywordCountTask {}: preCheck failed - SEARCH_KEY missing or blank", getId());
      return;
		}
		Object contentObj = sessionContext.getVariable(DOC_CONTENT_KEY);
		if (!(contentObj instanceof String) || ((String) contentObj).isBlank()) {
      facts.put(DONE_FACT, false);
			logger.warn("FindKeywordCountTask {}: preCheck failed - DOC_CONTENT missing or blank", getId());
			return;
		}

		String keyword = ((String) sessionContext.getVariable(SEARCH_KEY_KEY)).trim();
		String content = (String) sessionContext.getVariable(DOC_CONTENT_KEY);
		int count = countOccurrences(content, keyword);
		sessionContext.setVariable(RESULT_KEY, count);
		facts.put(DONE_FACT, true);
		logger.info("FindKeywordCountTask {}: keyword '{}' occurred {} times", getId(), keyword, count);
	}

	private int countOccurrences(String text, String keyword) {
		if (text == null || keyword == null || keyword.isBlank()) {
			return 0;
		}
		// Case-insensitive whole-word match: split into tokens and compare normalized
		String normKey = keyword.toLowerCase();
		String[] tokens = text.split("\\s+");
		int c = 0;
		for (String token : tokens) {
			String cleaned = token.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
			if (!cleaned.isEmpty() && cleaned.equals(normKey)) {
				c++;
			}
		}
		return c;
	}
}
