package org.zero2me.breezeflow.tutorial;

import org.zero2me.breezeflow.core.Workflow;
import org.zero2me.breezeflow.core.SequentialContainer;
import org.zero2me.breezeflow.core.ParallelContainer;
import org.zero2me.breezeflow.core.WorkflowExecutionException;
import org.zero2me.breezeflow.tutorial.tasks.ReadDocumentTask;
import org.zero2me.breezeflow.tutorial.tasks.CalculateWordCountTask;
import org.zero2me.breezeflow.tutorial.tasks.FindKeywordCountTask;
import org.zero2me.breezeflow.tutorial.tasks.PrintSummaryTask;
import org.zero2me.breezeflow.tutorial.tasks.SendEmailTask;

/**
 * DemoWorkflow builds an example workflow with the following structure:
 *
 * Root SequentialContainer:
 *   1. ReadDocumentTask
 *   2. ParallelContainer
 *        - CalculateWordCountTask
 *        - FindKeywordCountTask
 *   3. SequentialContainer
 *        - PrintSummaryTask
 *        - SendEmailTask
 *
 * Before running you may set initial data like SEARCH_KEY in session context or facts.
 */
public class DemoWorkflow extends Workflow {

	public DemoWorkflow() {
		super();
	}

	@Override
	public void buildWorkflow() {
		// 1. Read document
		addTask(ReadDocumentTask.class, "read_document");

		// 2. Parallel analysis (word count + keyword count)
		ParallelContainer parallel = (ParallelContainer)buildTask(ParallelContainer.class, "analyze_document");
		parallel.addTask(buildTask(CalculateWordCountTask.class, "calc_word_count"));
		parallel.addTask(buildTask(FindKeywordCountTask.class, "find_keyword_count"));
		addTask(parallel);

		// 3. Post processing sequential (print summary + send email)
		SequentialContainer post = (SequentialContainer)buildTask(SequentialContainer.class, "post_processing");
		post.addTask(buildTask(PrintSummaryTask.class, "print_summary"));
		post.addTask(buildTask(SendEmailTask.class, "send_email"));
		addTask(post);
	}

	/**
	 * Convenience main method to run the demo workflow.
	 */
	public static void main(String[] args) {
		DemoWorkflow workflow = new DemoWorkflow();
		// Seed SEARCH_KEY in session context
		workflow.getSessionContext().setVariable("SEARCH_KEY", "license");

		try {
			workflow.run();
		} catch (WorkflowExecutionException e) {
			System.err.println("Workflow failed: " + e.getMessage());
		}
	}
}
