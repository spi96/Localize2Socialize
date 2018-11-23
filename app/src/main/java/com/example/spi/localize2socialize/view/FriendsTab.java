package com.example.spi.localize2socialize.view;

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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.model.Account;
import com.example.spi.localize2socialize.view.adapters.FriendRequestAdapter;
import com.example.spi.localize2socialize.view.adapters.FriendsAdapter;
import com.example.spi.localize2socialize.viewmodel.FriendsTabViewModel;

import java.util.List;

public class FriendsTab extends Fragment implements View.OnClickListener, RefreshClickListener, OnViewHolderButtonsClickListener {
    static final int REQUEST_READ_CALENDAR = 1;
    static final int REQUEST_SHOW_SHARING_DIALOG = 2;
    static final int REQUEST_SEARCH_FRIENDS_DIALOG = 3;

    private static final String SHARING_DIALOG_TAG = "SHARING_DIALOG";
    private static final String SEARCHING_DIALOG_TAG = "SEARCHING_DIALOG";

    private FriendsTabViewModel mViewModel;

    private RecyclerView requestRecyclerView;
    private RecyclerView friendsRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    CardView friendRequestCardView;
    CardView friendsCardView;

    private ActionMode actionMode;

    public static FriendsTab newInstance() {
        return new FriendsTab();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.friends_tab_fragment, container, false);

        requestRecyclerView = view.findViewById(R.id.friendRequests);
        friendsRecyclerView = view.findViewById(R.id.friends);

        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        friendRequestCardView = view.findViewById(R.id.friendRequestCV);
        friendsCardView = view.findViewById(R.id.friendsCV);

        mViewModel = ViewModelProviders.of(this).get(FriendsTabViewModel.class);

        final Observer friendRequestObserver = new Observer<List<Account>>() {
            @Override
            public void onChanged(@Nullable List<Account> friendRequests) {
                ((FriendRequestAdapter) requestRecyclerView.getAdapter()).updateAdapter(friendRequests);
                int requestVisibility = friendRequests.size() > 0 ? View.VISIBLE : View.GONE;
                float friendsWeight = friendRequests.size() > 0 ? 7f : 10f;

                float density = getContext().getResources().getDisplayMetrics().density;
                int marginHorizontalDP = (int) (8 * density);
                int marginBottomDP = (int) (16 * density);
                int marginTopDP = friendRequests.size() > 0 ? marginHorizontalDP : marginBottomDP;

                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, friendsWeight);
                layoutParams.setMargins(marginHorizontalDP, marginTopDP, marginHorizontalDP, marginBottomDP);

                friendRequestCardView.setVisibility(requestVisibility);
                friendsCardView.setLayoutParams(layoutParams);
            }
        };

        final Observer friendsObserver = new Observer<List<Account>>() {

            @Override
            public void onChanged(@Nullable List<Account> friends) {
                ((FriendsAdapter) friendsRecyclerView.getAdapter()).updateAdapter(friends);
                //int friendsVisibility = friends.size() > 0 ? View.VISIBLE : View.INVISIBLE;
                //friendsCardView.setVisibility(friendsVisibility);
            }
        };

        mViewModel.getFriendRequestLiveData().observe(this, friendRequestObserver);
        mViewModel.getFriendsLiveData().observe(this, friendsObserver);

        FriendRequestAdapter friendRequestAdapter = new FriendRequestAdapter(mViewModel.getFriendRequests(), this);
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
        if (!checkFriendSelected()) return;

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
        List<Account> selectedFriends = ((FriendsAdapter) friendsRecyclerView.getAdapter()).getSelectedFriends();
        FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
        ShareDialog shareDialog = ShareDialog.newInstance(mViewModel.getAccount(), selectedFriends);
        shareDialog.setTargetFragment(this, REQUEST_SHOW_SHARING_DIALOG);
        shareDialog.show(fragmentManager, SHARING_DIALOG_TAG);
    }

    public void showSearchFriendsDialog() {
        FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
        SearchDialog searchFriendsDialog = SearchDialog.newInstance(mViewModel.getAccount());
        searchFriendsDialog.setTargetFragment(this, REQUEST_SEARCH_FRIENDS_DIALOG);
        searchFriendsDialog.show(fragmentManager, SEARCHING_DIALOG_TAG);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                showSearchFriendsDialog();
        }
    }

    @Override
    public void onRefreshClick() {
        mViewModel.refresh();
    }

    @Override
    public void buttonClicked(int position, boolean isAccepted) {
        Account request = ((FriendRequestAdapter) requestRecyclerView.getAdapter()).getRequest(position);
        mViewModel.handleFriendRequest(request, isAccepted);
    }
}
