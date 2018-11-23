package com.example.spi.localize2socialize.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Calendar {

    private Long id;
    private Long calId;
    private String displayName;
    private Account owner;
    private List<Account> participants;
    private List<Event> events;
    private Date startOfSharing;
    private Date endOfSharing;

    public Calendar() {
        events = new ArrayList<>();
    }

    public Calendar(Long calId, String displayName) {
        this.calId = calId;
        this.displayName = displayName;
        events = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public Long getCalId() {
        return calId;
    }

    public void setCalId(Long calId) {
        this.calId = calId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Date getStartOfSharing() {
        return startOfSharing;
    }

    public void setStartOfSharing(Date startOfSharing) {
        this.startOfSharing = startOfSharing;
    }

    public Date getEndOfSharing() {
        return endOfSharing;
    }

    public void setEndOfSharing(Date endOfSharing) {
        this.endOfSharing = endOfSharing;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public List<Account> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Account> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
