package com.ashitakalax.scheduledtimelapse.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.ashitakalax.scheduledtimelapse.ActiveTimelapseProjectsWidget;
import com.ashitakalax.scheduledtimelapse.R;
import com.ashitakalax.scheduledtimelapse.controller.CameraController;
import com.ashitakalax.scheduledtimelapse.data.ProjectContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by lballing on 7/30/2016.
 * this class will handle the broadcast of the alarm manager
 */
public class AlarmReceiver extends BroadcastReceiver{

    static final String TAG = "ALARM_RECEIVER";


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_PROJECT_ID = 0;
    static final int COL_PROJECT_TITLE = 1;
    static final int COL_PROJECT_FREQUENCY = 2;
    static final int COL_PROJECT_START_TIME = 3;
    static final int COL_PROJECT_END_TIME = 4;
    static final int COL_PROJECT_ACTIVE = 5;

    static final String BUNDLE_PROJECT_TITLE = "project_title";
    //static final String BUNDLE_PROJECT_ID = "project_id";


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        final String ProjectTitle;
        //final int projectId;
        if(bundle != null)
        {
            ProjectTitle = bundle.getString(BUNDLE_PROJECT_TITLE, "No Extras");
            //projectId = bundle.getInt(BUNDLE_PROJECT_ID, -1);
        }
        else
        {
            ProjectTitle = "No Extras";
        }
        Log.i(TAG, "received extra(" + ProjectTitle + ")");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();


        // Put here YOUR code.
        Toast.makeText(context, "Taking a Picture", Toast.LENGTH_LONG).show(); // For example
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();

        final Camera camera = CameraController.getCameraInstance(context);
        if(camera == null)
        {
            Log.i(TAG, "Couldn't acquire instance of camera");
            checkAlarms(context);

            wl.release();
            return;
        }
        Log.i(TAG, "Camera instance acquired");

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.i(TAG, "Camera Starting Preview");
                    camera.startPreview();
                    camera.takePicture(null, null, new Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Log.d(TAG, "On Picture Taken: ");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);

                            if (bitmap == null){
                                Log.e(TAG, "Error creating media file, check storage permissions: ");
                                return;
                            }

                            Log.d(TAG, "Writing to File: ");
                            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ScheduledTimelapse" + File.separator + ProjectTitle;
                            File directory = new File(path);
                            if(!directory.isDirectory()) {
                                directory.mkdirs();
                            }
                            //get the number of files in the directory
                            File fileList[] = directory.listFiles();

                            File file=new File(path, ProjectTitle + fileList.length +".jpg");
                            try {
                                FileOutputStream fos = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                            } catch (FileNotFoundException e) {
                                Log.d(TAG, "File not found: " + e.getMessage());
                            } catch (IOException e) {
                                Log.d(TAG, "Error accessing file: " + e.getMessage());
                            }
                            finally {
                                Log.i(TAG, "releasing Camera");
                                camera.release();
                            }
                        }
                    });
//                    camera.takePicture(null, null, CameraController.handlePictureCallback);
                } catch (Exception e) {
                    Log.e(TAG, "Taking Picture error:" +e.getMessage());
                    if (camera != null) {
                        Log.i(TAG, "releasing Camera");
                        camera.release();
                    }
                    throw new RuntimeException(e);
                }
            }

            @Override public void surfaceDestroyed(SurfaceHolder holder) {}
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                PixelFormat.TRANSLUCENT);

        Log.i(TAG, "adding preview to window manager");
        windowManager.addView(preview, params);

        // update the next time this specific project will go off next
        Log.i(TAG, "Checking/updating alarms");
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
//        Intent cancelIntent = new Intent("alarm.START_ALARM");
        Intent cancelIntent = new Intent(context,AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);
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
//                Intent futureIntent = new Intent("alarm.START_ALARM");
                Intent futureIntent = new Intent(context,AlarmReceiver.class);
                String projectTitle =cursor.getString(COL_PROJECT_TITLE);
                Log.i(TAG, "Next Alarm is(" + projectTitle + ")");
                futureIntent.putExtra(BUNDLE_PROJECT_TITLE, projectTitle);
                futureIntent.putExtra(BUNDLE_PROJECT_TITLE, projectTitle);
                PendingIntent futurePendingIntent = PendingIntent.getBroadcast(context, 0, futureIntent, 0);
                //time is valid to set
                am.setRepeating(AlarmManager.RTC_WAKEUP, startCalendarTime.getTimeInMillis(), frequencyIncrement, futurePendingIntent); // Millisec * Second * Minute
                //update Widget

            }
        } finally {
            cursor.close();
        }
        updateWidget(context);
    }

    private void updateWidget(Context context)
    {
        Intent intent = new Intent(context, ActiveTimelapseProjectsWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {R.xml.active_timelapse_projects_widget_info};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
    //todo unit test this
    public static Calendar getNextAlarm(long startTime, float frequency)
    {
        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        double period = (1/frequency);
        //the period is how far apart each picture will be taken, should be 5+ seconds
        period *= 1000; // convert the period into milliseconds for precision here
        while(calendar.getTimeInMillis() < now.getTimeInMillis())
        {
            calendar.add(Calendar.MILLISECOND, (int)period);
        }
        return calendar;//currently this isn't correct. this could be in the past
    }

    public void setAlarm(Context context)
    {
        checkAlarms(context);
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
