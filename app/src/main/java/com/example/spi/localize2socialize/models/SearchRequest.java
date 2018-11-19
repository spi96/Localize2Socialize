package com.example.spi.localize2socialize.models;

public class SearchRequest {
    String filter;
    String callerPersonId;

    public SearchRequest() {
    }

    public SearchRequest(String filter, String callerPersonId) {
        this.filter = filter;
        this.callerPersonId = callerPersonId;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCallerPersonId() {
        return callerPersonId;
    }

    public void setCallerPersonId(String callerPersonId) {
        this.callerPersonId = callerPersonId;
    }
}
