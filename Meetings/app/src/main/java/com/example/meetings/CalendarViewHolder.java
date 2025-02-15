package com.example.meetings;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * ViewHolder for a single calendar cell in the RecyclerView.
 */
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final View parentView;          // The parent view of the calendar cell
    public final TextView dayOfMonth;      // TextView to display the day of the month
    private final ArrayList<Calendar> days; // List of Calendar objects representing the days
    private final CalendarAdapter.OnItemListener onItemListener; // Listener for item click events

    /**
     * Constructor for the CalendarViewHolder.
     *
     * @param itemView        the view representing the calendar cell
     * @param onItemListener  the listener for handling click events on calendar items
     * @param days            the list of Calendar objects for the current calendar view
     */
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<Calendar> days) {
        super(itemView);

        // Initialize views and listener
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        this.days = days;

        // Set click listener for the calendar cell
        itemView.setOnClickListener(this);
    }

    /**
     * Handles the click event for a calendar cell.
     *
     * @param view the view that was clicked
     */
    @Override
    public void onClick(View view) {
        // Trigger the onItemClick callback with the position and corresponding Calendar object
        onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
    }
}
