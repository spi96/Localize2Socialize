package com.example.spi.localize2socialize.view;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.model.Calendar;
import com.example.spi.localize2socialize.model.Event;
import com.example.spi.localize2socialize.model.Post;
import com.example.spi.localize2socialize.viewmodel.EventsTabViewModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EventsTab extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, TextWatcher,
        GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener, RefreshClickListener {
    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static final int LOCATION_SERVICE_ENABLE_ACTION = 2;
    static final int READ_REQUEST_CODE = 3;
    static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 4;

    private static final String TAG = "EventsTab";
    private static final String KEY_MYLASTLOCATION = "location";

    private EditText postEditText;
    private LinearLayout linearLayout;
    private CardView postCardView;
    private ImageButton attachImageButton;
    private CardView eventDetailsCV;
    private TextView eventTitleTV;
    private TextView eventDescriptionTV;
    private TextView eventDateStartTV;
    private TextView eventDateEndTV;
    private TextView eventLocationTV;
    private ImageView eventImage;

    private EventsTabViewModel mViewModel;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;


    public static EventsTab newInstance() {
        return new EventsTab();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mViewModel.setmLastKnownLocation((Location) savedInstanceState.getParcelable(KEY_MYLASTLOCATION));
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_tab_fragment, container, false);
        postEditText = view.findViewById(R.id.postEditText);
        attachImageButton = view.findViewById(R.id.attachImage);
        linearLayout = view.findViewById(R.id.postLayout);
        postCardView = view.findViewById(R.id.postCardView);
        eventDetailsCV = view.findViewById(R.id.eventDetailsCV);
        eventTitleTV = view.findViewById(R.id.eventTitleTV);
        eventDescriptionTV = view.findViewById(R.id.eventDescriptionTV);
        eventDateStartTV = view.findViewById(R.id.eventStartDate);
        eventDateEndTV = view.findViewById(R.id.eventEndDate);
        eventLocationTV = view.findViewById(R.id.eventLocation);
        eventImage = view.findViewById(R.id.eventImageIV);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Observer<List<Calendar>> calendarObserver = new Observer<List<Calendar>>() {
            @Override
            public void onChanged(@Nullable List<Calendar> calendars) {
                if (!calendars.isEmpty())
                    showEventsOnMap(calendars);
            }
        };


        final Observer<List<Post>> postObserver = new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                if (!posts.isEmpty())
                    showPostsOnMap(posts);
            }
        };

        mViewModel = ViewModelProviders.of(this).get(EventsTabViewModel.class);
        mViewModel.getCalendarLiveData().observe(this, calendarObserver);
        mViewModel.getPostLiveData().observe(this, postObserver);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            showAutoCompleteSearch();
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_MYLASTLOCATION, mViewModel.getmLastKnownLocation());
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mViewModel.loadSharings();

        setMapUI();
        setActionListeners(googleMap);
        getLocationPermission();
    }

    private void setMapUI() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    private void setActionListeners(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Marker postMarker = mViewModel.getPostMarker();
                if (postMarker != null) {
                    postMarker.setPosition(marker.getPosition());
                }
            }
        });

        attachImageButton.setOnClickListener(this);
        postEditText.addTextChangedListener(this);
    }

    private void showPostsOnMap(List<Post> posts) {
        for (Post post : posts) {
            Marker marker = mMap.addMarker(createMarkerOption(post));
            marker.setTag(post);
        }
    }

    private void showEventsOnMap(List<Calendar> calendars) {
        for (Calendar calendar : calendars) {
            for (Event event : calendar.getEvents()) {
                Marker marker = mMap.addMarker(createMarkerOption(event));
                marker.setTag(event);
            }
        }
    }

    private MarkerOptions createMarkerOption(Object item) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .draggable(false);//TODO lecserélni képre a hívó helyen
        Double latitude = null;
        Double longitude = null;
        if (item instanceof Post) {
            latitude = ((Post) item).getLocationLatitude();
            longitude = ((Post) item).getLocationLongitude();
        } else if (item instanceof Event) {
            latitude = ((Event) item).getLocationLatitude();
            longitude = ((Event) item).getLocationLongitude();
        }
        if (latitude == null || longitude == null)
            throw new NullPointerException("Location can not be null!");
        markerOptions.position(new LatLng(latitude.doubleValue(), longitude.doubleValue()));
        return markerOptions;
    }

    public void postLocation(LatLng latLng) {
        if (eventDetailsCV.getVisibility() == View.VISIBLE) {
            fillEventDescriptionView("", "", null, null, "");
            eventDetailsCV.setVisibility(View.GONE);
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .draggable(true);

        if (mViewModel.getPostMarker() != null) {
            mViewModel.getPostMarker().remove();
        }
        mViewModel.setPostMarker(mMap.addMarker(markerOptions));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        postCardView.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int paddingTop = ((ViewGroup.MarginLayoutParams) postCardView.getLayoutParams()).topMargin + postCardView.getHeight();
                mMap.setPadding(0, paddingTop, 0, 0);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(mViewModel.getPostMarker())) {
            return true;
        }
        eventDetailsCV.setVisibility(View.VISIBLE);
        if (marker.getTag() instanceof Event) {
            Event event = (Event) marker.getTag();
            fillEventDescriptionView(event.getTitle(), event.getDescription(), event.getStartDate(),
                    event.getEndDate(), event.getLocationName());
        } else if (marker.getTag() instanceof Post) {
            Post post = (Post) marker.getTag();
            fillEventDescriptionView(post.getDescription(), "", post.getStartDate(),
                    null, post.getLocationName());
            if (post.getEncodedAttachedImage() != null && !post.getEncodedAttachedImage().equals("")) {
                Bitmap bitmap = scaleImageToFitView(mViewModel.decodePhoto(post.getEncodedAttachedImage()));
                eventImage.setImageBitmap(bitmap);
            }
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        return true;
    }

    private Bitmap scaleImageToFitView(Bitmap bitmap) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Bitmap scaledBitmap = null;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            scaledBitmap = scaleToFitWidth(bitmap, displayMetrics.widthPixels / 2);
        } else {
            scaledBitmap = scaleToFitHeight(bitmap, displayMetrics.heightPixels / 3);
        }
        return scaledBitmap;
    }

    private void fillEventDescriptionView(String title, String description, Date dateStart, Date dateEnd, String locationName) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String start = dateStart == null ? "" : simpleDate.format(dateStart);
        String end = dateEnd == null ? "" : simpleDate.format(dateEnd);

        eventTitleTV.setText(title);
        eventDescriptionTV.setText(description);
        eventDateStartTV.setText(start);
        eventDateEndTV.setText(end);
        eventLocationTV.setText(locationName);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        resetTab();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
        }
    }

    private void updateUI() {
        try {
            if (mMap != null) {
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setMyLocationEnabled(true);
            }
        } catch (SecurityException e) {
            Log.e("EventsTab exception: %s", e.getMessage());
        }
    }

    private void getCurrentLocation() {
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        try {
                            mViewModel.setmLastKnownLocation(task.getResult());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mViewModel.getmLastKnownLocation().getLatitude(),
                                            mViewModel.getmLastKnownLocation().getLongitude()), 15));
                            updateUI();
                        } catch (NullPointerException e) {
                            Log.e("EventsTab exception: %s", e.getMessage());
                            statusCheck(true);
                            updateUI();
                        }
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e("EventsTab exception: %s", e.getMessage());
        }
    }

    public void statusCheck(boolean showAlertDialog) {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (showAlertDialog) {
                buildAlertMessageNoGps();
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.gps_not_enabled)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_SERVICE_ENABLE_ACTION);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case LOCATION_SERVICE_ENABLE_ACTION:
                getCurrentLocation();
                break;
            case READ_REQUEST_CODE:
                if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
                    try {
                        imageSearchResultProcess(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(getContext(), data);
                    mViewModel.setPostLocationName(place.getAddress().toString());
                    postLocation(place.getLatLng());
                    getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getContext(), data);
                    Log.i(TAG, status.getStatusMessage());
                }
                break;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getLocationPermission();
        return true;
    }

    public void resetTab() {
        if (postCardView.getVisibility() == View.VISIBLE || mViewModel.getPostMarker() != null) {
            if (mViewModel.getPostMarker() != null) {
                mViewModel.getPostMarker().remove();
                mViewModel.setPostMarker(null);
                mViewModel.setPostAttachedImage(null);
                mViewModel.setPostDescription(null);
                hideKeyboard(getView().getRootView());
                postEditText.setText("");
            }

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mMap.setPadding(0, 0, 0, 0);
                }
            });

            postCardView.setVisibility(View.GONE);
        }

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        if (fab.getVisibility() == View.VISIBLE) {
            fab.setVisibility(View.GONE);
        }

        if (eventDetailsCV.getVisibility() == View.VISIBLE) {
            fillEventDescriptionView("", "", null, null, "");
            eventDetailsCV.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        postEditText.clearFocus();
    }

    public void performFileSearch() {
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), READ_REQUEST_CODE);
    }

    private void imageSearchResultProcess(Intent resultData) throws IOException {
        Uri uri = null;
        if (resultData != null) {
            uri = resultData.getData();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            mViewModel.setPostAttachedImage(bitmap);
            showSnackbar(getResources().getString(R.string.img_attached));
        }

    }

    private Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    private Bitmap scaleToFitHeight(Bitmap b, int height) {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attachImage:
                performFileSearch();
                break;
            case R.id.fab:
                savePost();
                break;
        }
    }

    @Override
    public void onRefreshClick() {
        resetTab();
        mMap.clear();
        mViewModel.loadSharings();
    }

    private void showAutoCompleteSearch() {
        try {
            AutocompleteFilter typeFiler = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .setCountry(getContext().getResources().getConfiguration().getLocales().get(0).getCountry())
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFiler)
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void savePost() {
        if (postEditText.getText() == null || postEditText.getText().toString().equals("")) {
            postEditText.requestFocus();
            postEditText.setError(getResources().getString(R.string.required));
            return;
        }
        mViewModel.savePost(postEditText.getText().toString());
        resetTab();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (postEditText.getError() != null && postEditText.getError().length() > 0) {
            postEditText.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void showSnackbar(String message) {
        Snackbar.make(getActivity().findViewById(R.id.main_content), message, Snackbar.LENGTH_LONG).show();
    }
}
