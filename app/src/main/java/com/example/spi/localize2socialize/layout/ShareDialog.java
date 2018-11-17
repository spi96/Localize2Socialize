package com.example.spi.localize2socialize.layout;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.spi.localize2socialize.R;
import com.example.spi.localize2socialize.models.Calendar;
import com.example.spi.localize2socialize.models.Event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class ShareDialog extends DialogFragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CALENDAR_LOADER_ID = 1;
    private static final int EVENT_LOADER_ID = 2;
    private final String DATE_FORMAT = "yyyy-MM-dd";

    private android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateListener;
    private SimpleDateFormat simpleDateFormat;

    //private List<Calendar> calendars;
    private ArrayAdapter<Calendar> calendars;
    private List<Event> events;

    //private Spinner sharingTypeSpinner;
    private Spinner calendarSpinner;
    private EditText endOfSharingET;
    private TextInputLayout textInputLayout;
    private Button shareButton;
    private Button cancelButton;

    public ShareDialog() {
    }

    public static ShareDialog newInstance() {
        ShareDialog fragment = new ShareDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendars = new ArrayAdapter<Calendar>(getContext(), android.R.layout.simple_spinner_item);
        calendars.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        events = new ArrayList<>();
        //calendars = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, getActivity().getResources().getConfiguration().getLocales().get(0));

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                updateView();
            }
        };
        calendar.add(android.icu.util.Calendar.DATE, 7);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sharing_fragment, container);
        //sharingTypeSpinner = (Spinner) view.findViewById(R.id.sharingTypeSpinner);
        calendarSpinner = (Spinner) view.findViewById(R.id.calendarSpinner);
        endOfSharingET = (EditText) view.findViewById(R.id.EndOfSharingET);
        shareButton = (Button) view.findViewById(R.id.action_share_ok);
        cancelButton = (Button) view.findViewById(R.id.action_share_cancel);
        textInputLayout = (TextInputLayout) view.findViewById(R.id.EndOfSharingTIL);

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
    }

    private void onShareButtonClick() {
        if (!validateDialog()) {
            return;
        }

        getLoaderManager().restartLoader(ShareDialog.EVENT_LOADER_ID, null, this);

        /*Intent intent = new Intent();
        intent.putExtra("Deadline", calendar.getTime());
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);*/
    }

    private boolean validateDialog() {
        try {
            if (endOfSharingET.getText().length() == 0) {
                textInputLayout.setError(getActivity().getString(R.string.end_of_sharing_empty));
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
                dismiss();
                break;
            case R.id.EndOfSharingET:
                onEndOfSharingClick();
                break;
        }
    }

    public static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.EVENT_END_TIMEZONE,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events._ID,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.RDATE
    };

    private static final int PROJECTION_CALENDAR_ID_INDEX = 0;
    private static final int PROJECTION_TITLE_INDEX = 1;
    private static final int PROJECTION_DESCRIPTION_INDEX = 2;
    private static final int PROJECTION_EVENT_LOCATION_INDEX = 3;
    private static final int PROJECTION_DTSTART_INDEX = 4;
    private static final int PROJECTION_DTEND_INDEX = 5;
    private static final int PROJECTION_EVENT_TIMEZONE_INDEX = 6;
    private static final int PROJECTION_EVENT_END_TIMEZONE_INDEX = 7;
    private static final int PROJECTION_ALL_DAY_INDEX = 8;
    private static final int PROJECTION_EVENT_ID_INDEX = 9;
    private static final int PROJECTION_RRULE_INDEX = 10;
    private static final int PROJECTION_RDATE_INDEX = 11;


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;

        switch (id) {
            case CALENDAR_LOADER_ID:
                baseUri = CalendarContract.Calendars.CONTENT_URI;

                /*String select = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                        + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                        + Calendars.OWNER_ACCOUNT + " = ?))";*/

                return new CursorLoader(getContext(), baseUri,
                        CALENDAR_PROJECTION, null, null, null);

            case EVENT_LOADER_ID:
                baseUri = CalendarContract.Events.CONTENT_URI;
                String selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?))";
                String[] selectionArgs = new String[]{String.valueOf(((Calendar) calendarSpinner.getSelectedItem()).getId())};
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
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            calID = data.getLong(PROJECTION_ID_INDEX);
            displayName = data.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = data.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = data.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            calendars.add(new Calendar(calID, accountName, displayName, ownerName));
        }
        //arrayAdapter.addAll(calendars.stream().map(calendarDisplayNameProjection).collect(Collectors.<String>toList()));
    }

    private void readEventResult(Cursor data) {
        data.moveToFirst();
        while (data.moveToNext()) {
            long calID = 0;
            long eventID = 0;
            String title = null;
            String description = null;
            String eventLocation = null;
            Date dtStart = null;
            Date dtEnd = null;
            String eventTimeZone = null;
            String eventEndTimeZone = null;
            boolean allDay = false;
            String rRule = null;
            String rDate = null;

            calID = data.getLong(PROJECTION_CALENDAR_ID_INDEX);
            eventID = data.getLong(PROJECTION_EVENT_ID_INDEX);
            title = data.getString(PROJECTION_TITLE_INDEX);
            description = data.getString(PROJECTION_DESCRIPTION_INDEX);
            eventLocation = data.getString(PROJECTION_EVENT_LOCATION_INDEX);
            dtStart = new Date(data.getLong(PROJECTION_DTSTART_INDEX));
            dtEnd = new Date(data.getLong(PROJECTION_DTEND_INDEX));
            eventTimeZone = data.getString(PROJECTION_EVENT_TIMEZONE_INDEX);
            eventEndTimeZone = data.getString(PROJECTION_EVENT_END_TIMEZONE_INDEX);
            allDay = data.getInt(PROJECTION_ALL_DAY_INDEX) == 0 ? false : true;
            rRule = data.getString(PROJECTION_RRULE_INDEX);
            rDate = data.getString(PROJECTION_RDATE_INDEX);

            //TODO mapping
            //events.add();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    Function<Calendar, String> calendarDisplayNameProjection = new Function<Calendar, String>() {
        @Override
        public String apply(Calendar s) {
            return s.getDisplayName();
        }
    };

    private void updateView() {
        endOfSharingET.setText(simpleDateFormat.format(calendar.getTime()));
        textInputLayout.setError(null);
    }
}
