package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// formatting helpers for money and dates
public final class Fmt {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private Fmt() {
    }

    public static String money(double amount) {
        return String.format(Locale.ENGLISH, "PHP %,.2f", amount);
    }

    // return e.g. Nov 2, 2026, or an empty string for null
    public static String date(LocalDate date) {
        return date == null ? "" : DATE.format(date);
    }

    // return e.g. Nov 2, 2026 5:56 PM
    public static String dateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : DATE_TIME.format(dateTime);
    }

    // rounds to centavos so repeated additions do not drift
    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // describes how far away an expiry date is, e.g. "3 days", "Today" or "Expired 2 days ago".
    public static String daysPhrase(long days) {
        if (days < 0) {
            long ago = -days;
            return "Expired " + ago + (ago == 1 ? " day ago" : " days ago");
        }
        if (days == 0) {
            return "Today";
        }
        return days + (days == 1 ? " day" : " days");
    }
}
