package com.ashitakalax.scheduledtimelapse.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import com.ashitakalax.scheduledtimelapse.data.ProjectContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by lballing on 7/30/2016.
 * this class will handle the broadcast of the alarm manager
 */
public class AlarmReceiver extends BroadcastReceiver{

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

    static final String BUNDLE_PROJECT_TITLE = "project_title";


    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Put here YOUR code.
        Toast.makeText(context, " Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example

        // update the next time this specific project will go off next
        checkAlarms(context);

        wl.release();
    }

    /**
     * Checks that alarms are setup to run at the correct time to take a picture
     * NOTE: This function should be optimized to save on battery
     * @param context
     */
    public void checkAlarms(Context context)
    {
        String sortOrder = ProjectContract.ProjectEntry.COLUMN_START_TIME + "ASC";
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("alarm.START_ALARM");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.cancel(pi);
        Cursor cursor = context.getContentResolver().query(ProjectContract.ProjectEntry.CONTENT_URI
        ,null
        ,null
        ,null
        , ProjectContract.ProjectEntry.COLUMN_START_TIME + " ASC");

        if(cursor == null)
        {
            return;
        }

        // for this I need to know the next alarm to set
        // we need to calculate what the time will be
        ArrayList<Calendar> Times = new ArrayList<>();
        ArrayList<Integer> increments = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        try {
            while (cursor.moveToNext()) {

                boolean isActive = cursor.getString(COL_PROJECT_ACTIVE).equals("1");
                if(!isActive)
                {
                    // remove from the alarm manager
                    continue;
                }
                int projectId = cursor.getInt(COL_PROJECT_ID);
                long startTime = cursor.getLong(COL_PROJECT_START_TIME);
                long endTime = cursor.getLong(COL_PROJECT_END_TIME);
                float frequency = cursor.getFloat(COL_PROJECT_FREQUENCY);
                //check if the endTime has already past
                Calendar endCalendarTime = Calendar.getInstance();
                endCalendarTime.setTimeInMillis(endTime);
                // Expired time
                if(endCalendarTime.getTimeInMillis() < now.getTimeInMillis())
                {
                    // remove alarm, and set alarm to not active, then requery
                    deactivateAlarm(context, projectId);
                    continue;
                }
                // go to the next alarm time
                Calendar startCalendarTime = Calendar.getInstance();
                startCalendarTime.setTimeInMillis(startTime);
                //convert the frequency into milliseconds
                double period = (1/frequency)*1000;

                int frequencyIncrement = (int)(period);//time in milliseconds
                while(startCalendarTime.getTimeInMillis() < now.getTimeInMillis())
                {
                    startCalendarTime.add(Calendar.MILLISECOND, frequencyIncrement);
                }

                if(startCalendarTime.getTimeInMillis() > endCalendarTime.getTimeInMillis())
                {
                    continue;
                }
                i.putExtra(BUNDLE_PROJECT_TITLE, cursor.getString(COL_PROJECT_TITLE));
                pi = PendingIntent.getBroadcast(context, 0, i, 0);
                //time is valid to set
                am.setRepeating(AlarmManager.RTC_WAKEUP, startCalendarTime.getTimeInMillis(), frequencyIncrement, pi); // Millisec * Second * Minute
            }
        } finally {
            cursor.close();
        }
    }

    public Calendar getNextAlarm(long startTime, float frequency)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        double period = (1/frequency);
        //the period is how far apart each picture will be taken, should be 5+ seconds
        period *= 1000; // convert the period into milliseconds for precision here
        calendar.add(Calendar.MILLISECOND, (int)period);
        return calendar;//currently this isn't correct. this could be in the past
    }

    public void setAlarm(Context context)
    {
        checkAlarms(context);
        return;
//        //get the next alarm that we need
//        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent("alarm.START_ALARM");
////        Intent i = new Intent(context, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60, pi); // Millisec * Second * Minute
    }
    private void deactivateAlarm(Context context, int mProjectId)
    {
        ContentValues newProjectValues = new ContentValues();

        newProjectValues.put(ProjectContract.ProjectEntry.COLUMN_ALARM_ACTIVE, false);
            context.getContentResolver().update(ProjectContract.ProjectEntry.CONTENT_URI, newProjectValues, ProjectContract.ProjectEntry._ID + "=?",new String[] {String.valueOf(mProjectId)});
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent("alarm.START_ALARM");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
