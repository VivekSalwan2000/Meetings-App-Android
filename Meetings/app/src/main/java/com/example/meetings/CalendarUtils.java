package com.example.meetings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarUtils {
    public static Calendar selectedDate;

    // Formatter for full date (used for display and database storage)
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
    private static final SimpleDateFormat databaseFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * Format a Calendar date to a readable string for display.
     */
    public static String formattedDate(Calendar date) {
        return dateFormatter.format(date.getTime());
    }

    /**
     * Format a Calendar time to a readable string for display.
     */
    public static String formattedTime(Calendar time) {
        return timeFormatter.format(time.getTime());
    }

    /**
     * Format a Calendar to show Month and Year (e.g., "December 2024").
     */
    public static String monthYearFromDate(Calendar date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return formatter.format(date.getTime());
    }

    /**
     * Parse a date string from the database into a Calendar object.
     */
    public static Calendar parseFromDatabase(String dateString) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(databaseFormatter.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * Format a Calendar date to a string for database storage.
     */
    public static String formatToDatabase(Calendar date) {
        return databaseFormatter.format(date.getTime());
    }


    /**
     * Check if two Calendar objects represent the same day.
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
