package com.example.spi.localize2socialize.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spi.localize2socialize.Global;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.model.Account;
import com.example.spi.localize2socialize.model.Calendar;
import com.example.spi.localize2socialize.model.Event;
import com.example.spi.localize2socialize.model.Post;
import com.example.spi.localize2socialize.net.RequestQueueSingleton;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EventsTabViewModel extends AndroidViewModel {
    private final String REQUEST_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private MutableLiveData<List<MarkerOptions>> markerOptions = new MutableLiveData<>();

    private Marker postMarker;
    private Location mLastKnownLocation;

    public EventsTabViewModel(@NonNull Application application) {
        super(application);
    }

    public Marker getPostMarker() {
        return postMarker;
    }

    public void setPostMarker(Marker postMarker) {
        this.postMarker = postMarker;
    }

    public Location getmLastKnownLocation() {
        return mLastKnownLocation;
    }

    public void setmLastKnownLocation(Location mLastKnownLocation) {
        this.mLastKnownLocation = mLastKnownLocation;
    }

    public MutableLiveData<List<MarkerOptions>> getMarkerOptionLiveData() {
        return markerOptions;
    }

    public List<MarkerOptions> getMarkerOptionList() {
        if (markerOptions == null) {
            markerOptions = new MutableLiveData<>();
            loadSharings();
            return new ArrayList<>();
        }
        return markerOptions.getValue();
    }

    public void loadSharings() {
        String url = getApplication().getResources().
                getString(R.string.baseUrl) + getApplication().getResources().getString(R.string.getItemsForMap);
        Account account = ((Global) getApplication()).getUser();
        JSONObject jsonObject = null;
        try {
            jsonObject = createRequest(account);
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
        RequestQueueSingleton.getInstance(getApplication()).addToRequestQueue(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("calendars") && response.has("posts")) {
                        convertResponse(response);
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

    private void convertResponse(JSONObject response) throws JSONException {
        Type calendarListType = new TypeToken<List<Calendar>>() {
        }.getType();
        Type postListType = new TypeToken<List<Post>>() {
        }.getType();

        JSONArray calendarResult = response.getJSONArray("calendars");
        JSONArray postResult = response.getJSONArray("posts");

        String calendarJson = calendarResult.toString();
        String postJson = postResult.toString();

        Gson gson = new GsonBuilder().setDateFormat(REQUEST_DATE_FORMAT).create();

        //deleteExistingMarkers(); TODO
        List<Calendar> calendars = gson.fromJson(calendarJson, calendarListType);
        List<Post> posts = gson.fromJson(postJson, postListType);
        convertItemsToMarkerOption(calendars, posts);
    }

    private void convertItemsToMarkerOption(List<Calendar> calendars, List<Post> posts) {
        List<MarkerOptions> eventMarkers = new ArrayList<>();
        for (Calendar calendar : calendars) {
            for (Event event : calendar.getEvents()) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(event.getLocationLatitude(), event.getLocationLongitude()))
                        //.title(event.getTitle())
                        //.snippet(event.getLocationName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                        .draggable(false);
                eventMarkers.add(markerOptions);
            }
        }

        for (Post post : posts) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(post.getLocationLatitude(), post.getLocationLongitude()))
                    .title(post.getDescription())
                    .snippet(post.getLocationName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .draggable(false);
            eventMarkers.add(markerOptions);
        }
        markerOptions.setValue(eventMarkers);
    }
}
