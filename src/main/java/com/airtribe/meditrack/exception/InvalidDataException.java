package com.airtribe.meditrack.exception;

/**
 * Thrown when input data fails validation rules.
 */
public class InvalidDataException extends RuntimeException {

    private final String fieldName;
    private final Object invalidValue;

    public InvalidDataException(String fieldName, Object invalidValue) {
        super("Invalid value for field '" + fieldName + "': " + invalidValue);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    public InvalidDataException(String message) {
        super(message);
        this.fieldName = null;
        this.invalidValue = null;
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = null;
        this.invalidValue = null;
    }

    public String getFieldName() { return fieldName; }
    public Object getInvalidValue() { return invalidValue; }
}
