package com.ashitakalax.scheduledtimelapse.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ashitakalax.scheduledtimelapse.data.CameraContract.CameraEntry;

/**
 * Created by Levi Balling on 7/19/2016.
 */
public class CameraDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "camera.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public CameraDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CAMERA_TABLE = "CREATE TABLE " + CameraEntry.TABLE_NAME + " (" +

                CameraEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                CameraEntry.COLUMN_ISO + " INTEGER NOT NULL, " +
                CameraEntry.COLUMN_SHUTTER_SPEED + " INTEGER NOT NULL, " +
                CameraEntry.COLUMN_FLASH + " BOOLEAN NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_CAMERA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CameraEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
