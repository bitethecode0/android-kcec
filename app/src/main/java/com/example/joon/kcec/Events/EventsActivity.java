package com.example.joon.kcec.Events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.joon.kcec.R;

public class EventsActivity extends AppCompatActivity {
    private static final String TAG = "EventsFragment";

    private CalendarView mCalendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = (month+1)+"/"+dayOfMonth+"/"+year;
                Log.d(TAG, "onSelectedDayChange: date selected : "+date);
            }
        });
    }

}
