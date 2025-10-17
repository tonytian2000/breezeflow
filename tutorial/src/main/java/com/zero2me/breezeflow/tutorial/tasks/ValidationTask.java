package com.zero2me.breezeflow.tutorial.tasks;

import com.zero2me.breezeflow.core.Task;

import java.util.Map;
import java.util.HashMap;

/**
 * A task that demonstrates validation capabilities.
 * This task validates input data and stores validation results.
 */
public class ValidationTask extends Task {
    
    private String inputKey = "data";
    private String outputKey = "validationResult";
    private ValidationRule rule = ValidationRule.NOT_NULL;
    
    // Package-private constructor - only TaskFactory can create instances
    public ValidationTask() {
        // No need to call super() explicitly
    }
    
    public enum ValidationRule {
        NOT_NULL,
        NOT_EMPTY,
        POSITIVE_NUMBER,
        EMAIL_FORMAT,
        MIN_LENGTH
    }
    
    private int minLength = 1;
    
    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }
    
    public String getInputKey() {
        return inputKey;
    }
    
    public void setOutputKey(String outputKey) {
        this.outputKey = outputKey;
    }
    
    public String getOutputKey() {
        return outputKey;
    }
    
    public void setRule(ValidationRule rule) {
        this.rule = rule;
    }
    
    public ValidationRule getRule() {
        return rule;
    }
    
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
    
    public int getMinLength() {
        return minLength;
    }
    
    @Override
    protected boolean preCheck() {
        if (facts == null) {
            logger.warn("ValidationTask {}: Facts is null", getId());
            return false;
        }
        
        if (facts.getFact(inputKey) == null) {
            logger.warn("ValidationTask {}: Input key '{}' not found in facts", getId(), inputKey);
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void invoke() {
        logger.info("ValidationTask {}: Starting validation with rule: {}", getId(), rule);
        
        Object inputData = facts.get(inputKey);
        boolean isValid = validate(inputData, rule);
        
        // Create validation result
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", isValid);
        validationResult.put("rule", rule.toString());
        validationResult.put("inputKey", inputKey);
        validationResult.put("message", isValid ? "Validation passed" : "Validation failed");
        
        // Store the result in facts
        sessionContext.setVariable(outputKey, validationResult);
        
        logger.info("ValidationTask {}: Validation completed. Result: {}", getId(), isValid);
    }
    
    private boolean validate(Object data, ValidationRule rule) {
        switch (rule) {
            case NOT_NULL:
                return data != null;
                
            case NOT_EMPTY:
                if (data == null) return false;
                if (data instanceof String) {
                    return !((String) data).trim().isEmpty();
                }
                if (data instanceof java.util.Collection) {
                    return !((java.util.Collection<?>) data).isEmpty();
                }
                return true;
                
            case POSITIVE_NUMBER:
                if (data instanceof Number) {
                    return ((Number) data).doubleValue() > 0;
                }
                return false;
                
            case EMAIL_FORMAT:
                if (data instanceof String) {
                    String email = (String) data;
                    return email.contains("@") && email.contains(".");
                }
                return false;
                
            case MIN_LENGTH:
                if (data instanceof String) {
                    return ((String) data).length() >= minLength;
                }
                return false;
                
            default:
                return false;
        }
    }
}
