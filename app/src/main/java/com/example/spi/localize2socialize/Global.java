package com.example.spi.localize2socialize;

import android.app.Application;

import com.example.spi.localize2socialize.models.Account;

public class Global extends Application {
    private Account user;

    public void setUser(Account user) {
        this.user = user;
    }

    public Account getUser() {
        return user;
    }
}
