package com.ashitakalax.scheduledtimelapse;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.ashitakalax.scheduledtimelapse.R;
import com.ashitakalax.scheduledtimelapse.alarm.AlarmReceiver;
import com.ashitakalax.scheduledtimelapse.data.ProjectContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class ActiveTimelapseProjectsWidget extends AppWidgetProvider {
    //todo unify the source of all these constants
    private static final String[] PROJECT_COLUMNS = {
            ProjectContract.ProjectEntry.TABLE_NAME + "." + ProjectContract.ProjectEntry._ID,
            ProjectContract.ProjectEntry.COLUMN_TITLE,
            ProjectContract.ProjectEntry.COLUMN_FREQUENCY,
            ProjectContract.ProjectEntry.COLUMN_START_TIME,
            ProjectContract.ProjectEntry.COLUMN_END_TIME,
            ProjectContract.ProjectEntry.COLUMN_ALARM_ACTIVE
    };
    static final int COL_PROJECT_ID = 0;
    static final int COL_PROJECT_TITLE = 1;
    static final int COL_PROJECT_FREQUENCY = 2;
    static final int COL_PROJECT_START_TIME = 3;
    static final int COL_PROJECT_END_TIME = 4;
    static final int COL_PROJECT_ACTIVE = 5;

    private static final SimpleDateFormat calFormat = new SimpleDateFormat("EEE, MMM d, hh:mm aaa", Locale.US);

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Cursor cursor =context.getContentResolver().query(
                ProjectContract.ProjectEntry.CONTENT_URI
                ,null
                ,null
                ,null, ProjectContract.ProjectEntry.COLUMN_ALARM_ACTIVE + " DESC");
        boolean isActive = false;
        // first active alarm
        if(cursor != null && cursor.moveToFirst()) {
            isActive = cursor.getString(COL_PROJECT_ACTIVE).equals("1");
        }
        String title;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.active_timelapse_projects_widget);
        if(!isActive)
        {
            title = context.getString(R.string.widget_no_active_projects);
            views.setTextViewText(R.id.appwidget_time_text, "");
            views.setContentDescription(R.id.appwidget_time_text, "");
        }
        else {
            title = cursor.getString(COL_PROJECT_TITLE);
            long startTime = cursor.getLong(COL_PROJECT_START_TIME);
            float freq = cursor.getFloat(COL_PROJECT_FREQUENCY);

            Calendar nextPicture = AlarmReceiver.getNextAlarm(startTime, freq);

            String timeStr = calFormat.format(nextPicture.getTime());
            views.setTextViewText(R.id.appwidget_time_text, timeStr);
            views.setContentDescription(R.id.appwidget_time_text, timeStr);
        }
        // Construct the RemoteViews object
        views.setTextViewText(R.id.appwidget_title_text, title);
        views.setContentDescription(R.id.appwidget_title_text, title);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(new ComponentName(context, ActiveTimelapseProjectsWidget.class), views);
        cursor.close();
//        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

