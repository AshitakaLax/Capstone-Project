package com.ashitakalax.scheduledtimelapse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ashitakalax.scheduledtimelapse.controller.CameraController;

import java.util.List;

/**
 * Created by lballing on 7/22/2016.
 * This activity will handle the creation of a new Project
 */
public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static SparseArray<String> spinnerArray = new SparseArray<String>(){
        {
            append(R.id.iso_spinner, CameraController.ISO_GET_KEY);
            append(R.id.pic_size_spinner, CameraController.PIC_SIZE_GET_KEY);
            append(R.id.flash_spinner, CameraController.FLASH_GET_KEY);
            append(R.id.focus_spinner, CameraController.FOCUS_GET_KEY);
            append(R.id.format_spinner, CameraController.FORMAT_GET_KEY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        for (int i = 0; i < spinnerArray.size(); i++) {
            Spinner spinner = (Spinner)findViewById(spinnerArray.keyAt(i));
            String value = spinnerArray.valueAt(i);
            List<String> options = CameraController.getCameraOptions(value);
            if(options.size() != 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                String currentOption = CameraController.getCameraOption(value.substring(0, value.lastIndexOf('-')));
                int currentIndex = adapter.getPosition(currentOption);
                spinner.setSelection(currentIndex);
                spinner.setOnItemSelectedListener(this);
            }
            else
            {
                spinner.setVisibility(View.GONE);
                ViewGroup container = (ViewGroup)findViewById(R.id.spinner_group);
                View previousView = container.getChildAt(container.indexOfChild(spinner)-1);
                previousView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String item = adapterView.getItemAtPosition(position).toString();
        String optionType = spinnerArray.get(view.getId());
        optionType = optionType.substring(0, optionType.lastIndexOf('-'));//removes -values
        CameraController.setCameraOption(optionType, item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //nothing to do
    }
}
