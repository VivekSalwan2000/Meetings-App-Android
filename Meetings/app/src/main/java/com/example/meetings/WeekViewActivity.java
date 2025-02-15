package com.example.meetings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    private static final String TAG = "WeekViewActivity";

    private DatabaseHelper databaseHelper; // SQLite database helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);

        Log.d(TAG, "Transitioning to WeekViewActivity.");

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow); // Optional custom back arrow
            getSupportActionBar().setTitle(""); // Set custom title
        }

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Ensure selectedDate is initialized
        if (CalendarUtils.selectedDate == null) {
            CalendarUtils.selectedDate = Calendar.getInstance();
        }

        initWidgets(); // Initialize UI elements
        setWeekView(); // Set up the week view
    }

    private void initWidgets() {
        // Initialize UI widgets
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
    }

    private void setWeekView() {
        // Display the current month and year
        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));

        // Get the days in the current week
        ArrayList<Calendar> days = daysInWeekArray(CalendarUtils.selectedDate);

        // Set up the RecyclerView with a GridLayoutManager (7 columns for 7 days)
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        setEventAdapter(); // Display the events for the current day
    }

    /**
     * Creates a list of days for the current week based on the selected date.
     *
     * @param selectedDate the date from which the week starts
     * @return a list of Calendar objects representing the days in the week
     */
    private ArrayList<Calendar> daysInWeekArray(Calendar selectedDate) {
        ArrayList<Calendar> days = new ArrayList<>();
        Calendar current = getSundayForDate(selectedDate); // Get the Sunday of the current week
        Calendar endDate = (Calendar) current.clone();
        endDate.add(Calendar.DAY_OF_MONTH, 7); // Calculate the end of the week

        // Add each day of the week to the list
        while (current.before(endDate)) {
            days.add((Calendar) current.clone());
            current.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

    /**
     * Finds the Sunday of the week for a given date.
     *
     * @param current the date for which the Sunday is to be determined
     * @return a Calendar object set to the Sunday of the current week
     */
    private Calendar getSundayForDate(Calendar current) {
        Calendar temp = (Calendar) current.clone();
        while (temp.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            temp.add(Calendar.DAY_OF_MONTH, -1);
        }
        return temp;
    }

    private void setEventAdapter() {
        // Fetch events for the selected date from the database
        ArrayList<Event> dailyEvents = databaseHelper.getEventsForDate(CalendarUtils.selectedDate);

        // Set up the adapter for the ListView
        EventAdapter eventAdapter = new EventAdapter(this, dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    /**
     * Handles the action to navigate to the previous week.
     *
     * @param view the view that triggered the action
     */
    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate.add(Calendar.WEEK_OF_YEAR, -1);
        setWeekView();
    }

    /**
     * Handles the action to navigate to the next week.
     *
     * @param view the view that triggered the action
     */
    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate.add(Calendar.WEEK_OF_YEAR, 1);
        setWeekView();
    }

    /**
     * Handles the click on a specific day in the calendar.
     *
     * @param position the position of the clicked item
     * @param date     the date of the clicked item
     */
    @Override
    public void onItemClick(int position, Calendar date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter(); // Refresh events when returning to this activity
    }

    /**
     * Handles the action to navigate to the EventEditActivity.
     *
     * @param view the view that triggered the action
     */
    public void newEventAction(View view) {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the back button click
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Deletes all meetings for the selected day.
     *
     * @param view the view that triggered the action
     */
    public void deleteDailyMeetings(View view) {
        ArrayList<Event> eventsForDay = databaseHelper.getEventsForDate(CalendarUtils.selectedDate);

        for (Event event : eventsForDay) {
            databaseHelper.deleteEvent(event);
        }

        setEventAdapter(); // Refresh the event list

        Toast.makeText(this, "All meetings for the selected day have been deleted!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Pushes all meetings from the selected day to the next available day.
     *
     * @param view the view that triggered the action
     */
    public void pushAction(View view) {
        Calendar currentDate = (Calendar) CalendarUtils.selectedDate.clone();
        Calendar nextDate = getNextPushDate(currentDate);

        ArrayList<Event> currentDayEvents = databaseHelper.getEventsForDate(currentDate);

        // Add the events to the next day
        for (Event event : currentDayEvents) {
            Event newEvent = new Event(event.getName(), nextDate, event.getContactName(), event.getContactPhone());
            databaseHelper.addEvent(newEvent);
        }

        // Remove the events from the current day
        for (Event event : currentDayEvents) {
            databaseHelper.deleteEvent(event);
        }

        Toast.makeText(this, "Meetings pushed to " + CalendarUtils.formattedDate(nextDate), Toast.LENGTH_SHORT).show();
        setEventAdapter();
    }

    /**
     * Determines the next available date for pushing meetings.
     *
     * @param currentDate the current date to calculate the next push date from
     * @return the next available date for meetings
     */
    private Calendar getNextPushDate(Calendar currentDate) {
        Calendar nextDate = (Calendar) currentDate.clone();
        int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.THURSDAY) {
            nextDate.add(Calendar.DAY_OF_MONTH, 1); // Next day
        } else if (dayOfWeek == Calendar.FRIDAY) {
            nextDate.add(Calendar.DAY_OF_MONTH, 3); // Next Monday
        } else if (dayOfWeek == Calendar.SATURDAY) {
            nextDate.add(Calendar.DAY_OF_MONTH, 1); // Next Sunday
        } else if (dayOfWeek == Calendar.SUNDAY) {
            nextDate.add(Calendar.DAY_OF_MONTH, 6); // Next Saturday
        }

        return nextDate;
    }
}
