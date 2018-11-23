package com.example.spi.localize2socialize.model;

import java.util.Date;

public class Event {
    private Long id;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private double latitude;
    private double longitude;
    private String locationName;


    public Event() {
    }

    public Event(String title, String description, String eventLocation,
                 double latitude, double longitude, Date dtStart, Date dtEnd) {
        this.title = title;
        this.description = description;
        this.startDate = dtStart;
        this.endDate = dtEnd;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = eventLocation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
