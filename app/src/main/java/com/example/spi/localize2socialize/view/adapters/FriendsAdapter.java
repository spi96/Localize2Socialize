package com.example.spi.localize2socialize.view.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.model.Account;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private final List<Account> friends;
    private SparseBooleanArray selectedFriends;

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        int position;

        private CardView cardView;
        private TextView friendTextView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            friendTextView = itemView.findViewById(R.id.friend_name);
            cardView = itemView.findViewById(R.id.friendItemCV);
        }
    }

    public FriendsAdapter(List<Account> friends) {
        this.friends = friends;
        this.selectedFriends = new SparseBooleanArray();
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {
        holder.position = position;
        Account account = friends.get(holder.getAdapterPosition());
        holder.friendTextView.setText(new StringBuilder(account.getPersonFamilyName()).append(" ")
                .append(account.getPersonGivenName()));

        int colorBackground = selectedFriends.get(position) ? R.color.colorSelectedItem : R.color.colorBackgroundNotSelectedItem;
        int colorText = selectedFriends.get(position) ? R.color.colorTextSelectedItem : R.color.colorFriendsText;
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), colorBackground));
        holder.friendTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorText));
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

    private void selectView(int position, boolean value) {
        if (value) {
            selectedFriends.put(position, value);
        } else {
            selectedFriends.delete(position);
        }

        notifyDataSetChanged();
    }

    public void selectAll() {
        boolean isAllSelected = selectedFriends.size() == friends.size();
        selectedFriends.clear();

        if (!isAllSelected) {
            int i = 0;
            for (Account friend : friends) {
                selectedFriends.put(i, true);
                i++;
            }
        }

        notifyDataSetChanged();
    }

    public void updateAdapter(List<Account> friends) {
        this.friends.clear();
        if (friends != null)
            this.friends.addAll(friends);
        notifyDataSetChanged();
    }

    public int getSelectedFriendsCount() {
        return selectedFriends.size();
    }

    public List<Account> getSelectedFriends() {
        List<Account> selectedFriendList = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            if (selectedFriends.get(i)) {
                selectedFriendList.add(friends.get(i));
            }
        }
        return selectedFriendList;
    }

    public List<Account> getFriends() {
        return friends;
    }
}
