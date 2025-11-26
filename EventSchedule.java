package com.example.berenang10;

import java.io.Serializable;

/**
 * Data class to represent an event schedule with date and location.
 * Implements Serializable so it can be passed between Activities via Intent.
 */
public class EventSchedule implements Serializable {
    private String date;
    private String location;

    public EventSchedule(String date, String location) {
        this.date = date;
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return date + " at " + location;
    }
}