package com.example.spi.localize2socialize.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spi.localize2socialize.Global;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.model.Account;
import com.example.spi.localize2socialize.model.Calendar;
import com.example.spi.localize2socialize.model.Post;
import com.example.spi.localize2socialize.net.RequestQueueSingleton;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsTabViewModel extends AndroidViewModel {
    private final String REQUEST_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private MutableLiveData<List<MarkerOptions>> markerOptions = new MutableLiveData<>();

    private MutableLiveData<List<Calendar>> calendars = new MutableLiveData<>();
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();

    private Marker postMarker;
    private Location mLastKnownLocation;
    private Bitmap postAttachedImage;
    private String postDescription;
    private String postLocationName;

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

    public MutableLiveData<List<Calendar>> getCalendarLiveData() {
        return calendars;
    }

    public MutableLiveData<List<Post>> getPostLiveData() {
        return posts;
    }

    public List<Calendar> getCalendars() {
        if (calendars == null) {
            calendars = new MutableLiveData<>();
            loadSharings();
            return new ArrayList<>();
        }
        return calendars.getValue();
    }

    public List<Post> getPosts() {
        if (posts == null) {
            posts = new MutableLiveData<>();
            loadSharings();
            return new ArrayList<>();
        }
        return posts.getValue();
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public Bitmap getPostAttachedImage() {
        return postAttachedImage;
    }

    public void setPostAttachedImage(Bitmap postAttachedImage) {
        if (this.postAttachedImage != null) this.postAttachedImage.recycle();
        this.postAttachedImage = postAttachedImage;
    }

    public String getPostLocationName() {
        return postLocationName;
    }

    public void setPostLocationName(String postLocationName) {
        this.postLocationName = postLocationName;
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
        Gson gson = new GsonBuilder().setDateFormat(REQUEST_DATE_FORMAT).create();
        return new JSONObject(gson.toJson(request));
    }

    private void sendRequest(String url, int method, JSONObject jsonRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonRequest,
                getResponseListener(), getResponseErrorListener());
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getApplication()).addToRequestQueue(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("calendars") && response.has("posts")) {
                        convertResponse(response);
                    } else if (response.has("postResponse")) {
                        boolean successful = response.getBoolean("postResponse");
                        if (successful) {

                        }
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

        List<Calendar> calendarList = gson.fromJson(calendarJson, calendarListType);
        List<Post> postList = gson.fromJson(postJson, postListType);
        calendars.setValue(calendarList);
        posts.setValue(postList);
    }

    private String encodePhoto(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap decodePhoto(String encoded) {
        byte[] decoded = Base64.decode(encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
    }

    public void savePost(String postDescription) {
        this.postDescription = postDescription;
        String encodedPhoto = postAttachedImage == null ? "" : encodePhoto(postAttachedImage);
        Account account = ((Global) getApplication()).getUser();
        Date current = android.icu.util.Calendar.getInstance().getTime();
        Post post = new Post(account, encodedPhoto, postDescription, current, current,
                postMarker.getPosition().latitude, postMarker.getPosition().longitude, postLocationName);
        JSONObject request = null;
        try {
            request = createRequest(post);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getApplication().getResources().
                getString(R.string.baseUrl) + getApplication().getResources().getString(R.string.savePost);
        sendRequest(url, Request.Method.POST, request);
    }
}
