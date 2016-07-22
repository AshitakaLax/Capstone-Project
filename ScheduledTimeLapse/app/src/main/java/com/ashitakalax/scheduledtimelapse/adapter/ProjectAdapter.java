package com.ashitakalax.scheduledtimelapse.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashitakalax.scheduledtimelapse.R;

/**
 * Created by lballing on 7/21/2016.
 * Recycler view adapter for Projects
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private ProjectAdapterOnClickHandler mClickHandler;
//    final private View mEmptyView;

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

//        int weatherId = mCursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
//        int defaultImage;
//        boolean useLongToday;

    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public class ProjectAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTitleTextView;
        public ProjectAdapterViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView)itemView.findViewById(R.id.title_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(this);
        }
    }

    public interface ProjectAdapterOnClickHandler {
        void onClick(ProjectAdapterViewHolder vh);
    }

    public ProjectAdapter(Context context, ProjectAdapterOnClickHandler dh) {
        mContext = context;
        mClickHandler = dh;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        //mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

}
