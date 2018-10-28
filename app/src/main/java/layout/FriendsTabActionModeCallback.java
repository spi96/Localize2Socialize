package layout;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.example.spi.localize2socialize.MainActivity;
import com.example.spi.localize2socialize.R;

import layout.adapters.FriendsAdapter;

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
                mode.finish();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        friendsAdapter.removeSelection();
    }
}
