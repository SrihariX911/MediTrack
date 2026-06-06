package com.airtribe.meditrack.util;

import com.airtribe.meditrack.exception.InvalidDataException;

/**
 * Centralised validation utilities used across the application.
 */
public final class Validator {

    private Validator() {}

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\+?[0-9]{7,15}$");
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.trim().length() <= 100;
    }

    // --- require* methods throw InvalidDataException on failure ---

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(fieldName, value);
        }
    }

    public static void requireValidName(String name) {
        if (!isValidName(name)) {
            throw new InvalidDataException("name", name);
        }
    }

    public static void requireValidEmail(String email) {
        if (!isValidEmail(email)) {
            throw new InvalidDataException("email", email);
        }
    }

    public static void requireValidPhone(String phone) {
        if (!isValidPhone(phone)) {
            throw new InvalidDataException("phone", phone);
        }
    }

    public static void requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new InvalidDataException(fieldName, value);
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidDataException(fieldName + " cannot be null", null);
        }
    }

    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw new InvalidDataException(message);
        }
    }
}
