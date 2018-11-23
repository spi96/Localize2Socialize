package com.example.spi.localize2socialize.model;

public class SearchRequest {
    private String filter;
    private Account callerPerson;

    public SearchRequest() {
    }

    public SearchRequest(String filter, Account callerPerson) {
        this.filter = filter;
        this.callerPerson = callerPerson;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Account getCallerPerson() {
        return callerPerson;
    }

    public void setCallerPerson(Account callerPerson) {
        this.callerPerson = callerPerson;
    }
}


