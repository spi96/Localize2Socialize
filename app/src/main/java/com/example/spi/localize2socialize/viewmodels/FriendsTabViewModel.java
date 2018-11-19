package com.example.spi.localize2socialize.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spi.localize2socialize.Global;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.models.Account;
import com.example.spi.localize2socialize.models.Friend;
import com.example.spi.localize2socialize.models.GetRelationshipsRequest;
import com.example.spi.localize2socialize.net.RequestQueueSingleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FriendsTabViewModel extends AndroidViewModel {
    private Account account;
    private MutableLiveData<List<Account>> friends = new MutableLiveData<>();
    private MutableLiveData<List<Account>> friendRequests = new MutableLiveData<>();

    public FriendsTabViewModel(@NonNull Application application) {
        super(application);
        account = ((Global) application).getUser();
        friends.setValue(new ArrayList<Account>());
        friendRequests.setValue(new ArrayList<Account>());
        loadFriends();
    }

    public List<Account> getFriendsList() {
        if (friends == null) {
            friends = new MutableLiveData<>();
            loadFriends();
        }
        return friends.getValue();
    }

    public List<Account> getFriendRequests() {
        if (friendRequests == null) {
            friendRequests = new MutableLiveData<>();
            loadFriends();
        }
        return friendRequests.getValue();
    }

    public MutableLiveData<List<Account>> getFriendsLiveData() {
        return friends;
    }

    public MutableLiveData<List<Account>> getFriendRequestLiveData() {
        return friendRequests;
    }

    public Account getAccount() {
        return account;
    }

    private void loadFriends() {
        String url = getApplication().getResources().
                getString(R.string.baseUrl) + getApplication().getResources().getString(R.string.getRelationships);
        GetRelationshipsRequest relationshipsRequest =
                new GetRelationshipsRequest(account.getPersonId());
        JSONObject jsonObject = null;
        try {
            jsonObject = createRequest(relationshipsRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendRequest(url, Request.Method.POST, jsonObject);
    }

    private JSONObject createRequest(com.example.spi.localize2socialize.models.Request request) throws JSONException {
        Gson gson = new GsonBuilder().create();
        return new JSONObject(gson.toJson(request));
    }

    private void sendRequest(String url, int method, JSONObject jsonRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonRequest,
                getResponseListener(), getResponseErrorListener());
        RequestQueueSingleton.getInstance(getApplication()).addToRequestQueue(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("friends") && response.has("pendingAccounts")) {
                    try {
                        friends.setValue(convertResponseToList(response, "friends"));
                        friendRequests.setValue(convertResponseToList(response, "pendingAccounts"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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

    private List<Account> convertResponseToList(JSONObject response, String key) throws JSONException {
        JSONArray result = response.getJSONArray(key);
        String json = result.toString();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Account>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    //MOCK
    private Function<String, Friend> mockToFriendRequest = new Function<String, Friend>() {
        @Override
        public Friend apply(String s) {
            Account account = new Account(s, s, s, s, s, null);
            Friend friend = new Friend(account, true);
            return friend;
        }
    };

    //MOCK
    private Function<String, Friend> mockToFriend = new Function<String, Friend>() {
        @Override
        public Friend apply(String s) {
            Account account = new Account(s, s, s, s, s, null);
            Friend friend = new Friend(account, false);
            return friend;
        }
    };
}
