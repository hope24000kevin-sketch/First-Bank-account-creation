package com.mygroup.grp1.validation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects field-level validation errors while preserving insertion order.
 */
public class ValidationResult {

    private final Map<String, String> errors = new LinkedHashMap<>();

    public void addError(String fieldKey, String message) {
        errors.put(fieldKey, message);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public String summaryText() {
        StringBuilder builder = new StringBuilder("Please fix the following issues:\n\n");
        int index = 1;
        for (String message : errors.values()) {
            builder.append(index++).append(". ").append(message).append('\n');
        }
        return builder.toString().trim();
    }
}
