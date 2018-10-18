package layout;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import models.User;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private List<User> friends;

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;

        public FriendsViewHolder(TextView textView) {
            super(textView);
            nameTextView = textView;
        }
    }

    public FriendsAdapter(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
