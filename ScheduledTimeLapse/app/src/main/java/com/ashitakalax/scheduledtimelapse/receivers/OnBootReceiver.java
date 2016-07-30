package com.ashitakalax.scheduledtimelapse.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ashitakalax.scheduledtimelapse.alarm.AlarmReceiver;

/**
 * Created by lballing on 7/29/2016.
 */
public class OnBootReceiver extends BroadcastReceiver {
    AlarmReceiver alarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            // setup all the alarms here
            alarm.setAlarm(context);
        }
    }
}
