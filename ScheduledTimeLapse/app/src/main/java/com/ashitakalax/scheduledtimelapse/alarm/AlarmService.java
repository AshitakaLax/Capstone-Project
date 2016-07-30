package com.ashitakalax.scheduledtimelapse.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by lballing on 7/30/2016.
 */
public class AlarmService extends Service {

    AlarmReceiver alarm = new AlarmReceiver();
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarm.setAlarm(this);
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        alarm.setAlarm(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
