package com.example.spi.localize2socialize.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.model.Account;
import com.example.spi.localize2socialize.model.FriendRequest;
import com.example.spi.localize2socialize.model.SearchRequest;
import com.example.spi.localize2socialize.net.RequestQueueSingleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class SearchDialog extends DialogFragment implements View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {
    private static Account person;
    private Account selectedPerson;
    private ArrayAdapter<Account> filteredAccounts;

    private AppCompatAutoCompleteTextView autoCompleteTextView;
    private Button cancelButton;
    private Button sendRequestButton;

    public SearchDialog() {
    }

    public static SearchDialog newInstance(Account _person) {
        SearchDialog searchDialog = new SearchDialog();
        person = _person;
        return searchDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filteredAccounts = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);
        filteredAccounts.setNotifyOnChange(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_friend_dialog, container);
        autoCompleteTextView = view.findViewById(R.id.auto_complete_search_friends);
        sendRequestButton = view.findViewById(R.id.action_send_friend_request);
        cancelButton = view.findViewById(R.id.action_search_cancel);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        autoCompleteTextView.setAdapter(filteredAccounts);
        setActionListeners();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_search_cancel:
                dismiss();
                break;
            case R.id.action_send_friend_request:
                sendFriendRequest();
                break;
        }
    }

    private void setActionListeners() {
        autoCompleteTextView.setOnItemClickListener(this);
        autoCompleteTextView.addTextChangedListener(this);
        sendRequestButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 1) {
            searchAccounts(s);
        }
        if (autoCompleteTextView.getError() != null && autoCompleteTextView.getError().length() > 0) {
            autoCompleteTextView.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedPerson = filteredAccounts.getItem(position);
    }

    public void searchAccounts(CharSequence filter) {
        String url = getResources().
                getString(R.string.baseUrl) + getResources().getString(R.string.searchAccounts);
        SearchRequest request = new SearchRequest(filter.toString(), person);
        JSONObject jsonObject = null;
        try {
            jsonObject = createRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendRequest(url, Request.Method.POST, jsonObject);
    }

    private JSONObject createRequest(Object request) throws JSONException {
        Gson gson = new GsonBuilder().create();
        return new JSONObject(gson.toJson(request));
    }

    private void sendRequest(String url, int method, JSONObject jsonRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonRequest,
                getResponseListener(), getResponseErrorListener());
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("accounts")) {
                        filteredAccounts.clear();
                        List<Account> accountList = convertResponseToList(response);
                        filteredAccounts.addAll(accountList);

                    } else if (response.has("friendRequest")) {
                        String message;
                        if (response.getBoolean("friendRequest")) {
                            message = getResources().getString(R.string.request_sent);
                        } else {
                            message = getResources().getString(R.string.relationship_exists);
                        }
                        showSnackbar(message);
                        dismiss();
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

    private List<Account> convertResponseToList(JSONObject response) throws JSONException {
        JSONArray result = response.getJSONArray("accounts");
        String json = result.toString();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Account>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    private void sendFriendRequest() {
        String url = getResources().
                getString(R.string.baseUrl) + getResources().getString(R.string.sendRequest);

        if (selectedPerson != null) {
            FriendRequest friendRequest = new FriendRequest(person.getPersonId(), selectedPerson.getPersonId());
            JSONObject jsonObject = null;
            try {
                jsonObject = createRequest(friendRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendRequest(url, Request.Method.POST, jsonObject);
        } else {
            autoCompleteTextView.requestFocus();
            autoCompleteTextView.setError(getResources().getString(R.string.person_not_found));
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(getActivity().findViewById(R.id.main_content), message, Snackbar.LENGTH_LONG).show();
    }
}
