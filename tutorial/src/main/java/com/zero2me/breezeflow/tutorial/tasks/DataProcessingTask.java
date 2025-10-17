package com.zero2me.breezeflow.tutorial.tasks;

import com.zero2me.breezeflow.core.Task;
import java.util.List;

/**
 * A more complex task that demonstrates data processing capabilities.
 * This task processes a list of numbers and performs various operations.
 */
public class DataProcessingTask extends Task {
    
    private String inputKey = "numbers";
    private String outputKey = "processedNumbers";
    private Operation operation = Operation.SUM;
    
    // Package-private constructor - only TaskFactory can create instances
    public DataProcessingTask() {
        // No need to call super() explicitly
    }
    
    public enum Operation {
        SUM, AVERAGE, MAX, MIN, COUNT
    }
    
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
    
    public void setOperation(Operation operation) {
        this.operation = operation;
    }
    
    public Operation getOperation() {
        return operation;
    }
    
    @Override
    protected boolean preCheck() {
        if (facts == null) {
            logger.warn("DataProcessingTask {}: Facts is null", getId());
            return false;
        }
        
        if (facts.getFact(inputKey) == null) {
            logger.warn("DataProcessingTask {}: Input key '{}' not found in facts", getId(), inputKey);
            return false;
        }
        
        boolean inputData = (Boolean)facts.get(inputKey);
        if (inputData) {
            return true;
        }
        
        return false;
    }
    
    @Override
    protected void invoke() {
        logger.info("DataProcessingTask {}: Starting data processing with operation: {}", getId(), operation);
        
        List<Number> numbers = List.of(1, 2, 3, 4, 5);
        Number result = processNumbers(numbers, operation);
        
        // Store the result in facts
        sessionContext.setVariable(outputKey, result);
        
        logger.info("DataProcessingTask {}: Completed processing. Result: {} = {}", 
                   getId(), operation, result);
    }
    
    private Number processNumbers(List<Number> numbers, Operation operation) {
        switch (operation) {
            case SUM:
                return numbers.stream()
                    .mapToDouble(Number::doubleValue)
                    .sum();
                    
            case AVERAGE:
                return numbers.stream()
                    .mapToDouble(Number::doubleValue)
                    .average()
                    .orElse(0.0);
                    
            case MAX:
                return numbers.stream()
                    .mapToDouble(Number::doubleValue)
                    .max()
                    .orElse(0.0);
                    
            case MIN:
                return numbers.stream()
                    .mapToDouble(Number::doubleValue)
                    .min()
                    .orElse(0.0);
                    
            case COUNT:
                return numbers.size();
                
            default:
                throw new IllegalArgumentException("Unknown operation: " + operation);
        }
    }
}
