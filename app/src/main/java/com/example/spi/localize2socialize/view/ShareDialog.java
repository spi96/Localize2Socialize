package com.example.spi.localize2socialize.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.model.Account;
import com.example.spi.localize2socialize.model.Calendar;
import com.example.spi.localize2socialize.model.Event;
import com.example.spi.localize2socialize.net.RequestQueueSingleton;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShareDialog extends DialogFragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {
    private static final int CALENDAR_LOADER_ID = 1;
    private static final int EVENT_LOADER_ID = 2;
    private final String DATE_FORMAT = "yyyy-MM-dd";
    private final String REQUEST_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateListener;
    private SimpleDateFormat simpleDateFormat;

    private ArrayAdapter<Calendar> calendars;
    private Calendar selectedCalendar;
    private List<Event> events;
    private static List<Account> selectedFriends;
    private static Account account;

    private Spinner calendarSpinner;
    private EditText endOfSharingET;
    private TextInputLayout textInputLayout;
    private Button shareButton;
    private Button cancelButton;
    private ProgressBar progressBar;

    public static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
    };

    private static final int PROJECTION_TITLE_INDEX = 0;
    private static final int PROJECTION_DESCRIPTION_INDEX = 1;
    private static final int PROJECTION_EVENT_LOCATION_INDEX = 2;
    private static final int PROJECTION_DTSTART_INDEX = 3;
    private static final int PROJECTION_DTEND_INDEX = 4;

    public ShareDialog() {
    }

    public static ShareDialog newInstance(Account _account, List<Account> _selectedFriends) {
        ShareDialog fragment = new ShareDialog();
        account = _account;
        selectedFriends = _selectedFriends;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendars = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        calendars.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        events = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, getActivity().getResources().getConfiguration().getLocales().get(0));

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth, 23, 59, 59);
                updateView();
            }
        };
        calendar.add(android.icu.util.Calendar.DATE, 7);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sharing_fragment, container);
        calendarSpinner = view.findViewById(R.id.calendarSpinner);
        endOfSharingET = view.findViewById(R.id.EndOfSharingET);
        shareButton = view.findViewById(R.id.action_share_ok);
        cancelButton = view.findViewById(R.id.action_share_cancel);
        progressBar = view.findViewById(R.id.progressBar);
        textInputLayout = view.findViewById(R.id.EndOfSharingTIL);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        endOfSharingET.setShowSoftInputOnFocus(false);
        calendarSpinner.setAdapter(calendars);
        setActionListeners();
        getLoaderManager().initLoader(ShareDialog.CALENDAR_LOADER_ID, null, this);

        return view;
    }

    private void setActionListeners() {
        cancelButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        endOfSharingET.setOnClickListener(this);
        calendarSpinner.setOnItemSelectedListener(this);
    }

    private void onShareButtonClick() {
        if (!validateDialog()) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        disableButtons(false);

        getLoaderManager().restartLoader(ShareDialog.EVENT_LOADER_ID, null, this);
    }

    private boolean validateDialog() {
        try {
            if (endOfSharingET.getText().length() == 0) {
                textInputLayout.setError(getActivity().getString(R.string.required));
                return false;
            }
            if (simpleDateFormat.parse(endOfSharingET.getText().toString()).before(android.icu.util.Calendar.getInstance().getTime())) {
                textInputLayout.setError(getActivity().getString(R.string.end_of_sharing_expired));
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void onEndOfSharingClick() {
        int year = calendar.get(android.icu.util.Calendar.YEAR);
        int month = calendar.get(android.icu.util.Calendar.MONTH);
        int day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                dateListener,
                year, month, day);
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_share_ok:
                onShareButtonClick();
                break;
            case R.id.action_share_cancel:
                closePopup("", Activity.RESULT_CANCELED);
                break;
            case R.id.EndOfSharingET:
                onEndOfSharingClick();
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;

        switch (id) {
            case CALENDAR_LOADER_ID:
                baseUri = CalendarContract.Calendars.CONTENT_URI;
                String select = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))";
                String[] selectArgs = new String[]{account.getPersonEmail()};
                return new CursorLoader(getContext(), baseUri,
                        CALENDAR_PROJECTION, select, selectArgs,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " ASC");

            case EVENT_LOADER_ID:
                selectedCalendar = (Calendar) calendarSpinner.getSelectedItem();
                baseUri = CalendarContract.Events.CONTENT_URI;
                String selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?) AND (" +
                        CalendarContract.Events.EVENT_LOCATION + " != ?))";
                String[] selectionArgs = new String[]{String.valueOf(selectedCalendar.getCalId()), ""};

                return new CursorLoader(getContext(), baseUri, EVENT_PROJECTION, selection, selectionArgs, null);

            default:
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case CALENDAR_LOADER_ID:
                readCalendarResult(data);
                break;
            case EVENT_LOADER_ID:
                readEventResult(data);
                break;
        }
    }

    private void readCalendarResult(Cursor data) {
        while (data.moveToNext()) {
            long calID = data.getLong(PROJECTION_ID_INDEX);
            String displayName = data.getString(PROJECTION_DISPLAY_NAME_INDEX);

            calendars.add(new Calendar(calID, displayName));
        }
        if (calendars.getCount() == 0) {
            shareButton.setEnabled(false);
            calendars.add(new Calendar(-1L, getContext().getResources().getString(R.string.no_calendars_error)));
        }
    }

    private void readEventResult(Cursor data) {
        Date currentTime = java.util.Calendar.getInstance().getTime();
        Date endTime = calendar.getTime();
        data.moveToFirst();
        events.clear();
        while (data.moveToNext()) {
            String title = data.getString(PROJECTION_TITLE_INDEX);
            String description = data.getString(PROJECTION_DESCRIPTION_INDEX);
            String eventLocation = data.getString(PROJECTION_EVENT_LOCATION_INDEX);
            Date dtStart = new Date(data.getLong(PROJECTION_DTSTART_INDEX));
            Date dtEnd = new Date(data.getLong(PROJECTION_DTEND_INDEX));

            if (dtEnd.before(currentTime) || dtStart.after(endTime))
                continue;

            LatLng latLng = getLatLngFromStringAddress(eventLocation);
            if (latLng == null)
                continue;

            Event event = new Event(title, description, eventLocation,
                    latLng.latitude, latLng.longitude, dtStart, dtEnd);
            events.add(event);
        }
        sendFriendRequest();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateView() {
        endOfSharingET.setText(simpleDateFormat.format(calendar.getTime()));
        textInputLayout.setError(null);
    }

    private void setSpinnerError() {
        View selectedView = calendarSpinner.getSelectedView();
        if (selectedView instanceof TextView) {
            calendarSpinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError(getContext().getResources().getString(R.string.no_calendars_error));
            calendarSpinner.performClick();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (calendars.getItem(position).getCalId() == -1) {
            setSpinnerError();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private LatLng getLatLngFromStringAddress(String addressName) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addresses;
        LatLng result = null;

        try {
            addresses = geocoder.getFromLocationName(addressName, 5);

            if (addresses == null || addresses.size() == 0) {
                return null;
            }

            Address address = addresses.get(0);
            result = new LatLng(address.getLatitude(), address.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void sendFriendRequest() {
        if (checkIfEventsEmpty()) {
            return;
        }

        String url = getResources().
                getString(R.string.baseUrl) + getResources().getString(R.string.shareCalendar);
        selectedCalendar.setEvents(events);
        selectedCalendar.setStartOfSharing(android.icu.util.Calendar.getInstance().getTime());
        selectedCalendar.setEndOfSharing(calendar.getTime());
        selectedCalendar.setOwner(account);
        selectedCalendar.setParticipants(selectedFriends);
        JSONObject jsonObject = null;
        try {
            jsonObject = createRequest(selectedCalendar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendRequest(url, Request.Method.POST, jsonObject);
    }

    private JSONObject createRequest(Object request) throws JSONException {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat(REQUEST_DATE_FORMAT).create();
        return new JSONObject(gson.toJson(request));
    }

    private void sendRequest(String url, int method, JSONObject jsonRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonRequest,
                getResponseListener(), getResponseErrorListener());
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("successful")) {
                    try {
                        boolean successful = response.getBoolean("successful");

                        String message = successful ? getResources().getString(R.string.calendar_shared) :
                                getResources().getString(R.string.sharing_exists);
                        progressBar.setVisibility(View.GONE);
                        closePopup(message, Activity.RESULT_OK);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private Response.ErrorListener getResponseErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                disableButtons(true);
                Log.e("API CALL ERROR", error.toString());
            }
        };
    }

    private void disableButtons(boolean enabled) {
        cancelButton.setEnabled(enabled);
        shareButton.setEnabled(enabled);
    }

    private boolean checkIfEventsEmpty() {
        if (events.size() == 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    closePopup(getResources().getString(R.string.no_events), Activity.RESULT_OK);
                }
            });
            return true;
        }
        return false;
    }

    private void closePopup(String message, int resultCode) {
        Fragment target = getTargetFragment();
        if (target != null) {
            Intent intent = new Intent();
            intent.putExtra("result", message);
            target.onActivityResult(getTargetRequestCode(), resultCode, intent);
        }
        dismiss();
    }
}
