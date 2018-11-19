package com.example.spi.localize2socialize.models;

public class FriendRequest extends Request {
    private String senderPersonId;
    private String receiverPersonId;

    public FriendRequest() {
    }

    public FriendRequest(String senderPersonId, String receiverPersonId) {
        this.senderPersonId = senderPersonId;
        this.receiverPersonId = receiverPersonId;
    }

    public String getSenderPersonId() {
        return senderPersonId;
    }

    public void setSenderPersonId(String senderPersonId) {
        this.senderPersonId = senderPersonId;
    }

    public String getReceiverPersonId() {
        return receiverPersonId;
    }

    public void setReceiverPersonId(String receiverPersonId) {
        this.receiverPersonId = receiverPersonId;
    }
}
