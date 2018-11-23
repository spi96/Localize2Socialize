package com.example.spi.localize2socialize.view;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.viewmodel.EventsTabViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
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

public class EventsTab extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener, RefreshClickListener {
    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static final int LOCATION_SERVICE_ENABLE_ACTION = 2;
    static final int READ_REQUEST_CODE = 3;

    private static final String TAG = "EventsTab";
    private static final String KEY_MYLASTLOCATION = "location";

    private EditText postEditText;
    private LinearLayout linearLayout;
    private CardView postCardView;
    private ImageButton attachImageButton;

    private EventsTabViewModel mViewModel;

    private GoogleMap mMap;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private FusedLocationProviderClient fusedLocationProviderClient;


    public static EventsTab newInstance() {
        return new EventsTab();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mViewModel.setmLastKnownLocation((Location) savedInstanceState.getParcelable(KEY_MYLASTLOCATION));
        }

        geoDataClient = Places.getGeoDataClient(getContext());
        placeDetectionClient = Places.getPlaceDetectionClient(getContext());
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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EventsTabViewModel.class);
        // TODO: Use the ViewModel

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
        googleMap.setOnMapLongClickListener(this);
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
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .draggable(true);

        if (mViewModel.getPostMarker() != null) {
            mViewModel.getPostMarker().remove();
        }
        mViewModel.setPostMarker(mMap.addMarker(markerOptions));

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
        return false;
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
                if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                    imageSearchResultProcess(data);
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
            }

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mMap.setPadding(0, 0, 0, 0);
                }
            });

            postCardView.setVisibility(View.GONE);
        }
    }

    public void performFileSearch() {
        Intent intent = new Intent(/*Intent.ACTION_OPEN_DOCUMENT*/);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        //startActivityForResult(intent, READ_REQUEST_CODE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), READ_REQUEST_CODE);
    }

    private void imageSearchResultProcess(Intent resultData) {
        Uri uri = null;
        if (resultData != null) {
            uri = resultData.getData();
            //TODO image save
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attachImage:
                performFileSearch();
                break;
        }
    }

    @Override
    public void onRefreshClick() {
        //TOD
    }
}
