package com.example.spi.localize2socialize.model;

import java.util.Date;

public class Post {
    private Long id;
    private Account owner;
    private String encodedAttachedImage;
    private String description;
    private Date startDate;
    private Date endDate;
    private Double locationLatitude;
    private Double locationLongitude;
    private String locationName;

    public Post() {
    }

    public Post(Account owner, String encodedAttachedImage, String description,
                Date startDate, Date endDate, Double locationLatitude,
                Double locationLongitude, String locationName) {
        this.owner = owner;
        this.encodedAttachedImage = encodedAttachedImage;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.locationName = locationName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public String getEncodedAttachedImage() {
        return encodedAttachedImage;
    }

    public void setEncodedAttachedImage(String encodedAttachedImage) {
        this.encodedAttachedImage = encodedAttachedImage;
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

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(Double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public Double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(Double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
