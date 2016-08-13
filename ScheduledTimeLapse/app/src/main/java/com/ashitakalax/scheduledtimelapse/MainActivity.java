package com.ashitakalax.scheduledtimelapse;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ashitakalax.scheduledtimelapse.adapter.ProjectAdapter;
import com.ashitakalax.scheduledtimelapse.adapter.ProjectCursorAdapter;
import com.ashitakalax.scheduledtimelapse.alarm.AlarmReceiver;
import com.ashitakalax.scheduledtimelapse.data.ProjectContract;
import com.google.firebase.analytics.FirebaseAnalytics;

// todo update this activity to be a fragment
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PROJECT_LOADER = 0;
    private static final String[] PROJECT_COLUMNS = {
            ProjectContract.ProjectEntry.TABLE_NAME + "." + ProjectContract.ProjectEntry._ID,
            ProjectContract.ProjectEntry.COLUMN_TITLE,
            ProjectContract.ProjectEntry.COLUMN_FREQUENCY,
            ProjectContract.ProjectEntry.COLUMN_START_TIME,
            ProjectContract.ProjectEntry.COLUMN_END_TIME,
            ProjectContract.ProjectEntry.COLUMN_ALARM_ACTIVE
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_PROJECT_ID = 0;
    static final int COL_PROJECT_TITLE = 1;
    static final int COL_PROJECT_FREQUENCY = 2;
    static final int COL_PROJECT_START_TIME = 3;
    static final int COL_PROJECT_END_TIME = 4;
    static final int COL_PROJECT_ACTIVE = 5;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProjectCursorAdapter mCursorAdapter;
    private ListView mListView;

    // Whether or not we are in dual-pane mode
    boolean mIsDualPane = false;
    private PendingIntent pendingIntent;
    AdSupport mAdSupport;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AlarmReceiver alarm = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        alarm.checkAlarms(this);
        // setup ads if free
        this.mAdSupport = new AdSupport();
        View adView = (View) findViewById(R.id.ad_view);
        if (adView != null) {
            this.mAdSupport.handleOnCreate(this, adView);
        }

        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // setup the recycler view
        if(false) {
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout Manager
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new ProjectAdapter(this, new ProjectAdapter.ProjectAdapterOnClickHandler() {
                @Override
                public void onClick(ProjectAdapter.ProjectAdapterViewHolder vh) {
                    //start the newProjectActivity
                    Intent intent = new Intent(getApplicationContext(), NewProjectActivity.class);
                    intent.putExtra(NewProjectActivity.PROJECT_POSITION, vh.mProjectId);
                    startActivity(intent);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            //listView approach
            mListView = (ListView)findViewById(R.id.my_list_view);
            //This is the cursor adapter implementation

            mCursorAdapter = new ProjectCursorAdapter(this, null, 0);
            //change the recycle view to be just a list view
            this.mListView.setAdapter(mCursorAdapter);
            this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    if (cursor != null) {

                        //Long movieId = (long) cursor.getInt(COL_MOVIE_ID);
//
//                        try{
//                            ((OnMovieSelected) mMainActivity).onMovieSelected(movieId);
//                        }catch (ClassCastException cce){
//
//                        }
                    }
                }
            });

            LoaderManager manager = this.getSupportLoaderManager();
            manager.initLoader(PROJECT_LOADER, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoaderManager manager = this.getSupportLoaderManager();
        manager.restartLoader(PROJECT_LOADER, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            // start the Settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id + "");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "option");

        if (id == R.id.nav_camera) {
            // Handle the camera action
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "CameraPreview");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            // start the CamerPreview activity
            Intent intent = new Intent(this, CameraPreviewActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_gallery) {

            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "gallery");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            ((TextView) this.findViewById(R.id.textView)).setText("TODO open gallery");

        } else if (id == R.id.nav_user_manual) {

            // start the User Manual activity
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "userManual");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            Intent intent = new Intent(this, UserManualActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        // goto new fragment to create a new project with all of it's settings

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, R.id.fab + "");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "fab");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "newProject");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Intent intent = new Intent(this, NewProjectActivity.class);
        intent.putExtra(NewProjectActivity.PROJECT_POSITION, -1);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Loader<Cursor> cursorLoader = new CursorLoader(this, ProjectContract.ProjectEntry.CONTENT_URI, null, null, null, ProjectContract.ProjectEntry.COLUMN_ALARM_ACTIVE + " DESC");
        return  cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.mCursorAdapter.swapCursor(null);

    }

}

