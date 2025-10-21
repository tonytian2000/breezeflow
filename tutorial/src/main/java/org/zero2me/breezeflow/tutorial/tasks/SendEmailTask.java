package org.zero2me.breezeflow.tutorial.tasks;

import org.zero2me.breezeflow.core.Task;

/**
 * Task that simulates sending a summary email.
 * Preconditions: fact "PRINT_SUMMARY_DONE" must be true.
 * Invoke: retrieves summary from SessionContext key "TASK_SUMMARY" and logs simulated send to a fixed email.
 * Sets fact "EMAIL_SENT_DONE" = true on success.
 */
public class SendEmailTask extends Task {

	private static final String SUMMARY_DONE_FACT = "PRINT_SUMMARY_DONE";
	private static final String SUMMARY_KEY = "TASK_SUMMARY";
	private static final String TARGET_EMAIL = "test@email.com";
	private static final String EMAIL_SENT_DONE_FACT = "EMAIL_SENT_DONE";

	// Package-private constructor
	public SendEmailTask() {}

	@Override
	protected boolean preCheck() {
		Boolean summaryDone = facts.get(SUMMARY_DONE_FACT);
		if (summaryDone == null || !summaryDone) {
			logger.warn("SendEmailTask {}: preCheck failed - '{}' fact not true", getId(), SUMMARY_DONE_FACT);
			return false;
		}

		// Summary can be optional; if missing we still proceed but log a warning.
		return true;
	}

	@Override
	protected void invoke() {
		Object summaryObj = sessionContext.getVariable(SUMMARY_KEY);
		if (summaryObj == null) {
		  logger.warn("SendEmailTask {}: no summary content found in session context under key '{}'", getId(), SUMMARY_KEY);
		  facts.put(EMAIL_SENT_DONE_FACT, false);
		  return;
		}

		String summary = summaryObj instanceof String ? (String) summaryObj : "(No summary content found)";

		// Simulate sending email (in real scenario integrate with SMTP or service client)
		logger.info("SendEmailTask {}: sending email to {} with summary content length {}", getId(), TARGET_EMAIL, summary.length());
		logger.debug("Email content:\n{}", summary);
		facts.put(EMAIL_SENT_DONE_FACT, true);
	}
}
