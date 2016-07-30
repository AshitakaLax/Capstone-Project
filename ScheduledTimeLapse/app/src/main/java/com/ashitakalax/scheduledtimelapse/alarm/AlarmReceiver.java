package com.ashitakalax.scheduledtimelapse.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

/**
 * Created by lballing on 7/30/2016.
 * this class will handle the broadcast of the alarm manager
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Put here YOUR code.
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example

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

    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("alarm.START_ALARM");
//        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent("alarm.START_ALARM");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
