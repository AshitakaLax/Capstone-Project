package com.ashitakalax.scheduledtimelapse;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ashitakalax.scheduledtimelapse.data.ProjectContract;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lballing on 7/22/2016.
 * This activity will handle the creation of a new Project
 */
public class NewProjectActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PROJECT_POSITION = "PROJECT_POSITION";
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

    private EditText titleEditText;
    private EditText frequencyEditText;
    private TextView startDateTextView;
    private TextView startTimeTextView;
    private TextView endDateTextView;
    private TextView endTimeTextView;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private Button saveProjectButton;
    private Switch mActiveSwitch;

    private int mProjectPosition;
    private int mProjectId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa", Locale.US);//todo update to have second precision

    //todo abstract each of these items into it's own class with formatting integrated into it.
    private DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, monthOfYear);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            startDateTextView.setText(dateFormat.format(startCalendar.getTime()));
        }
    };

    private TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startCalendar.set(Calendar.MINUTE, minute);
            startTimeTextView.setText(timeFormat.format(startCalendar.getTime()));
        }
    };

    private DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, monthOfYear);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            endDateTextView.setText(dateFormat.format(endCalendar.getTime()));
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endCalendar.set(Calendar.MINUTE, minute);

            endTimeTextView.setText(timeFormat.format(endCalendar.getTime()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.new_project_layout);
        Fade fade = new Fade(Fade.IN);
        Fade fadeExit = new Fade(Fade.OUT);
        fade.setDuration(300);
        fadeExit.setDuration(300);
        getWindow().setReenterTransition(fade);
        getWindow().setReturnTransition(fadeExit);
        getWindow().setEnterTransition(fade);

        ViewGroup rootView = (ViewGroup)findViewById(R.id.new_project_container);
        TransitionManager.go(new Scene(rootView), fade);
        if(extras != null)
        {
            mProjectPosition= extras.getInt(PROJECT_POSITION, -1);
        }
        else
        {
            mProjectPosition= -1;
        }

        //todo add support for saving the options in the toolbar instead of a button
        titleEditText = (EditText)findViewById(R.id.titleEditText);
        frequencyEditText = (EditText)findViewById(R.id.frequencyEditText);
        saveProjectButton = (Button)findViewById(R.id.addProjectButton);

        startDateTextView = (TextView)findViewById(R.id.startDateEditText);
        startTimeTextView = (TextView)findViewById(R.id.startTimeEditText);
        endDateTextView = (TextView) findViewById(R.id.endDateEditText);
        endTimeTextView = (TextView) findViewById(R.id.endTimeEditText);
        mActiveSwitch = (Switch) findViewById(R.id.projectActiveSwitch);

        if(mProjectPosition > -1)
        {
            //get the cursor with this position
            Cursor cursor =getContentResolver().query(
                    ProjectContract.ProjectEntry.CONTENT_URI
                    ,null
                    ,ProjectContract.ProjectEntry._ID + " = ?"
                    ,new String[] {String.valueOf(mProjectPosition)}, null);

            try {
                cursor.moveToFirst();

                String titleStr = cursor.getString(COL_PROJECT_TITLE);
                Float frequency = cursor.getFloat(COL_PROJECT_FREQUENCY);
                long startRaw = cursor.getLong(COL_PROJECT_START_TIME);
                long endRaw = cursor.getLong(COL_PROJECT_END_TIME);
                mProjectId = cursor.getInt(COL_PROJECT_ID);
                boolean projectActive = cursor.getString(COL_PROJECT_ACTIVE).equals("1");
                titleEditText.setText(titleStr);
                frequencyEditText.setText(NumberFormat.getNumberInstance().format(frequency));
                Calendar temp = Calendar.getInstance();
                temp.setTimeInMillis(startRaw);
                startCalendar.setTimeInMillis(startRaw);

                startDateTextView.setText(dateFormat.format(temp.getTime()));
                startTimeTextView.setText(timeFormat.format(temp.getTime()));
                temp.setTimeInMillis(endRaw);
                endCalendar.setTimeInMillis(endRaw);
                endDateTextView.setText(dateFormat.format(temp.getTime()));
                endTimeTextView.setText(timeFormat.format(temp.getTime()));
                saveProjectButton.setText(R.string.project_update_button_text);
                mActiveSwitch.setChecked(projectActive);
                cursor.close();
            }
            catch(NullPointerException exception)
            {
                Log.d("Scheduled timelapse", exception.getMessage());
                mProjectPosition = -1;
            }
        }

        if(mProjectPosition == -1) {
            Calendar now = Calendar.getInstance();
            startDateTextView.setText(dateFormat.format(now.getTime()));
            startTimeTextView.setText(timeFormat.format(now.getTime()));
            endDateTextView.setText(dateFormat.format(now.getTime()));
            endTimeTextView.setText(timeFormat.format(now.getTime()));
        }
        saveProjectButton.setOnClickListener(this);
        startTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTimePickerFragment(startTimeSetListener);
            }
        });

        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatePickerFragment(startDateSetListener);
            }
        });

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatePickerFragment(endDateSetListener);
            }
        });

        endTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTimePickerFragment(endTimeSetListener);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    private void getDatePickerFragment(DatePickerDialog.OnDateSetListener dateSetListener)
    {
        DatePickerDialog datePickerDialog;
        Calendar now = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(dateSetListener,now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    private void getTimePickerFragment(TimePickerDialog.OnTimeSetListener timeSetListener)
    {
        TimePickerDialog timePickerDialog;
        Calendar now = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance(timeSetListener, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), false);
        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
    }

    /**
     * Handles adding a new project to the system
     * @param view
     */
    @Override
    public void onClick(View view) {
        // add new project
        //check that the values are valid
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());

        if(this.startCalendar.getTimeInMillis() > this.endCalendar.getTimeInMillis())
        {
            Toast.makeText(this, R.string.invalid_time_prompt, Toast.LENGTH_LONG).show();
            return;
        }
        //this just means that it will start when active
        ContentValues newProjectValues = new ContentValues();

        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_TITLE, this.titleEditText.getText().toString());
        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_FREQUENCY, Float.parseFloat(this.frequencyEditText.getText().toString()));
        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_START_TIME, this.startCalendar.getTime().getTime());
        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_END_TIME, this.endCalendar.getTime().getTime());
        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_ALARM_ACTIVE, this.mActiveSwitch.isChecked());
        if(this.mProjectPosition > -1)
        {
            this.getContentResolver().update(ProjectContract.ProjectEntry.CONTENT_URI, newProjectValues, ProjectContract.ProjectEntry._ID + "=?",new String[] {String.valueOf(mProjectId)});
        }
        else {
            this.getContentResolver().insert(ProjectContract.ProjectEntry.CONTENT_URI, newProjectValues);
        }
        //Check if the project is active, if so then deactivate others

        //goto home project
        Intent homeIntent = new Intent(this, MainActivity.class);

        startActivity(homeIntent);
    }
}
