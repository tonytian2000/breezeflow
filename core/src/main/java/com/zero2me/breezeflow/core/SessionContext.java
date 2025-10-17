package com.zero2me.breezeflow.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionContext {
    private final Map<String, Object> dataContext = new ConcurrentHashMap<>();
    /**
     * Gets the execution variables.
     *
     * @return the execution variables
     */
    public Map<String, Object> getContextData() {
        return dataContext;
    }

    /**
     * Sets a variable in the execution context.
     *
     * @param name the variable name
     * @param value the variable value
     */
    public void setVariable(String name, Object value) {
        dataContext.put(name, value);
    }

    /**
     * Gets a variable from the execution context.
     *
     * @param name the variable name
     * @return the variable value, or null if not found
     */
    public Object getVariable(String name) {
        return dataContext.get(name);
    }
}
