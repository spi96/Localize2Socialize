package layout;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spi.localize2socialize.R;

import viewmodels.FriendsTabViewModel;

public class FriendsTab extends Fragment {

    private FriendsTabViewModel mViewModel;

    private RecyclerView requestRecyclerView;
    private RecyclerView friendsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static FriendsTab newInstance() {
        return new FriendsTab();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_tab_fragment, container, false);

        requestRecyclerView = (RecyclerView) view.findViewById(R.id.friendRequests);
        friendsRecyclerView = (RecyclerView) view.findViewById(R.id.friends);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FriendsTabViewModel.class);
        // TODO: Use the ViewModel
    }

}