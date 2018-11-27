package com.example.spi.localize2socialize.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spi.localize2socialize.Global;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.model.Account;
import com.example.spi.localize2socialize.model.DeleteRelationshipsRequest;
import com.example.spi.localize2socialize.model.Friend;
import com.example.spi.localize2socialize.model.GetRelationshipsRequest;
import com.example.spi.localize2socialize.model.HandleRelationshipRequest;
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

import static com.android.volley.Request.Method.POST;

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
            return new ArrayList<>();
        }
        return friends.getValue();
    }

    public List<Account> getFriendRequests() {
        if (friendRequests == null) {
            friendRequests = new MutableLiveData<>();
            loadFriends();
            return new ArrayList<>();
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
        sendRequest(url, POST, jsonObject);
    }

    private JSONObject createRequest(Object request) throws JSONException {
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
                try {
                    if (response.has("friends") && response.has("pendingAccounts")) {
                        friends.setValue(convertResponseToList(response, "friends"));
                        friendRequests.setValue(convertResponseToList(response, "pendingAccounts"));
                    } else if (response.has("senderPersonAccepted")) {
                        if (!response.isNull("senderPersonAccepted")) {
                            Account senderPerson = convertResponseToAccount(response, "senderPersonAccepted");
                            List<Account> displayedRequests = friendRequests.getValue();
                            displayedRequests.remove(senderPerson);
                            List<Account> displayedFriends = friends.getValue();
                            displayedFriends.add(senderPerson);
                            friendRequests.setValue(displayedRequests);
                            friends.setValue(displayedFriends);
                        }
                    } else if (response.has("senderPersonDenied")) {
                        if (!response.isNull("senderPersonDenied")) {
                            Account senderPerson = convertResponseToAccount(response, "senderPersonDenied");
                            List<Account> displayedRequests = friendRequests.getValue();
                            displayedRequests.remove(senderPerson);
                            friendRequests.setValue(displayedRequests);
                        }
                    } else if (response.has("relationshipDelete")) {
                        List<Account> deletedFriends = convertResponseToList(response, "relationshipDelete");
                        List<Account> displayedFriends = friends.getValue();
                        displayedFriends.removeAll(deletedFriends);
                        friends.setValue(displayedFriends);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<Account>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    private Account convertResponseToAccount(JSONObject response, String key) throws JSONException {
        String json = response.getJSONObject(key).toString();
        Gson gson = new Gson();
        Type type = new TypeToken<Account>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    public void refresh() {
        loadFriends();
    }

    public void deleteFriends(List<Account> selectedFriends) throws JSONException {
        String url = getApplication().getResources().
                getString(R.string.baseUrl) + getApplication().getResources().getString(R.string.deleteRelationships);

        DeleteRelationshipsRequest request = new DeleteRelationshipsRequest(selectedFriends, account);
        JSONObject jsonObject = createRequest(request);
        sendRequest(url, POST, jsonObject);
    }

    public void handleFriendRequest(Account sender, boolean isAccepted) {
        String url = getApplication().getResources().
                getString(R.string.baseUrl) + getApplication().getResources().getString(R.string.handleRequest);
        HandleRelationshipRequest relationshipRequest = new HandleRelationshipRequest(sender.getPersonId(), account.getPersonId(), isAccepted);

        JSONObject jsonObject = null;
        try {
            jsonObject = createRequest(relationshipRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendRequest(url, POST, jsonObject);
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
