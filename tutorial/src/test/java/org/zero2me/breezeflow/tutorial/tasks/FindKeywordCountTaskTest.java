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

class FindKeywordCountTaskTest {

    static class TestListener extends WorkflowListener {
        List<WorkflowEventType> events = new ArrayList<>();
        @Override
        protected void handle(WorkflowEventType eventType, String message) {
            events.add(eventType);
        }
    }

    @Test
    void testKeywordCounting() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        ctx.setVariable("DOC_CONTENT", "Alpha beta, beta ALPHA; Beta! alpha.");
        ctx.setVariable("SEARCH_KEY", "alpha");
        facts.put("DOC_PROCESS_DONE", true);

        FindKeywordCountTask task = new FindKeywordCountTask();
        task.setName("findKeyword");
        task.inject(facts, ctx, config, listener);

        Assertions.assertTrue(task.preCheck(), "Pre-check should pass with required data");
        task.invoke();

        Object countObj = ctx.getVariable("FIND_KEYWORD_COUNT");
        Assertions.assertNotNull(countObj);
        Assertions.assertEquals(3, countObj, "Keyword 'alpha' should appear 3 times (case-insensitive, whole-word)");

        Boolean done = facts.get("FIND_KEYWORD_DONE");
        Assertions.assertNotNull(done);
        Assertions.assertTrue(done);
    }

    @Test
    void testPreCheckFailsWithoutDocDone() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        ctx.setVariable("DOC_CONTENT", "Sample text");
        ctx.setVariable("SEARCH_KEY", "sample");
        // DOC_PROCESS_DONE not set

        FindKeywordCountTask task = new FindKeywordCountTask();
        task.inject(facts, ctx, config, listener);

        Assertions.assertFalse(task.preCheck(), "Pre-check should fail when DOC_PROCESS_DONE missing");
    }
}
