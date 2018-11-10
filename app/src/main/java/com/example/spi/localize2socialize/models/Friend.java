package com.example.spi.localize2socialize.models;

public class Friend {

    private User user;
    private boolean isPending;

    public Friend() {
    }

    public Friend(User user, boolean isPending) {
        this.user = user;
        this.isPending = isPending;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }
}
