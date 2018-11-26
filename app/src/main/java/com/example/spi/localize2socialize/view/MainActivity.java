package com.example.spi.localize2socialize.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.spi.localize2socialize.Global;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.viewmodel.MainActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = "MainActivity";

    private MainActivityViewModel viewModel;
    private RefreshClickListener refreshClickListener;

    Context context;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    TabLayout tabLayout;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInAccount account;
        account = getIntent().getParcelableExtra("ACCOUNT");

        final Global global = (Global) getApplication();

        Bitmap photo = null;  //TODO Glide.with(this).load(account.getPhotoUrl()).into(PhotoImageview);
        try {
            if (account.getPhotoUrl() != null)
                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), account.getPhotoUrl()); //TODO
        } catch (IOException e) {
            e.printStackTrace();
        }

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.setUser(account, photo);
        global.setUser(viewModel.getUser());

        setContentView(R.layout.activity_main);

        floatingActionButton = findViewById(R.id.fab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setUpFragments(mSectionsPagerAdapter);

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        invalidateOptionsMenu();
                        floatingActionButton.setVisibility(View.GONE);
                        break;
                    case 1:
                        floatingActionButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        EventsTab eventsTab = (EventsTab) getFragment(0);
                        eventsTab.resetTab();
                        invalidateOptionsMenu();
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }

    private void setUpFragments(SectionsPagerAdapter sectionsPagerAdapter) {
        EventsTab eventsTab = EventsTab.newInstance();
        refreshClickListener = eventsTab;
        floatingActionButton.setOnClickListener(eventsTab);
        sectionsPagerAdapter.addFragment(eventsTab, "Events");
        sectionsPagerAdapter.addFragment(FriendsTab.newInstance(), "Friends");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Fragment getFragment(int position) {
        return mSectionsPagerAdapter.getItem(position);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewModel.getUser().getId() == null)
            viewModel.saveOrUpdateAccount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.menu_search);
        MenuItem settings = menu.findItem(R.id.menu_settings);
        if (tabLayout.getSelectedTabPosition() == 0) {
            search.setVisible(true);
            settings.setVisible(true);

        } else {
            search.setVisible(false);
            settings.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
            signOut();
            return true;
        }
        if (id == R.id.menu_refresh) {
            if (refreshClickListener != null) {
                refreshClickListener.onRefreshClick();
            }
            return true;
        }

        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        FloatingActionButton fab = findViewById(R.id.fab);
        FriendsTab friendsTab = ((FriendsTab) mSectionsPagerAdapter.getItem(1));
        EventsTab eventsTab = ((EventsTab) mSectionsPagerAdapter.getItem(0));

        switch (position) {
            case 0:
                friendsTab.finishActionMode();
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.fab_post));
                fab.setOnClickListener(eventsTab);
                refreshClickListener = eventsTab;
                break;
            case 1:
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_friend));
                fab.setOnClickListener(friendsTab);
                refreshClickListener = friendsTab;
                break;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentNameList = new ArrayList<>();

        public void addFragment(Fragment fragment, String fragmentName) {
            fragmentList.add(fragment);
            fragmentNameList.add(fragmentName);
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentNameList.get(position);
        }
    }

    public void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        viewModel.signOut(this, mGoogleSignInClient);
    }
}
