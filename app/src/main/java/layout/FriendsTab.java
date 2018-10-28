package layout;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.RecyclerClickListener;
import com.example.spi.localize2socialize.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import layout.adapters.FriendRequestAdapter;
import layout.adapters.FriendsAdapter;
import models.Friend;
import models.User;
import viewmodels.FriendsTabViewModel;

public class FriendsTab extends Fragment {
    private FriendsTabViewModel mViewModel;

    private RecyclerView requestRecyclerView;
    private RecyclerView friendsRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ActionMode actionMode;

    public static FriendsTab newInstance() {
        return new FriendsTab();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_tab_fragment, container, false);

        requestRecyclerView = (RecyclerView) view.findViewById(R.id.friendRequests);
        friendsRecyclerView = (RecyclerView) view.findViewById(R.id.friends);

        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //TODO MOCK!!!
        List<String> friends = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.friends)));
        List<String> friendRequests = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.friend_requests)));

        FriendRequestAdapter friendRequestAdapter = new FriendRequestAdapter(friendRequests.stream().map(mockToFriendRequest).collect(Collectors.<Friend>toList()));
        FriendsAdapter friendsAdapter = new FriendsAdapter(friends.stream().map(mockToFriend).collect(Collectors.<Friend>toList()));

        requestRecyclerView.setAdapter(friendRequestAdapter);
        friendsRecyclerView.setAdapter(friendsAdapter);

        friendRequestAdapter.notifyDataSetChanged();
        friendsAdapter.notifyDataSetChanged();

        friendsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), friendsRecyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (actionMode != null) {
                    onListItemSelect(position);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                onListItemSelect(position);
            }
        }));

        return view;
    }

    private void onListItemSelect(int position) {
        FriendsAdapter friendsAdapter = (FriendsAdapter) friendsRecyclerView.getAdapter();
        friendsAdapter.toggleSelection(position);

        boolean hasSelectedItems = friendsAdapter.getSelectedFriendsCount() > 0;

        if (hasSelectedItems && actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new FriendsTabActionModeCallback(friendsAdapter, getContext()));
        } else if (!hasSelectedItems && actionMode != null) {
            actionMode.finish();
            setActionModeNull();
        }

        if (actionMode != null) {
            actionMode.setTitle(friendsAdapter.getSelectedFriendsCount() + " selected");
        }
    }

    public void setActionModeNull() {
        if (actionMode != null) {
            actionMode = null;
        }
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FriendsTabViewModel.class);

    }

    Function<String, Friend> mockToFriendRequest = new Function<String, Friend>() {
        @Override
        public Friend apply(String s) {
            User user = new User(s, s, s, s, s);
            Friend friend = new Friend(user, true);
            return friend;
        }
    };

    Function<String, Friend> mockToFriend = new Function<String, Friend>() {
        @Override
        public Friend apply(String s) {
            User user = new User(s, s, s, s, s);
            Friend friend = new Friend(user, false);
            return friend;
        }
    };
}
