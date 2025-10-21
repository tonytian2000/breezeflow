package org.zero2me.breezeflow.common;

import java.util.Objects;

/**
 * Common validation utilities for BreezeFlow components.
 */
public final class ValidationUtils {
    
    private ValidationUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validates that the given object is not null.
     *
     * @param object the object to validate
     * @param name the name of the parameter for error messages
     * @throws IllegalArgumentException if the object is null
     */
    public static void requireNonNull(Object object, String name) {
        if (object == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }
    
    /**
     * Validates that the given string is not null or empty.
     *
     * @param string the string to validate
     * @param name the name of the parameter for error messages
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void requireNonEmpty(String string, String name) {
        requireNonNull(string, name);
        if (string.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be empty");
        }
    }
    
    /**
     * Validates that the given number is positive.
     *
     * @param number the number to validate
     * @param name the name of the parameter for error messages
     * @throws IllegalArgumentException if the number is not positive
     */
    public static void requirePositive(Number number, String name) {
        requireNonNull(number, name);
        if (number.doubleValue() <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }
}
