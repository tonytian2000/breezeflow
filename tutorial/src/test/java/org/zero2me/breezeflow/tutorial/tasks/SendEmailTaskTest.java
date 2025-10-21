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

class SendEmailTaskTest {

    static class TestListener extends WorkflowListener {
        List<WorkflowEventType> events = new ArrayList<>();
        @Override
        protected void handle(WorkflowEventType eventType, String message) {
            events.add(eventType);
        }
    }

    @Test
    void testEmailSendSuccess() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        facts.put("PRINT_SUMMARY_DONE", true);
        ctx.setVariable("TASK_SUMMARY", "Summary line 1\nSummary line 2");

        SendEmailTask task = new SendEmailTask();
        task.inject(facts, ctx, config, listener);
        Assertions.assertTrue(task.preCheck(), "Pre-check should pass when PRINT_SUMMARY_DONE is true");
        task.invoke();
        Boolean sent = facts.get("EMAIL_SENT_DONE");
        Assertions.assertNotNull(sent, "EMAIL_SENT_DONE fact should be set");
        Assertions.assertTrue(sent, "EMAIL_SENT_DONE should be true after successful send");
    }

    @Test
    void testPreCheckFailsWhenSummaryNotDone() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        SendEmailTask task = new SendEmailTask();
        task.inject(facts, ctx, config, listener);
        Assertions.assertFalse(task.preCheck(), "Pre-check should fail without PRINT_SUMMARY_DONE fact");
        Assertions.assertNull(facts.get("EMAIL_SENT_DONE"), "EMAIL_SENT_DONE should not be set when preCheck fails");
    }
}
