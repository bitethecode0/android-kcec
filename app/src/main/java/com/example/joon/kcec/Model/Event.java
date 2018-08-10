package com.example.joon.kcec.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Event implements Parcelable {
    private String event_name;
    private String event_location;
    private String event_description;
    private String event_category;
    private Date date;

    public Event() {
    }

    public Event(String event_name, String event_location, String event_description, String event_category, Date date) {
        this.event_name = event_name;
        this.event_location = event_location;
        this.event_description = event_description;
        this.event_category = event_category;
        this.date = date;
    }

    protected Event(Parcel in) {
        event_name = in.readString();
        event_location = in.readString();
        event_description = in.readString();
        event_category = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public String getEvent_category() {
        return event_category;
    }

    public void setEvent_category(String event_category) {
        this.event_category = event_category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(event_name);
        dest.writeString(event_location);
        dest.writeString(event_description);
        dest.writeString(event_category);
    }
}
