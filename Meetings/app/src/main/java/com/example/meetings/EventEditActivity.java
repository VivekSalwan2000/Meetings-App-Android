package com.example.meetings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EventEditActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_CONTACT = 1;
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 2;

    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV, contactNameTV;

    private Calendar time;
    private String selectedContactName = "";
    private String selectedContactPhone = "";
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        databaseHelper = new DatabaseHelper(this); // Initialize the database helper
        initWidgets(); // Initialize UI widgets

        time = (Calendar) CalendarUtils.selectedDate.clone(); // Clone the selected date
        updateDateAndTimeDisplay(); // Update the displayed date and time
    }

    private void initWidgets() {
        // Initialize UI elements
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeTV);
        contactNameTV = findViewById(R.id.contactNameTV);

        // Set click listeners for date, time, and contact selection
        eventDateTV.setOnClickListener(v -> showSpinnerDatePickerDialog());
        eventTimeTV.setOnClickListener(v -> showSpinnerTimePickerDialog());
        contactNameTV.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the READ_CONTACTS permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
            } else {
                // Open contact picker
                openContactsPicker();
            }
        });
    }

    private void updateDateAndTimeDisplay() {
        // Format and display the selected date and time
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());

        eventDateTV.setText("Date: " + dateFormatter.format(time.getTime()));
        eventTimeTV.setText("Time: " + timeFormatter.format(time.getTime()));
    }

    private void showSpinnerDatePickerDialog() {
        // Display a date picker dialog in spinner style
        int year = time.get(Calendar.YEAR);
        int month = time.get(Calendar.MONTH);
        int day = time.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // Spinner style
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    time.set(Calendar.YEAR, selectedYear);
                    time.set(Calendar.MONTH, selectedMonth);
                    time.set(Calendar.DAY_OF_MONTH, selectedDay);
                    updateDateAndTimeDisplay();
                }, year, month, day);

        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setSpinnersShown(true);
        datePickerDialog.show();
    }

    private void showSpinnerTimePickerDialog() {
        // Display a time picker dialog in spinner style
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // Spinner style
                (view, selectedHour, selectedMinute) -> {
                    time.set(Calendar.HOUR_OF_DAY, selectedHour);
                    time.set(Calendar.MINUTE, selectedMinute);
                    updateDateAndTimeDisplay();
                }, hour, minute, false); // `false` for 12-hour format

        timePickerDialog.show();
    }

    private void openContactsPicker() {
        // Open the contacts picker to select a contact
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            if (data != null) {
                Uri contactUri = data.getData();
                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    selectedContactName = cursor.getString(nameIndex);

                    @SuppressLint("Range")
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId}, null);

                    if (phones != null && phones.moveToFirst()) {
                        int phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        selectedContactPhone = phones.getString(phoneIndex);
                        phones.close();
                    }

                    cursor.close();

                    contactNameTV.setText("Contact: " + selectedContactName + " (" + selectedContactPhone + ")");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_CONTACTS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openContactsPicker();
            } else {
                Toast.makeText(this, "Permission denied to access contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Saves the event to the database.
     *
     * @param view the view that triggered the save action
     */
    public void saveEventAction(View view) {
        String eventName = eventNameET.getText().toString();

        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter an event name!", Toast.LENGTH_SHORT).show();
            return;
        }

        Event newEvent = new Event(eventName, time, selectedContactName, selectedContactPhone);

        // Save event to the database
        boolean isSaved = databaseHelper.insertEvent(newEvent);

        if (isSaved) {
            Toast.makeText(this, "Event Saved with Contact: " + selectedContactName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving event!", Toast.LENGTH_SHORT).show();
        }

        finish(); // Close the activity
    }
}
