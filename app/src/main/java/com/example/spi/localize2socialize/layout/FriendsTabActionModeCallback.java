package com.example.spi.localize2socialize.layout;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.example.spi.localize2socialize.MainActivity;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.layout.adapters.FriendsAdapter;

public class FriendsTabActionModeCallback implements ActionMode.Callback {
    private FriendsAdapter friendsAdapter;
    private Context context;

    public FriendsTabActionModeCallback(FriendsAdapter friendsAdapter, Context context) {
        this.friendsAdapter = friendsAdapter;
        this.context = context;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.item_selection_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                checkPermission();
                break;
            case R.id.action_select_all:
                friendsAdapter.selectAll();
                updateActionModeTitle();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        friendsAdapter.removeSelection();
        ((FriendsTab) ((MainActivity) context).getFragment(1)).setActionModeNull();
    }

    private void checkPermission() {
        ((FriendsTab) ((MainActivity) context).getFragment(1)).checkPermission();
    }

    private void updateActionModeTitle() {
        ((FriendsTab) ((MainActivity) context).getFragment(1)).updateActionModeTitle(friendsAdapter);
    }
}
