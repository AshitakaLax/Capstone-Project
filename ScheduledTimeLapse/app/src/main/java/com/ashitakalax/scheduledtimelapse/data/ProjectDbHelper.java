package com.ashitakalax.scheduledtimelapse.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ashitakalax.scheduledtimelapse.data.ProjectContract.ProjectEntry;

/**
 * Created by Levi Balling on 7/19/2016.
 */
public class ProjectDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "project.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    public ProjectDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_PROJECT_TABLE = "CREATE TABLE " + ProjectEntry.TABLE_NAME + " (" +

                ProjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                ProjectEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ProjectEntry.COLUMN_FREQUENCY + " FLOAT NOT NULL, " +
                ProjectEntry.COLUMN_START_TIME + " LONG NOT NULL, " +
                ProjectEntry.COLUMN_END_TIME + " LONG NOT NULL, " +
                ProjectEntry.COLUMN_ALARM_ACTIVE + " BOOLEAN NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_PROJECT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProjectEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
