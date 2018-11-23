package com.example.spi.localize2socialize.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

import com.example.spi.localize2socialize.model.Event;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class EventsTabViewModel extends ViewModel {
    private MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private Marker postMarker;
    private Location mLastKnownLocation;

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

    public List<Event> getEventList() {
        if (events == null) {
            events.postValue(loadEvents());
        }
        return events.getValue();
    }

    private List<Event> loadEvents() {
        return null;
    }
}
