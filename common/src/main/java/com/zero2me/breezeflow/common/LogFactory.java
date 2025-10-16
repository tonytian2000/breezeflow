package com.zero2me.breezeflow.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common logger factory for BreezeFlow components.
 * Provides a centralized way to create loggers across all modules.
 */
public final class LogFactory {
    
    private LogFactory() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates a logger for the specified class.
     *
     * @param clazz the class to create a logger for
     * @return a logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Creates a logger with the specified name.
     *
     * @param name the name of the logger
     * @return a logger instance
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}
