package com.example.spi.localize2socialize.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.spi.localize2socialize.LoginActivity;
import com.example.spi.localize2socialize.models.Account;
import com.example.spi.localize2socialize.net.ApiController;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {
    private Account _account = null;
    public static ApiController apiController = ApiController.getInstance();

    public void setUser(GoogleSignInAccount account) {
        _account = new Account(account.getDisplayName(),
                account.getGivenName(),
                account.getFamilyName(),
                account.getEmail(),
                account.getId(),
                account.getPhotoUrl()
        );

        saveOrUpdateAccount();
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

    public void saveOrUpdateAccount() {
        apiController.saveOrUpdateAccount(_account, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t != null) {

                }
            }
        });
    }
}
