package com.example.spi.localize2socialize.layout;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spi.localize2socialize.MainActivity;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.RecyclerClickListener;
import com.example.spi.localize2socialize.RecyclerTouchListener;
import com.example.spi.localize2socialize.layout.adapters.FriendRequestAdapter;
import com.example.spi.localize2socialize.layout.adapters.FriendsAdapter;
import com.example.spi.localize2socialize.models.Friend;
import com.example.spi.localize2socialize.viewmodels.FriendsTabViewModel;

import java.util.List;

public class FriendsTab extends Fragment {
    static final int REQUEST_READ_CALENDAR = 1;
    static final int REQUEST_SHOW_SHARING_DIALOG = 2;
    private static final String DIALOG_TAG = "SHARING_DIALOG";

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
        final View view = inflater.inflate(R.layout.friends_tab_fragment, container, false);

        requestRecyclerView = (RecyclerView) view.findViewById(R.id.friendRequests);
        friendsRecyclerView = (RecyclerView) view.findViewById(R.id.friends);

        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewModel = ViewModelProviders.of(this).get(FriendsTabViewModel.class);

        final Observer friendRequestObserver = new Observer<List<Friend>>() {
            @Override
            public void onChanged(@Nullable List<Friend> friends) {
                int visibility = friends.size() > 0 ? View.VISIBLE : View.GONE;
                view.findViewById(R.id.friendRequestCV).setVisibility(visibility);
            }
        };

        mViewModel.getFriendRequestLiveData().observe(this, friendRequestObserver);

        FriendRequestAdapter friendRequestAdapter = new FriendRequestAdapter(mViewModel.getFriendRequests());
        FriendsAdapter friendsAdapter = new FriendsAdapter(mViewModel.getFriendsList());

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
            actionMode = ((MainActivity) getActivity()).startSupportActionMode(new FriendsTabActionModeCallback(friendsAdapter, getContext()));
        }

        if (actionMode != null) {
            updateActionModeTitle(friendsAdapter);
        }
    }

    public void updateActionModeTitle(FriendsAdapter friendsAdapter) {
        actionMode.setTitle(friendsAdapter.getSelectedFriendsCount() + " selected");
    }

    public void setActionModeNull() {
        if (actionMode != null) {
            actionMode = null;
        }
    }

    public void finishActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FriendsTabViewModel.class);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void checkPermission() {
        if (checkFriendSelected() == false) return;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, FriendsTab.REQUEST_READ_CALENDAR);
        } else {
            showSharingDialog();
        }
    }

    private boolean checkFriendSelected() {
        if (((FriendsAdapter) friendsRecyclerView.getAdapter()).getSelectedFriendsCount() == 0) {
            Snackbar.make(getActivity().findViewById(R.id.main_content), R.string.no_friend_selected, Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void showSharingDialog() {
        FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
        ShareDialog shareDialog = ShareDialog.newInstance();
        shareDialog.setTargetFragment(this, REQUEST_SHOW_SHARING_DIALOG);
        shareDialog.show(fragmentManager, DIALOG_TAG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_READ_CALENDAR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showSharingDialog();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.main_content), R.string.calendar_permission_needed, Snackbar.LENGTH_LONG).show();
                }
        }
    }
}
