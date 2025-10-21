package org.zero2me.breezeflow.tutorial.tasks;

import org.zero2me.breezeflow.core.Facts;
import org.zero2me.breezeflow.core.SessionConfig;
import org.zero2me.breezeflow.core.SessionContext;
import org.zero2me.breezeflow.core.WorkflowEventType;
import org.zero2me.breezeflow.core.WorkflowListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PrintSummaryTaskTest {

    static class TestListener extends WorkflowListener {
        List<WorkflowEventType> events = new ArrayList<>();
        @Override
        protected void handle(WorkflowEventType eventType, String message) {
            events.add(eventType);
        }
    }

    @Test
    void testSummaryWhenWordAndKeywordCountsPresent() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        facts.put("DOC_CAL_WORD_COUNT_DONE", true);
        facts.put("FIND_KEYWORD_DONE", true);
        ctx.setVariable("DOC_CONTENT_COUNT", 123);
        ctx.setVariable("SEARCH_KEY", "alpha");
        ctx.setVariable("FIND_KEYWORD_COUNT", 5);

        PrintSummaryTask task = new PrintSummaryTask();
        task.inject(facts, ctx, config, listener);
        Assertions.assertTrue(task.preCheck(), "Pre-check should pass when at least one prerequisite fact is true");
        task.invoke();

        Boolean done = facts.get("PRINT_SUMMARY_DONE");
        Assertions.assertNotNull(done);
        Assertions.assertTrue(done);
        Object summary = ctx.getVariable("TASK_SUMMARY");
        Assertions.assertNotNull(summary, "TASK_SUMMARY should be stored in session context");
        Assertions.assertTrue(summary.toString().contains("Total Word Count"));
    }

    @Test
    void testSummaryPreCheckFailsWhenNoPrereqs() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        PrintSummaryTask task = new PrintSummaryTask();
        task.inject(facts, ctx, config, listener);
        Assertions.assertFalse(task.preCheck(), "Pre-check should fail when neither fact is true");
        // Ensure invoke not called; summary should not exist
        Assertions.assertNull(ctx.getVariable("TASK_SUMMARY"));
    }

    @Test
    void testSummaryWithOnlyWordCount() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        facts.put("DOC_CAL_WORD_COUNT_DONE", true);
        ctx.setVariable("DOC_CONTENT_COUNT", 10);

        PrintSummaryTask task = new PrintSummaryTask();
        task.inject(facts, ctx, config, listener);
        Assertions.assertTrue(task.preCheck());
        task.invoke();
        Boolean done = facts.get("PRINT_SUMMARY_DONE");
        Assertions.assertTrue(done);
        Assertions.assertNotNull(ctx.getVariable("TASK_SUMMARY"));
    }
}
