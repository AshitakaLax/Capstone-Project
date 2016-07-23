package com.ashitakalax.scheduledtimelapse;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ashitakalax.scheduledtimelapse.data.ProjectContract;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by lballing on 7/22/2016.
 * This activity will handle the creation of a new Project
 */
public class NewProjectActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText titleEditText;
    private EditText frequencyEditText;
    private TextView startDateTextView;
    private TextView startTimeTextView;
    private TextView endDateTextView;
    private TextView endTimeTextView;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private Button saveProjectButton;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");//todo update to have second precision

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

        setContentView(R.layout.new_project_layout);

        //todo add support for saving the options in the toolbar instead of a button
        titleEditText = (EditText)findViewById(R.id.titleEditText);
        frequencyEditText = (EditText)findViewById(R.id.frequencyEditText);
        saveProjectButton = (Button)findViewById(R.id.addProjecButton);

        startDateTextView = (TextView)findViewById(R.id.startDateEditText);
        startTimeTextView = (TextView)findViewById(R.id.startTimeEditText);
        endDateTextView = (TextView) findViewById(R.id.endDateEditText);
        endTimeTextView = (TextView) findViewById(R.id.endTimeEditText);
        Calendar now = Calendar.getInstance();
        startDateTextView.setText(dateFormat.format(now.getTime()));
        startTimeTextView.setText(timeFormat.format(now.getTime()));
        endDateTextView.setText(dateFormat.format(now.getTime()));
        endTimeTextView.setText(timeFormat.format(now.getTime()));
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

        ContentValues newProjectValues = new ContentValues();

        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_TITLE, this.titleEditText.getText().toString());
        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_FREQUENCY, this.frequencyEditText.getText().toString());
        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_START_TIME, this.startCalendar.getTime().getTime());
        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_END_TIME, this.endCalendar.getTime().getTime());
        this.getContentResolver().insert(ProjectContract.ProjectEntry.CONTENT_URI, newProjectValues);

        //goto home project
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
    }
}
