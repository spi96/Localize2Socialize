package com.example.spi.localize2socialize.layout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

public class ShareDialog extends DialogFragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CALENDAR_LOADER_ID = 1;
    private final String DATE_FORMAT = "yyyy-MM-dd";

    private android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateListener;
    private SimpleDateFormat simpleDateFormat;

    private List<Calendar> calendars;
    private ArrayAdapter<String> arrayAdapter;

    //private Spinner sharingTypeSpinner;
    private Spinner calendarSpinner;
    private EditText endOfSharingET;
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
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendars = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, getActivity().getResources().getConfiguration().getLocales().get(0));

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                updateView();
            }
        };
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

        calendarSpinner.setAdapter(arrayAdapter);
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
        if (endOfSharingET.getText().length() == 0) {
            endOfSharingET.setError(getActivity().getString(R.string.end_of_sharing_error));
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("Deadline", calendar.getTime());
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
    }

    private void onEndOfSharingClick() {
        if (endOfSharingET.getText().length() == 0) {
            calendar.add(android.icu.util.Calendar.DATE, 7);
        }
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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = CalendarContract.Calendars.CONTENT_URI;

        /*String select = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + Calendars.OWNER_ACCOUNT + " = ?))";*/

        return new CursorLoader(getContext(), baseUri,
                CALENDAR_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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

        arrayAdapter.addAll(calendars.stream().map(calendarDisplayNameProjection).collect(Collectors.<String>toList()));
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
        endOfSharingET.setError(null);
    }
}
