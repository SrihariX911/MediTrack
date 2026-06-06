package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.exception.InvalidDataException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Date and time formatting / parsing utilities.
 */
public final class DateUtil {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    private static final DateTimeFormatter DATETIME_FMT =
            DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT);

    private DateUtil() {}

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FMT) : "";
    }

    public static String formatDateTime(LocalDateTime dt) {
        return dt != null ? dt.format(DATETIME_FMT) : "";
    }

    public static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("date (expected " + Constants.DATE_FORMAT + ")", s);
        }
    }

    public static LocalDateTime parseDateTime(String s) {
        try {
            return LocalDateTime.parse(s.trim(), DATETIME_FMT);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException(
                    "dateTime (expected " + Constants.DATETIME_FORMAT + ")", s);
        }
    }

    public static int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public static boolean isFuture(LocalDateTime dt) {
        return dt.isAfter(LocalDateTime.now());
    }
}
