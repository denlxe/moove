package util;

import exception.ValidationException;

import java.time.LocalDate;

/*
 * + turns raw text from the UI into validated values.
 * + every method throws a ValidationException with a message, so screens dont have to deal with NumberFormatException
 */
public final class Input {

    private Input() {
    }

    // requires non-blank text of at most maxLength characters
    public static String text(String text, String label, int maxLength) throws ValidationException {
        String value = text == null ? "" : text.trim();
        if (value.isEmpty()) {
            throw new ValidationException(label + " is required.");
        }
        if (value.length() > maxLength) {
            throw new ValidationException(label + " must be at most " + maxLength + " characters.");
        }
        return value;
    }

    // like text but blank is allowed and returns an empty string.
    public static String optionalText(String text, String label, int maxLength) throws ValidationException {
        String value = text == null ? "" : text.trim();
        if (value.length() > maxLength) {
            throw new ValidationException(label + " must be at most " + maxLength + " characters.");
        }
        return value;
    }

    // parses a number, ignoring a leading "PHP" and thousands separators
    public static double number(String text, String label) throws ValidationException {
        String cleaned = text == null ? "" : text.trim().replace("PHP", "").replace(",", "").trim();
        if (cleaned.isEmpty()) {
            throw new ValidationException(label + " is required.");
        }
        try {
            double value = Double.parseDouble(cleaned);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new ValidationException(label + " must be a number.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a number.");
        }
    }

    // a number strictly greater than zero
    public static double positiveNumber(String text, String label) throws ValidationException {
        double value = number(text, label);
        if (value <= 0) {
            throw new ValidationException(label + " must be greater than zero.");
        }
        return value;
    }

    // a number that is zero or more
    // note: a blank field counts as zero
    public static double nonNegativeNumber(String text, String label) throws ValidationException {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        double value = number(text, label);
        if (value < 0) {
            throw new ValidationException(label + " cannot be negative.");
        }
        return value;
    }

    // whole number strictly greater than zero
    public static int positiveInt(String text, String label) throws ValidationException {
        String cleaned = text == null ? "" : text.trim().replace(",", "");
        if (cleaned.isEmpty()) {
            throw new ValidationException(label + " is required.");
        }
        try {
            int value = Integer.parseInt(cleaned);
            if (value <= 0) {
                throw new ValidationException(label + " must be greater than zero.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a whole number.");
        }
    }

    // requires a chosen date
    public static LocalDate date(LocalDate date, String label) throws ValidationException {
        if (date == null) {
            throw new ValidationException("Please choose a " + label + ".");
        }
        return date;
    }

    // requires a selection from a drop-down
    public static <T> T choice(T value, String label) throws ValidationException {
        if (value == null) {
            throw new ValidationException("Please choose a " + label + ".");
        }
        return value;
    }
}
