package com.ashitakalax.scheduledtimelapse.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ashitakalax.scheduledtimelapse.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lballing on 8/12/2016.
 */
public class ProjectCursorAdapter extends CursorAdapter {
    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_PROJECT_ID = 0;
    static final int COL_PROJECT_TITLE = 1;
    static final int COL_PROJECT_FREQUENCY = 2;
    static final int COL_PROJECT_START_TIME = 3;
    static final int COL_PROJECT_END_TIME = 4;
    static final int COL_PROJECT_ACTIVE = 5;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEE, MMM d, yyyy  hh:mm aaa", Locale.US);


    public ProjectCursorAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.project_card, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        CardView cardView = (CardView)view.findViewById(R.id.card_view);
        TextView titleTextView = (TextView)view.findViewById(R.id.title_text_view);
        TextView frequencyTextView = (TextView)view.findViewById(R.id.frequency_text_view);
        TextView startTimeTextView = (TextView)view.findViewById(R.id.start_time_text_view);
        TextView endTimeTextView = (TextView)view.findViewById(R.id.end_time_text_view);
//        TextView activeTextView = (TextView) view.findViewById(R.id.active_text_view);
        Switch activeSwitch = (Switch) view.findViewById(R.id.projectActiveSwitch);
        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    compoundButton.setText(R.string.card_subtitle_active);
                }
                else {
                    compoundButton.setText(R.string.card_subtitle_inactive);
                }
            }
        });
        // todo add image to cardview(either default or one that is part of the set)
        //holder.mProjectId = mCursor.getInt(COL_PROJECT_ID);
        titleTextView.setText(mCursor.getString(COL_PROJECT_TITLE));
        frequencyTextView.setText( "Frequency " + mCursor.getFloat(COL_PROJECT_FREQUENCY));
        startTimeTextView.setText(dateTimeFormat.format(new Date(mCursor.getLong(COL_PROJECT_START_TIME))));
        endTimeTextView.setText(dateTimeFormat.format(new Date(mCursor.getLong(COL_PROJECT_END_TIME))));
        String activeStatus;
        if(mCursor.getString(COL_PROJECT_ACTIVE).equals("1"))
        {
            activeSwitch.setChecked(true);
            cardView.setElevation(R.dimen.card_raised_elevation);

            activeStatus = "Active";
        }
        else
        {
            cardView.setElevation(R.dimen.cardview_default_elevation);
            activeSwitch.setChecked(false);
            activeStatus = "Inactive";
        }
        activeSwitch.setText(activeStatus);
//        activeTextView.setText(activeStatus);

    }
}
