package com.example.meetings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private static final String TAG = "MainActivity";
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        if (CalendarUtils.selectedDate == null) {
            CalendarUtils.selectedDate = Calendar.getInstance();
            Log.d(TAG, "Initializing selectedDate to current date: " + CalendarUtils.selectedDate.getTime());
        }

        initWidgets();
        initToolbarActions();
        loadEventsFromDatabase();
        setMonthView();
    }

    private void initWidgets() {
        // Initialize UI widgets
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void initToolbarActions() {
        // New Event button
        ImageButton newMeetingButton = findViewById(R.id.newMeetingButton);
        newMeetingButton.setOnClickListener(v -> newMeetingAction());

        // Weekly View button
        ImageButton weeklyButton = findViewById(R.id.weeklyButton);
        weeklyButton.setOnClickListener(v -> weeklyAction());

        // Delete All Meetings button
        ImageButton deleteAllMeetingsButton = findViewById(R.id.deleteAllMeetingsButton);
        deleteAllMeetingsButton.setOnClickListener(v -> deleteAllMeetings());
    }

    private void setMonthView() {
        // Set the month and year text
        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));

        // Populate the days for the current month
        ArrayList<Calendar> daysInMonth = daysInMonthArray();

        // Set up RecyclerView with a grid layout
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    /**
     * Creates an array of days to display in the calendar for the current month, including
     * padding for days from the previous and next months.
     *
     * @return a list of Calendar objects representing the days to display
     */
    private ArrayList<Calendar> daysInMonthArray() {
        ArrayList<Calendar> daysInMonthArray = new ArrayList<>();
        Calendar firstOfMonth = (Calendar) CalendarUtils.selectedDate.clone();
        firstOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = firstOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = firstOfMonth.get(Calendar.DAY_OF_WEEK) - 1;

        Calendar prevMonth = (Calendar) firstOfMonth.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int prevDaysInMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= 42; i++) {
            Calendar date = (Calendar) firstOfMonth.clone();
            if (i <= dayOfWeek) {
                date.set(Calendar.DAY_OF_MONTH, prevDaysInMonth - dayOfWeek + i);
                date.add(Calendar.MONTH, -1); // Add days from the previous month
            } else if (i > daysInMonth + dayOfWeek) {
                date.set(Calendar.DAY_OF_MONTH, i - dayOfWeek - daysInMonth);
                date.add(Calendar.MONTH, 1); // Add days from the next month
            } else {
                date.set(Calendar.DAY_OF_MONTH, i - dayOfWeek); // Add days from the current month
            }
            daysInMonthArray.add(date);
        }
        return daysInMonthArray;
    }

    /**
     * Navigates to the previous month and refreshes the month view.
     *
     * @param view the view that triggered the action
     */
    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate.add(Calendar.MONTH, -1);
        setMonthView();
    }

    /**
     * Navigates to the next month and refreshes the month view.
     *
     * @param view the view that triggered the action
     */
    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate.add(Calendar.MONTH, 1);
        setMonthView();
    }

    /**
     * Handles the click on a specific day in the calendar.
     *
     * @param position the position of the clicked item
     * @param date     the date of the clicked item
     */
    @Override
    public void onItemClick(int position, Calendar date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    /**
     * Navigates to the weekly view activity.
     */
    public void weeklyAction() {
        Intent intent = new Intent(this, WeekViewActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates to the EventEditActivity to create a new event.
     */
    public void newMeetingAction() {
        Intent intent = new Intent(this, EventEditActivity.class);
        startActivity(intent);
    }

    /**
     * Deletes all meetings from the database and refreshes the calendar view.
     */
    public void deleteAllMeetings() {
        // Clear all meetings from the database
        databaseHelper.deleteAllEvents();
        Event.setEventsList(new ArrayList<>()); // Clear in-memory events list

        // Show a toast for confirmation
        Toast.makeText(this, "All meetings have been deleted!", Toast.LENGTH_SHORT).show();

        // Refresh the calendar view
        setMonthView();
    }

    /**
     * Loads all events from the database into memory.
     */
    private void loadEventsFromDatabase() {
        Event.setEventsList(databaseHelper.getAllEvents());
        Log.d(TAG, "Loaded " + Event.getEventsList().size() + " events from the database.");
    }
}
