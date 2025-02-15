package com.example.meetings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Event> events;

    /**
     * Constructor for the EventAdapter.
     *
     * @param context the context in which the adapter is used
     * @param events  the list of events to display
     */
    public EventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    /**
     * Gets the total count of events.
     *
     * @return the number of events in the list
     */
    @Override
    public int getCount() {
        return events.size();
    }

    /**
     * Gets the event at the specified position.
     *
     * @param position the position of the event in the list
     * @return the event object at the specified position
     */
    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    /**
     * Gets the item ID for the specified position.
     * Here, the position itself is returned as the ID since events don't have unique IDs.
     *
     * @param position the position of the item
     * @return the ID of the item (same as position)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Creates or updates a view for an event at the specified position.
     *
     * @param position    the position of the event in the list
     * @param convertView the recycled view to reuse, if possible
     * @param parent      the parent view group
     * @return the updated view displaying the event details
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Inflate the layout for the event item if no recycled view is available
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_event, parent, false);
        }

        // Get the current event
        Event event = events.get(position);

        // Initialize views from the layout
        TextView eventTitle = convertView.findViewById(R.id.eventTitle); // Event title TextView
        TextView eventTime = convertView.findViewById(R.id.eventTime); // Event time TextView
        TextView eventContact = convertView.findViewById(R.id.eventContact); // Event contact details TextView

        // Set data to the views
        eventTitle.setText(event.getName()); // Set the event title
        eventTime.setText("Time: " + CalendarUtils.formattedTime(event.getDate())); // Set the formatted event time
        eventContact.setText("Contact: " + event.getContactName() + " (" + event.getContactPhone() + ")"); // Set the contact details

        return convertView;
    }
}
