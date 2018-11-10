package com.example.spi.localize2socialize.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.spi.localize2socialize.LoginActivity;
import com.example.spi.localize2socialize.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivityViewModel extends ViewModel {
    private User _user = null;

    public void setUser(GoogleSignInAccount account) {
        if (_user == null) {
            _user = new User(account.getDisplayName(),
                    account.getGivenName(),
                    account.getFamilyName(),
                    account.getEmail(),
                    account.getId());
        }
    }

    public void signOut(final Activity activity, GoogleSignInClient client) {
        client.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
            }
        });
    }
}
