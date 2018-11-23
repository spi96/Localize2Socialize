package com.example.spi.localize2socialize.model;

public class Friend {

    private Account account;
    private boolean isPending;

    public Friend() {
    }

    public Friend(Account account, boolean isPending) {
        this.account = account;
        this.isPending = isPending;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }
}
