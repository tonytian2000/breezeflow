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

class CalculateWordCountTaskTest {

    static class TestListener extends WorkflowListener {
        List<WorkflowEventType> events = new ArrayList<>();
        @Override
        protected void handle(WorkflowEventType eventType, String message) {
            events.add(eventType);
        }
    }

    @Test
    void testWordCountCalculation() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        // Prepare preconditions
        String sample = "Hello world! This is a test document.";
        ctx.setVariable("DOC_CONTENT", sample);
        facts.put("DOC_PROCESS_DONE", true);

        CalculateWordCountTask task = new CalculateWordCountTask();
        task.setName("calcWordCount");
        task.inject(facts, ctx, config, listener);

        Assertions.assertTrue(task.preCheck(), "Pre-check should pass with DOC_PROCESS_DONE and DOC_CONTENT set");
        task.invoke();

        Object countObj = ctx.getVariable("DOC_CONTENT_COUNT");
        Assertions.assertNotNull(countObj, "Word count should be stored in session context");
        Assertions.assertTrue(countObj instanceof Integer, "Word count should be an Integer");
        int count = (Integer) countObj;
        Assertions.assertEquals(7, count, "Word count should match expected number of words");

        Boolean doneFlag = facts.get("DOC_CAL_WORD_COUNT_DONE");
        Assertions.assertNotNull(doneFlag, "DOC_CAL_WORD_COUNT_DONE fact should be present");
        Assertions.assertTrue(doneFlag, "DOC_CAL_WORD_COUNT_DONE should be true");
    }

    @Test
    void testPreCheckFailsWhenFactMissing() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        ctx.setVariable("DOC_CONTENT", "data");
        // DOC_PROCESS_DONE fact missing
        CalculateWordCountTask task = new CalculateWordCountTask();
        task.inject(facts, ctx, config, listener);

        Assertions.assertFalse(task.preCheck(), "Pre-check should fail when DOC_PROCESS_DONE fact missing");
    }
}
