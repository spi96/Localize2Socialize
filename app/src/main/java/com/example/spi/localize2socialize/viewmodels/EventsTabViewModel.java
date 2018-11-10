package com.example.spi.localize2socialize.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.spi.localize2socialize.models.Event;

import java.util.List;

public class EventsTabViewModel extends ViewModel {
    private MutableLiveData<List<Event>> events = new MutableLiveData<>();

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
