package org.zero2me.breezeflow.tutorial.tasks;

import org.zero2me.breezeflow.core.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Task that reads the tutorial demo.txt document and stores its content
 * into the workflow SessionContext under key "DOC_CONTENT". It also sets a fact
 * named "DOC_PROCESS_DONE" to true to indicate the document has been processed.
 */
public class ReadDocumentTask extends Task {

	private static final String DOC_CONTENT_KEY = "DOC_CONTENT";
	private static final String DOC_DONE_FACT = "DOC_PROCESS_DONE";
	private static final String DEFAULT_RESOURCE_NAME = "demo.txt";

	// Allow overriding the resource name if needed
	private String resourceName = DEFAULT_RESOURCE_NAME; // classpath resource name

	// Package-private constructor - only TaskFactory can create instances
	public ReadDocumentTask() {
		// default
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceName() {
		return resourceName;
	}

	@Override
	protected boolean preCheck() {
		// Check resource availability via classloader
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
			if (is == null) {
				logger.warn("ReadDocumentTask {}: resource '{}' not found on classpath", getId(), resourceName);
				return false;
			}
			return true;
		} catch (IOException e) {
			logger.warn("ReadDocumentTask {}: error accessing resource '{}': {}", getId(), resourceName, e.getMessage());
			return false;
		}
	}

	@Override
	protected void invoke() {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
			if (is == null) {
				throw new RuntimeException("Resource not found: " + resourceName);
			}
			byte[] bytes = is.readAllBytes();
			String content = new String(bytes, StandardCharsets.UTF_8);
      		logger.info(content);
			sessionContext.setVariable(DOC_CONTENT_KEY, content);
			facts.put(DOC_DONE_FACT, true);
			logger.info("ReadDocumentTask {}: resource '{}' read successfully ({} bytes)", getId(), resourceName, content.length());
		} catch (IOException e) {
			throw new RuntimeException("Failed to read resource: " + resourceName, e);
		}
	}
}
