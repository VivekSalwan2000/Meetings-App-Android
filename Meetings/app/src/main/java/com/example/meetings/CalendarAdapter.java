package com.example.meetings;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<Calendar> days;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<Calendar> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (days.size() > 15) // Month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else // Week view
            layoutParams.height = parent.getHeight();

        return new CalendarViewHolder(view, onItemListener, days);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final Calendar date = days.get(position);

        if (date == null) {
            holder.dayOfMonth.setText("");
            holder.dayOfMonth.setTextColor(Color.LTGRAY);
        } else {
            holder.dayOfMonth.setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));
            if (CalendarUtils.isSameDay(date, CalendarUtils.selectedDate))
                holder.parentView.setBackgroundColor(Color.LTGRAY);

            if (date.get(Calendar.MONTH) == CalendarUtils.selectedDate.get(Calendar.MONTH))
                holder.dayOfMonth.setTextColor(Color.BLACK);
            else
                holder.dayOfMonth.setTextColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, Calendar date);
    }
}