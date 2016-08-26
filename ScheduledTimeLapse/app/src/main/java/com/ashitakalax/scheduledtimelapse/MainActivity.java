package com.ashitakalax.scheduledtimelapse;

import android.app.ActivityOptions;
import android.content.ContentValues;
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
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.ashitakalax.scheduledtimelapse.adapter.ProjectCursorAdapter;
import com.ashitakalax.scheduledtimelapse.alarm.AlarmReceiver;
import com.ashitakalax.scheduledtimelapse.data.ProjectContract;
import com.google.firebase.analytics.FirebaseAnalytics;

// todo update this activity to be a fragment
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, ProjectCursorAdapter.OnProjectSelected, ProjectCursorAdapter.OnProjectActiveStateChanged {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PROJECT_LOADER = 0;

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_PROJECT_ID = 0;
    static final int COL_PROJECT_TITLE = 1;
    static final int COL_PROJECT_FREQUENCY = 2;
    static final int COL_PROJECT_START_TIME = 3;
    static final int COL_PROJECT_END_TIME = 4;
    static final int COL_PROJECT_ACTIVE = 5;

    private ProjectCursorAdapter mCursorAdapter;

    // Whether or not we are in dual-pane mode
    AdSupport mAdSupport;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AlarmReceiver alarm = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_main);
        Slide activitySlide = new Slide(Gravity.BOTTOM);
        activitySlide.setDuration(300);
        getWindow().setExitTransition(activitySlide);
        getWindow().setReenterTransition(activitySlide);
        getWindow().setReturnTransition(activitySlide);
        //setup exit transition
        Slide slide = new Slide(Gravity.BOTTOM);
        ListView mListView = (ListView) findViewById(R.id.my_list_view);
        ViewGroup rootView = mListView;
        slide.setDuration(300);
        TransitionManager.go(new Scene(rootView), slide);

        getWindow().setExitTransition(slide);

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

        //listView approach
        mListView = (ListView)findViewById(R.id.my_list_view);

        //This is the cursor adapter implementation
        mCursorAdapter = new ProjectCursorAdapter(this, null, 0);
        mCursorAdapter.setOnProjectSelectListener(this);
        mCursorAdapter.setmOnProjectActiveChanged(this);
        //change the recycle view to be just a list view
        mListView.setAdapter(mCursorAdapter);

        LoaderManager manager = this.getSupportLoaderManager();
        manager.initLoader(PROJECT_LOADER, null, this);

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
        }
//        else if (id == R.id.nav_gallery) {
//
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "gallery");
//            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//            ((TextView) this.findViewById(R.id.textView)).setText("TODO open gallery");
//
//        }
        else if (id == R.id.nav_user_manual) {

            // start the User Manual activity
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "userManual");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            Intent intent = new Intent(this, UserManualActivity.class);
            startActivity(intent);
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
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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

    @Override
    public void onProjectSelected(long projectId)
    {
        Intent intent = new Intent(getApplicationContext(), NewProjectActivity.class);
        Bundle args = new Bundle();
        args.putInt(NewProjectActivity.PROJECT_POSITION, (int)projectId);
        intent.putExtra(NewProjectActivity.PROJECT_POSITION, (int)projectId);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onProjectActiveState(int projectId, boolean isActive) {
        Cursor cursor =getContentResolver().query(
                ProjectContract.ProjectEntry.CONTENT_URI
                ,null
                ,ProjectContract.ProjectEntry._ID + " = ?"
                ,new String[] {String.valueOf(projectId)}, null);
        if(cursor == null)
        {
            return;
        }
        ContentValues updatedProjectValues = new ContentValues();
        try {
            cursor.moveToFirst();
            boolean projectActive = cursor.getString(COL_PROJECT_ACTIVE).equals("1");
            cursor.close();
            if (projectActive != isActive) {
                updatedProjectValues.put(ProjectContract.ProjectEntry.COLUMN_ALARM_ACTIVE, isActive);
                this.getContentResolver().update(ProjectContract.ProjectEntry.CONTENT_URI, updatedProjectValues, ProjectContract.ProjectEntry._ID + "=?", new String[]{String.valueOf(projectId)});
            }

        }
        catch (Exception e)
        {
            Log.e(TAG, "Cursor error" + e.getMessage());
        }
        finally {
            if(!cursor.isClosed()) {
                cursor.close();
            }
        }

        alarm.checkAlarms(this);
    }
}

