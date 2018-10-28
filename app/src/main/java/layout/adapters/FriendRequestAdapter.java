package layout.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spi.localize2socialize.R;

import java.util.ArrayList;
import java.util.List;

import models.Friend;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private final List<Friend> requests;

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        int position;

        TextView friendTextView;
        ImageButton denyButton;
        ImageButton acceptButton;
        LinearLayout actionButtonLayout;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            friendTextView = (TextView) itemView.findViewById(R.id.friend_name);
            denyButton = (ImageButton) itemView.findViewById(R.id.action_request_deny);
            acceptButton = (ImageButton) itemView.findViewById(R.id.action_request_accept);
            actionButtonLayout = (LinearLayout) itemView.findViewById(R.id.action_button_layout);
        }
    }

    public FriendRequestAdapter(List<Friend> friends) {
        this.requests = friends;
    }

    @Override
    public FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        FriendRequestViewHolder friendRequestViewHolder = new FriendRequestViewHolder(view);

        friendRequestViewHolder.actionButtonLayout.setVisibility(View.VISIBLE);

        return friendRequestViewHolder;
    }

    @Override
    public void onBindViewHolder(FriendRequestViewHolder holder, int position) {
        holder.position = position;
        holder.friendTextView.setText(requests.get(position).getUser().getPersonName());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
