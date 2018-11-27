package com.example.spi.localize2socialize.model;

import java.util.List;

public class DeleteRelationshipsRequest {
    private List<Account> friendsToDelete;
    private Account caller;

    public DeleteRelationshipsRequest() {
    }

    public DeleteRelationshipsRequest(List<Account> friendsToDelete, Account caller) {
        this.friendsToDelete = friendsToDelete;
        this.caller = caller;
    }

    public List<Account> getFriendsToDelete() {
        return friendsToDelete;
    }

    public void setFriendsToDelete(List<Account> friendsToDelete) {
        this.friendsToDelete = friendsToDelete;
    }

    public Account getCaller() {
        return caller;
    }

    public void setCaller(Account caller) {
        this.caller = caller;
    }
}
