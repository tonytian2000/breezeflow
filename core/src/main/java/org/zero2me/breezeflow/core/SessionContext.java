package org.zero2me.breezeflow.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

/**
 * Represents the execution context for a workflow session.
 * 
 * SessionContext provides a thread-safe storage for variables that need to be
 * shared between tasks during workflow execution. It acts as a shared memory
 * space where tasks can store and retrieve data as the workflow progresses.
 */
@Data
public class SessionContext {
    /**
     * Thread-safe map to store context variables.
     */
    private final Map<String, Object> contextData = new ConcurrentHashMap<>();

    /**
     * Sets a variable in the execution context.
     * If a variable with the same name already exists, it will be overwritten.
     *
     * @param name the variable name
     * @param value the variable value
     */
    public void setVariable(String name, Object value) {
        contextData.put(name, value);
    }

    /**
     * Gets a variable from the execution context.
     *
     * @param name the variable name
     * @return the variable value, or null if not found
     */
    public Object getVariable(String name) {
        return contextData.get(name);
    }

    /**
     * Checks whether a variable is present in the execution context.
     *
     * @param name the variable name to check
     * @return {@code true} if the variable name exists, {@code false} otherwise
     */
    public Boolean hasVariable(String name) {
        return contextData.containsKey(name);
    }
}
