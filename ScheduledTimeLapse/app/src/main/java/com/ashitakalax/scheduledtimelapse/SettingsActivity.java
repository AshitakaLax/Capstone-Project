package com.ashitakalax.scheduledtimelapse;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
public class SettingsActivity extends AppCompatActivity{

    private Spinner isoSpinner;
    private Spinner shutterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_project_layout);

        isoSpinner = (Spinner)findViewById(R.id.IsoSpinner);
        shutterSpinner = (Spinner)findViewById(R.id.ShutterSpinner);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }
}
