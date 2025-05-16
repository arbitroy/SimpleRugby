package com.simplyrugby.util;

import java.util.List;

/**
 * Exception thrown when input validation fails.
 */
public class ValidationException extends RuntimeException {
    private final List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = null;
    }

    public List<String> getErrors() {
        return errors;
    }
}