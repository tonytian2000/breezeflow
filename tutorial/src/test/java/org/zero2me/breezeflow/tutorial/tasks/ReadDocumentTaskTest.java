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

class ReadDocumentTaskTest {

    static class TestListener extends WorkflowListener {
        List<WorkflowEventType> events = new ArrayList<>();
        @Override
        protected void handle(WorkflowEventType eventType, String message) {
            events.add(eventType);
        }
    }

    @Test
    void testReadDocumentTaskReadsFileAndSetsContextAndFact() {
        Facts facts = new Facts();
        SessionContext ctx = new SessionContext();
        SessionConfig config = new SessionConfig();
        TestListener listener = new TestListener();

        // Use TaskFactory to ensure proper injection pattern if needed
    ReadDocumentTask task = new ReadDocumentTask();
    task.setName("readDoc");
    task.inject(facts, ctx, config, listener);

        // Pre-check should pass given demo.txt exists
        Assertions.assertTrue(task.preCheck(), "Pre-check should succeed when demo.txt exists");
    task.invoke();

        Object content = ctx.getVariable("DOC_CONTENT");
        Assertions.assertNotNull(content, "Document content should be stored in session context");
        Assertions.assertTrue(content instanceof String, "Content should be a String");
        Assertions.assertFalse(((String) content).isBlank(), "Content should not be blank");

        Boolean done = facts.get("DOC_PROCESS_DONE");
        Assertions.assertNotNull(done, "DOC_PROCESS_DONE fact should be present");
        Assertions.assertTrue(done, "DOC_PROCESS_DONE should be true");
    }
}
