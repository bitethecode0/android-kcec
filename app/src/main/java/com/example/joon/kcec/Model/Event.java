package com.example.joon.kcec.Model;

import java.util.Date;

public class Event {
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
}
