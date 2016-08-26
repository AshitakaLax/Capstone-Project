package com.ashitakalax.scheduledtimelapse.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewParentCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ashitakalax.scheduledtimelapse.R;
import com.ashitakalax.scheduledtimelapse.alarm.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private OnProjectSelected mOnProjectSelected;
    private OnProjectActiveStateChanged mOnProjectActiveChanged;
    private ArrayList<Switch> mSwitchList;
    public interface OnProjectSelected {
        void onProjectSelected(long projectId);
    }
    public interface OnProjectActiveStateChanged {
        void onProjectActiveState(int projectId, boolean isActive);
    }

    public ProjectCursorAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
        this.mSwitchList = new ArrayList<>();
    }

    public void setOnProjectSelectListener(OnProjectSelected listener)
    {
        mOnProjectSelected = listener;
    }
    public void setmOnProjectActiveChanged(OnProjectActiveStateChanged listener)
    {
        this.mOnProjectActiveChanged = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.project_card, parent, false);
        int id = cursor.getInt(COL_PROJECT_ID);
        view.setId(id);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnProjectSelected.onProjectSelected(view.getId());
            }
        });
        Switch activeSwitch = (Switch) view.findViewById(R.id.projectActiveSwitch);
        activeSwitch.setTag(id);
        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int projectId = (int)compoundButton.getTag();
                mOnProjectActiveChanged.onProjectActiveState(projectId, b);
                if(b) {
                    compoundButton.setText(R.string.card_subtitle_active);
                }
                else {
                    compoundButton.setText(R.string.card_subtitle_inactive);
                }
            }
        });
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        CardView cardView = (CardView)view.findViewById(R.id.card_view);
        TextView titleTextView = (TextView)view.findViewById(R.id.title_text_view);
        TextView frequencyTextView = (TextView)view.findViewById(R.id.frequency_text_view);
        TextView startTimeTextView = (TextView)view.findViewById(R.id.start_time_text_view);
        TextView endTimeTextView = (TextView)view.findViewById(R.id.end_time_text_view);
        Switch activeSwitch = (Switch) view.findViewById(R.id.projectActiveSwitch);

        // todo add image to cardview(either default or one that is part of the set)
        titleTextView.setText(cursor.getString(COL_PROJECT_TITLE));
        String freqStr = "Frequency " + cursor.getFloat(COL_PROJECT_FREQUENCY);
        frequencyTextView.setText( freqStr);
        startTimeTextView.setText(dateTimeFormat.format(new Date(cursor.getLong(COL_PROJECT_START_TIME))));
        long endTime = cursor.getLong(COL_PROJECT_END_TIME);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(endTime);
        endTimeTextView.setText(dateTimeFormat.format(new Date(cursor.getLong(COL_PROJECT_END_TIME))));
        String activeStatus;

        Calendar now = Calendar.getInstance();
        if(endCalendar.getTimeInMillis() < now.getTimeInMillis())
        {
            cardView.setElevation(R.dimen.cardview_default_elevation);
            activeSwitch.setChecked(false);
            activeSwitch.setEnabled(false);
            activeStatus = context.getString(R.string.project_card_expired);
        }
        else if(cursor.getString(COL_PROJECT_ACTIVE).equals("1"))
        {
            activeSwitch.setChecked(true);
            cardView.setElevation(R.dimen.card_raised_elevation);

            activeStatus = context.getString(R.string.project_card_active);
        }
        else
        {
            cardView.setElevation(R.dimen.cardview_default_elevation);
            activeSwitch.setChecked(false);
            activeStatus = context.getString(R.string.project_card_inactive);
        }
        activeSwitch.setText(activeStatus);
    }
}
