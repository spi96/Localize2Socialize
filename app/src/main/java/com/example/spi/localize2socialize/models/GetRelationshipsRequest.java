package com.example.spi.localize2socialize.models;

public class GetRelationshipsRequest extends Request {
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
