package com.example.spi.localize2socialize.models;

import java.util.ArrayList;
import java.util.List;

public class Calendar {

    private long Id;
    private String accountName;
    private String displayName;
    private String owner;
    private List<Event> events;

    public Calendar() {
        events = new ArrayList<>();
    }

    public Calendar(long id, String accountName, String displayName, String owner) {
        Id = id;
        this.accountName = accountName;
        this.displayName = displayName;
        this.owner = owner;
        events = new ArrayList<>();
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
