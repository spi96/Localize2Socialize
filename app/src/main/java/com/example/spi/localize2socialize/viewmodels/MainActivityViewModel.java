package com.example.spi.localize2socialize.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spi.localize2socialize.LoginActivity;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.models.Account;
import com.example.spi.localize2socialize.net.RequestQueueSingleton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class MainActivityViewModel extends AndroidViewModel {
    private Account _account = null;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void setUser(GoogleSignInAccount account, Bitmap bitmap) {
        _account = new Account(account.getDisplayName(),
                account.getGivenName(),
                account.getFamilyName(),
                account.getEmail(),
                account.getId(),
                account.getPhotoUrl()
        );

        if (bitmap != null)
            _account.setEncodedPhoto(encodePhoto(bitmap));
        else
            _account.setEncodedPhoto("");

        saveOrUpdateAccount();
    }

    public Account getUser() {
        return _account;
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
        String url = getApplication().getResources().
                getString(R.string.baseUrl) + getApplication().getResources().getString(R.string.saveOrUpdateAcount);
        JSONObject jsonObject = null;
        try {
            jsonObject = createRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendRequest(url, Request.Method.POST, jsonObject);
    }

    public void sendRequest(String url, int method, JSONObject jsonRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonRequest,
                getResponseListener(), getResponseErrorListener());
        RequestQueueSingleton.getInstance(getApplication()).addToRequestQueue(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("accountId")) ;
                    _account.setId(response.getLong("accountId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("HTTP RESPONSE", response.toString());
            }
        };
    }

    private Response.ErrorListener getResponseErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API CALL ERROR", error.toString());
            }
        };
    }

    private JSONObject createRequest() throws JSONException {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return new JSONObject(gson.toJson(_account));
    }

    private String encodePhoto(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap decodePhoto(String encoded) {
        byte[] decoded = Base64.decode(encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
    }
}
