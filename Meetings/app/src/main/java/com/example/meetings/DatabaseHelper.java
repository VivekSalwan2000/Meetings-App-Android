package com.example.meetings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Meetings.db"; // Database name
    private static final int DATABASE_VERSION = 1; // Database version

    // Table and Column Names
    private static final String TABLE_EVENTS = "events"; // Table name
    private static final String COLUMN_ID = "id"; // Event ID column
    private static final String COLUMN_NAME = "name"; // Event name column
    private static final String COLUMN_DATE = "date"; // Event date column
    private static final String COLUMN_CONTACT_NAME = "contact_name"; // Contact name column
    private static final String COLUMN_CONTACT_PHONE = "contact_phone"; // Contact phone column

    /**
     * Constructor for the DatabaseHelper.
     *
     * @param context the context in which the database is used
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param db the SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_CONTACT_NAME + " TEXT, " +
                COLUMN_CONTACT_PHONE + " TEXT)";
        db.execSQL(createTable); // Execute the table creation query
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         the SQLite database
     * @param oldVersion the old version of the database
     * @param newVersion the new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS); // Drop existing table
        onCreate(db); // Recreate the table
    }

    /**
     * Adds a new event to the database.
     *
     * @param event the event to add
     */
    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DATE, CalendarUtils.formatToDatabase(event.getDate())); // Format date for storage
        values.put(COLUMN_CONTACT_NAME, event.getContactName());
        values.put(COLUMN_CONTACT_PHONE, event.getContactPhone());
        db.insert(TABLE_EVENTS, null, values);
        db.close(); // Close the database connection
    }

    /**
     * Retrieves all events for a specific date.
     *
     * @param date the date for which to retrieve events
     * @return a list of events occurring on the specified date
     */
    public ArrayList<Event> getEventsForDate(Calendar date) {
        ArrayList<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String formattedDate = CalendarUtils.formatToDatabase(date); // Format date for query

        Cursor cursor = db.query(TABLE_EVENTS, null, COLUMN_DATE + " = ?", new String[]{formattedDate}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String contactName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME));
                    String contactPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE));

                    Calendar eventDate = CalendarUtils.parseFromDatabase(formattedDate); // Parse date from string
                    events.add(new Event(name, eventDate, contactName, contactPhone)); // Create and add event
                } while (cursor.moveToNext());
            }
            cursor.close(); // Close the cursor
        }

        db.close(); // Close the database connection
        return events;
    }

    /**
     * Deletes all events from the database.
     */
    public void deleteAllEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, null, null); // Delete all rows in the table
        db.close(); // Close the database connection
    }

    /**
     * Deletes a specific event from the database.
     *
     * @param event the event to delete
     * @return true if the event was successfully deleted, false otherwise
     */
    public boolean deleteEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        String formattedDate = CalendarUtils.formatToDatabase(event.getDate()); // Format date for query
        int rowsDeleted = db.delete(TABLE_EVENTS,
                COLUMN_NAME + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{event.getName(), formattedDate}); // Delete matching event
        db.close(); // Close the database connection

        return rowsDeleted > 0; // Return true if rows were deleted
    }

    /**
     * Retrieves all events from the database.
     *
     * @return a list of all events stored in the database
     */
    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Event event = Event.fromCursor(cursor); // Create event from cursor row
                events.add(event); // Add event to the list
            } while (cursor.moveToNext());

            cursor.close(); // Close the cursor
        }

        db.close(); // Close the database connection
        return events;
    }

    /**
     * Inserts an event into the database.
     *
     * @param event the event to insert
     * @return true if the event was successfully inserted, false otherwise
     */
    public boolean insertEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = event.toContentValues(); // Convert event to ContentValues

        long result = db.insert(TABLE_EVENTS, null, values); // Insert event
        db.close(); // Close the database connection

        return result != -1; // If result is -1, the insert operation failed
    }
}
