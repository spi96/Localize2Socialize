package com.example.spi.localize2socialize.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.models.Account;
import com.example.spi.localize2socialize.models.Friend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FriendsTabViewModel extends AndroidViewModel {
    private MutableLiveData<List<Friend>> friends = new MutableLiveData<>();

    private MutableLiveData<List<Friend>> friendRequests = new MutableLiveData<>();

    public FriendsTabViewModel(@NonNull Application application) {
        super(application);
        friends.setValue(loadFriends());
        friendRequests.setValue(loadFriendRequests());
    }

    public List<Friend> getFriendsList() {
        if (friends == null) {
            friends.setValue(loadFriends());
        }
        return friends.getValue();
    }

    public List<Friend> getFriendRequests() {
        if (friendRequests == null) {
            friendRequests.setValue(loadFriendRequests());
        }
        return friendRequests.getValue();
    }

    public MutableLiveData<List<Friend>> getFriendRequestLiveData() {
        return friendRequests;
    }

    private List<Friend> loadFriendRequests() {
        //TODO MOCK!!!
        List<String> friendRequests = new ArrayList<>(Arrays.asList(getApplication().getResources().getStringArray(R.array.friend_requests)));
        return friendRequests.stream().map(mockToFriendRequest).collect(Collectors.<Friend>toList());
    }

    private List<Friend> loadFriends() {
        List<String> friends = new ArrayList<>(Arrays.asList(getApplication().getResources().getStringArray(R.array.friends)));
        return friends.stream().map(mockToFriend).collect(Collectors.<Friend>toList());
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
