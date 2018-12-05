package com.example.spi.localize2socialize.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingleton {
    private static RequestQueueSingleton singleton;
    private RequestQueue requestQueue;

    private RequestQueueSingleton(Context context) {
        requestQueue = getRequestQueue(context);
    }

    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new RequestQueueSingleton(context);
        }
        return singleton;
    }

    private RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }
}
