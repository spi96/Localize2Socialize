package com.example.spi.localize2socialize.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.spi.localize2socialize.models.Account;
import com.example.spi.localize2socialize.models.SearchRequest;
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
    private static String personId;
    private String selectedPersonId;
    private ArrayAdapter<Account> filteredAccounts;

    private AppCompatAutoCompleteTextView autoCompleteTextView;
    private Button cancelButton;
    private Button sendRequestButton;

    public SearchDialog() {
    }

    public static SearchDialog newInstance(String _personId) {
        SearchDialog searchDialog = new SearchDialog();
        personId = _personId;
        return searchDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filteredAccounts = new ArrayAdapter<Account>(getContext(), android.R.layout.simple_dropdown_item_1line);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_friend_dialog, container);
        autoCompleteTextView = (AppCompatAutoCompleteTextView) view.findViewById(R.id.auto_complete_search_friends);
        sendRequestButton = view.findViewById(R.id.action_send_friend_request);
        cancelButton = view.findViewById(R.id.action_search_cancel);

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
                //TODO immplement
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
        if (s.length() > 2) {
            searchAccounts(s);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedPersonId = filteredAccounts.getItem(position).getPersonId();
    }

    public void searchAccounts(CharSequence filter) {
        String url = getResources().
                getString(R.string.baseUrl) + getResources().getString(R.string.searchAccounts);
        JSONObject jsonObject = null;
        try {
            jsonObject = createRequest(filter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendRequest(url, Request.Method.POST, jsonObject);
    }

    private JSONObject createRequest(CharSequence filter) throws JSONException {
        Gson gson = new GsonBuilder().create();
        SearchRequest request = new SearchRequest(filter.toString(), personId);
        return new JSONObject(gson.toJson(request));
    }

    public void sendRequest(String url, int method, JSONObject jsonRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonRequest,
                getResponseListener(), getResponseErrorListener());
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                filteredAccounts.clear();
                if (response.has("accounts")) {
                    try {
                        JSONArray result = response.getJSONArray("accounts");
                        String json = result.toString();
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Account>>() {
                        }.getType();
                        List<Account> accountList = gson.fromJson(json, type);
                        filteredAccounts.addAll(accountList);
                        filteredAccounts.notifyDataSetChanged();
                        //filteredAccounts.addAll(response.getJSONArray("accounts"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
}
