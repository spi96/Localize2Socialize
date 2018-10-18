package viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import models.Event;

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
