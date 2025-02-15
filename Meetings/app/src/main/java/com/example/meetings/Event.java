package com.example.meetings;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;

public class Event {

    private int id;               // Unique ID for each event
    private String name;          // Event name
    private Calendar date;        // Event date
    private String contactName;   // Contact name associated with the event
    private String contactPhone;  // Contact phone number associated with the event

    private static ArrayList<Event> eventsList = new ArrayList<>(); // In-memory event list

    /**
     * Constructor with all fields.
     *
     * @param id           the unique ID of the event
     * @param name         the name of the event
     * @param date         the date of the event
     * @param contactName  the name of the contact associated with the event
     * @param contactPhone the phone number of the contact associated with the event
     */
    public Event(int id, String name, Calendar date, String contactName, String contactPhone) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
    }

    /**
     * Constructor without ID (used for creating new events before saving to the database).
     *
     * @param name         the name of the event
     * @param date         the date of the event
     * @param contactName  the name of the contact associated with the event
     * @param contactPhone the phone number of the contact associated with the event
     */
    public Event(String name, Calendar date, String contactName, String contactPhone) {
        this(-1, name, date, contactName, contactPhone);
    }

    /**
     * Constructor without contact information.
     *
     * @param name the name of the event
     * @param date the date of the event
     */
    public Event(String name, Calendar date) {
        this(name, date, "", "");
    }

    // Getters

    /**
     * Gets the unique ID of the event.
     *
     * @return the event ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the event.
     *
     * @return the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the date of the event.
     *
     * @return the event date as a Calendar object
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * Gets the name of the contact associated with the event.
     *
     * @return the contact name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Gets the phone number of the contact associated with the event.
     *
     * @return the contact phone number
     */
    public String getContactPhone() {
        return contactPhone;
    }

    // Setters

    /**
     * Sets the unique ID of the event.
     *
     * @param id the event ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Converts the event into ContentValues for database operations.
     *
     * @return a ContentValues object containing the event data
     */
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", CalendarUtils.formatToDatabase(date)); // Format date for storage
        values.put("contact_name", contactName);
        values.put("contact_phone", contactPhone);
        return values;
    }

    /**
     * Creates an Event object from a database Cursor.
     *
     * @param cursor the database cursor containing event data
     * @return an Event object created from the cursor data
     */
    public static Event fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String dateString = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        String contactName = cursor.getString(cursor.getColumnIndexOrThrow("contact_name"));
        String contactPhone = cursor.getString(cursor.getColumnIndexOrThrow("contact_phone"));

        Calendar date = CalendarUtils.parseFromDatabase(dateString); // Parse date from database format
        return new Event(id, name, date, contactName, contactPhone);
    }

    /**
     * Gets the static in-memory list of events.
     *
     * @return the list of events
     */
    public static ArrayList<Event> getEventsList() {
        return eventsList;
    }

    /**
     * Sets the static in-memory list of events.
     *
     * @param events the list of events to set
     */
    public static void setEventsList(ArrayList<Event> events) {
        eventsList = events;
    }

    /**
     * Prints event details for debugging purposes.
     *
     * @return a string representation of the event
     */
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + CalendarUtils.formattedDate(date) +
                ", contactName='" + contactName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                '}';
    }
}
