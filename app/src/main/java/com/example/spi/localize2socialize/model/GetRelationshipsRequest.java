package com.example.spi.localize2socialize.model;

public class GetRelationshipsRequest {
    private String personId;

    public GetRelationshipsRequest() {
    }

    public GetRelationshipsRequest(String personId) {
        this.personId = personId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
}
