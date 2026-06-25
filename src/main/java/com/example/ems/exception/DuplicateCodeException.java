package com.example.ems.exception;

/**
 * Exception thrown when a unique code validation fails (e.g. duplicate employee or department code).
 */
public class DuplicateCodeException extends RuntimeException {

    private final String field;

    /**
     * Constructs a new DuplicateCodeException.
     *
     * @param field   the field name that is duplicate
     * @param message the validation message
     */
    public DuplicateCodeException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
