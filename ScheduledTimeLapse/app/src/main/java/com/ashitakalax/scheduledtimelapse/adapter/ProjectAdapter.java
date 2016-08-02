package com.ashitakalax.scheduledtimelapse.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashitakalax.scheduledtimelapse.R;
import com.ashitakalax.scheduledtimelapse.data.ProjectContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lballing on 7/21/2016.
 * Recycler view adapter for Projects
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private ProjectAdapterOnClickHandler mClickHandler;
    // todo add the date and time formatters to a constants in utilities
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEE, MMM d, yyyy  hh:mm aaa");

    private static final String[] PROJECT_COLUMNS = {
        ProjectContract.ProjectEntry.TABLE_NAME + "." + ProjectContract.ProjectEntry._ID,
            ProjectContract.ProjectEntry.COLUMN_TITLE,
            ProjectContract.ProjectEntry.COLUMN_FREQUENCY,
            ProjectContract.ProjectEntry.COLUMN_START_TIME,
            ProjectContract.ProjectEntry.COLUMN_END_TIME
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_PROJECT_ID = 0;
    static final int COL_PROJECT_TITLE = 1;
    static final int COL_PROJECT_FREQUENCY = 2;
    static final int COL_PROJECT_START_TIME = 3;
    static final int COL_PROJECT_END_TIME = 4;

    @Override
    public ProjectAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if ( parent instanceof RecyclerView ) {
            int layoutId = -1;
            layoutId = R.layout.project_card;
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            view.setFocusable(true);
            return new ProjectAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ProjectAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        // todo add image to cardview(either default or one that is part of the set)
        holder.mTitleTextView.setText(mCursor.getString(COL_PROJECT_TITLE));
        holder.mFrequencyTextView.setText( "Frequency " + mCursor.getFloat(COL_PROJECT_FREQUENCY));
        holder.mStartTimeTextView.setText(dateTimeFormat.format(new Date(mCursor.getLong(COL_PROJECT_START_TIME))));
        holder.mEndTimeTextView.setText(dateTimeFormat.format(new Date(mCursor.getLong(COL_PROJECT_END_TIME))));
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public class ProjectAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTitleTextView;
        public final TextView mFrequencyTextView;
        public final TextView mStartTimeTextView;
        public final TextView mEndTimeTextView;
        public ProjectAdapterViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView)itemView.findViewById(R.id.title_text_view);
            mFrequencyTextView = (TextView)itemView.findViewById(R.id.frequency_text_view);
            mStartTimeTextView = (TextView)itemView.findViewById(R.id.start_time_text_view);
            mEndTimeTextView = (TextView)itemView.findViewById(R.id.end_time_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //check that all the input is valid

            // check that none of them are
            mClickHandler.onClick(this);
        }
    }


    public interface ProjectAdapterOnClickHandler {
        void onClick(ProjectAdapterViewHolder vh);
    }

    public ProjectAdapter(Context context, ProjectAdapterOnClickHandler dh) {
        mContext = context;
        mClickHandler = dh;
        this.mCursor = context.getContentResolver().query(ProjectContract.ProjectEntry.CONTENT_URI
                ,null
        ,null
        ,null
        ,null);
    }
}
