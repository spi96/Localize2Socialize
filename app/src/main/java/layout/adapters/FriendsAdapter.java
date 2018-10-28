package layout.adapters;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spi.localize2socialize.R;

import java.util.ArrayList;
import java.util.List;

import models.Friend;
import models.User;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private List<Friend> friends;
    private SparseBooleanArray selectedFriends;

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        int position;

        private CardView cardView;
        private TextView friendTextView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            friendTextView = (TextView) itemView.findViewById(R.id.friend_name);
            cardView = (CardView) itemView.findViewById(R.id.friendItemCV);
        }
    }

    public FriendsAdapter(List<Friend> friends) {
        this.friends = friends;
        this.selectedFriends = new SparseBooleanArray();
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        FriendsViewHolder friendsViewHolder = new FriendsViewHolder(view);

        return friendsViewHolder;
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {
        holder.position = position;
        holder.friendTextView.setText(friends.get(position).getUser().getPersonName());

        int color = selectedFriends.get(position) ? R.color.colorSelectedItem : R.color.colorCardView;
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), color));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedFriends.get(position));
    }

    public void removeSelection() {
        selectedFriends = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            selectedFriends.put(position, value);
        } else {
            selectedFriends.delete(position);
        }

        notifyDataSetChanged();
    }

    public int getSelectedFriendsCount() {
        return selectedFriends.size();
    }

    public SparseBooleanArray getSelectedFriends() {
        return selectedFriends;
    }
}
