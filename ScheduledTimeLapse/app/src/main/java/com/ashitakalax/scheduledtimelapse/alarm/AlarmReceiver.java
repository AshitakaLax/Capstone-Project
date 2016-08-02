package com.ashitakalax.scheduledtimelapse.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
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
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Put here YOUR code.
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example

        // update the next time this specific project will go off next

        wl.release();

//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//        manager.cancel(pendingIntent);
//        Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        // For our recurring task, we'll just display a message
//        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks that alarms are setup to run at the correct time to take a picture
     * @param context
     */
    public void checkAlarms(Context context)
    {
        String sortOrder = ProjectContract.ProjectEntry.COLUMN_START_TIME + "ASC";
        Cursor cursor = context.getContentResolver().query(ProjectContract.ProjectEntry.CONTENT_URI
        ,null
        ,null
        ,null
        ,null);

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

                long startTime = cursor.getLong(COL_PROJECT_START_TIME);
                long endTime = cursor.getLong(COL_PROJECT_END_TIME);
                float frequency = cursor.getFloat(COL_PROJECT_FREQUENCY);
                //check if the endTime has already past
                Calendar endCalendarTime = Calendar.getInstance();
                endCalendarTime.setTimeInMillis(endTime);
                if(endCalendarTime.compareTo(now) >= 0)
                {
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

                if(startCalendarTime.compareTo(endCalendarTime) >= 0)
                {
                    continue;
                }
                //time is valid to set
                Times.add(startCalendarTime);
                increments.add(frequencyIncrement);
            }
        } finally {
            cursor.close();
        }

        //todo sort through all of the arrays
        //get the next alarm that we need
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("alarm.START_ALARM");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        for (int j = 0; j < Times.size(); j++) {
            Calendar alarmCalendar = Times.get(j);
            int alarmIncrement = increments.get(j);
            am.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), alarmIncrement, pi); // Millisec * Second * Minute
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

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent("alarm.START_ALARM");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
