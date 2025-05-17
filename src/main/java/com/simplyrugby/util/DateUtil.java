package com.simplyrugby.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

/**
 * Utility class for date operations.
 */
public class DateUtil {

    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    private static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Parse a date string into a Date object
     *
     * @param dateStr The date string to parse
     * @return The parsed Date object
     * @throws ValidationException If the date string is invalid
     */
    public static Date parseDate(String dateStr) throws ValidationException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ValidationException("Date cannot be empty", (List<String>) null);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            sdf.setLenient(false);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new ValidationException("Invalid date format. Please use " + DEFAULT_DATE_FORMAT, e);
        }
    }

    /**
     * Parse a date string into a Date object using the specified format
     *
     * @param dateStr The date string to parse
     * @param format The date format to use
     * @return The parsed Date object
     * @throws ValidationException If the date string is invalid
     */
    public static Date parseDate(String dateStr, String format) throws ValidationException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ValidationException("Date cannot be empty", (List<String>) null);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new ValidationException("Invalid date format. Please use " + format, e);
        }
    }

    /**
     * Format a Date object as a string
     *
     * @param date The Date object to format
     * @return The formatted date string
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Format a Date object as a string using the specified format
     *
     * @param date The Date object to format
     * @param format The date format to use
     * @return The formatted date string
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Format a Date object for storage in the database
     *
     * @param date The Date object to format
     * @return The formatted date string
     */
    public static String formatDateForDatabase(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATABASE_DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Check if a date is in the future
     *
     * @param date The date to check
     * @return true if the date is in the future, false otherwise
     */
    public static boolean isFutureDate(Date date) {
        if (date == null) {
            return false;
        }

        return date.after(new Date());
    }

    /**
     * Check if a date is in the past
     *
     * @param date The date to check
     * @return true if the date is in the past, false otherwise
     */
    public static boolean isPastDate(Date date) {
        if (date == null) {
            return false;
        }

        return date.before(new Date());
    }

    /**
     * Calculate the age from a birth date
     *
     * @param birthDate The birth date
     * @return The age in years
     */
    public static int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }

        LocalDate birthLocalDate = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate currentDate = LocalDate.now();

        return currentDate.getYear() - birthLocalDate.getYear() -
                (currentDate.getDayOfYear() < birthLocalDate.getDayOfYear() ? 1 : 0);
    }

    /**
     * Validate a date string
     *
     * @param dateStr The date string to validate
     * @return true if the date string is valid, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Validate a date string using the specified format
     *
     * @param dateStr The date string to validate
     * @param format The date format to use
     * @return true if the date string is valid, false otherwise
     */
    public static boolean isValidDate(String dateStr, String format) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Get the current date as a Date object
     *
     * @return The current date
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * Get the current date as a formatted string
     *
     * @return The current date string
     */
    public static String getCurrentDateString() {
        return formatDate(new Date());
    }

    /**
     * Get the current date as a formatted string using the specified format
     *
     * @param format The date format to use
     * @return The current date string
     */
    public static String getCurrentDateString(String format) {
        return formatDate(new Date(), format);
    }

    /**
     * Add days to a date
     *
     * @param date The date to add days to
     * @param days The number of days to add
     * @return The new date
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate newDate = localDate.plusDays(days);

        return Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}