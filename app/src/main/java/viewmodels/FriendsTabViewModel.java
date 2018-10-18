package viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import models.User;

public class FriendsTabViewModel extends ViewModel {
    private MutableLiveData<List<User>> friends = new MutableLiveData<>();

    private MutableLiveData<List<User>> friendRequests = new MutableLiveData<>();

    public List<User> getFriendsList() {
        if (friends == null) {
            friends.postValue(loadFriends());
        }
        return friends.getValue();
    }

    public List<User> getFriendREquests() {
        if (friendRequests == null) {
            friendRequests.postValue(loadFriendRequests());
        }
        return friendRequests.getValue();
    }

    private List<User> loadFriendRequests() {
        return null;
    }

    private List<User> loadFriends() {
        return null;
    }
}
