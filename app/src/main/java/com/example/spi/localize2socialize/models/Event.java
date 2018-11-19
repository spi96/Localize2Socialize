package com.example.spi.localize2socialize.models;

import java.util.Date;

public class Event {
    private long calID;
    private long eventID;
    private String title;
    private String description;
    private String eventLocation;
    private Date dtStart;
    private Date dtEnd;
    private String eventTimeZone;
    private String eventEndTimeZone;
    private boolean allDay;
    private String rRule;
    private String rDate;

    public Event() {
    }

    public Event(long calID, long eventID, String title, String description, String eventLocation,
                 Date dtStart, Date dtEnd, String eventTimeZone, String eventEndTimeZone,
                 boolean allDay, String rRule, String rDate) {
        this.calID = calID;
        this.eventID = eventID;
        this.title = title;
        this.description = description;
        this.eventLocation = eventLocation;
        this.dtStart = dtStart;
        this.dtEnd = dtEnd;
        this.eventTimeZone = eventTimeZone;
        this.eventEndTimeZone = eventEndTimeZone;
        this.allDay = allDay;
        this.rRule = rRule;
        this.rDate = rDate;
    }

    public long getCalID() {
        return calID;
    }

    public void setCalID(long calID) {
        this.calID = calID;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Date getDtStart() {
        return dtStart;
    }

    public void setDtStart(Date dtStart) {
        this.dtStart = dtStart;
    }

    public Date getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(Date dtEnd) {
        this.dtEnd = dtEnd;
    }

    public String getEventTimeZone() {
        return eventTimeZone;
    }

    public void setEventTimeZone(String eventTimeZone) {
        this.eventTimeZone = eventTimeZone;
    }

    public String getEventEndTimeZone() {
        return eventEndTimeZone;
    }

    public void setEventEndTimeZone(String eventEndTimeZone) {
        this.eventEndTimeZone = eventEndTimeZone;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getrRule() {
        return rRule;
    }

    public void setrRule(String rRule) {
        this.rRule = rRule;
    }

    public String getrDate() {
        return rDate;
    }

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }
}
