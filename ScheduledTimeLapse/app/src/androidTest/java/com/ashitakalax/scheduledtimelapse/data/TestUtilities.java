package com.ashitakalax.scheduledtimelapse.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.ashitakalax.scheduledtimelapse.utils.PollingCheck;

import junit.framework.AssertionFailedError;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Levi Balling on 7/20/2016.
 */
public class TestUtilities extends AndroidTestCase {


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(idx);
            if (columnName.equals(CameraContract.CameraEntry.COLUMN_FLASH) || columnName.equals(ProjectContract.ProjectEntry.COLUMN_ALARM_ACTIVE)) {

                actualValue = (actualValue.equals("1")) ? "true" : "false";
            }
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, actualValue);
        }
    }

    /**
     * Create Content values for testing
     *
     * @return Content values
     */
    static ContentValues createCameraValues() {
        ContentValues values = new ContentValues();
        values.put(CameraContract.CameraEntry.COLUMN_ISO, 100);
        values.put(CameraContract.CameraEntry.COLUMN_SHUTTER_SPEED, 0.125);
        values.put(CameraContract.CameraEntry.COLUMN_FLASH, true);
        return values;
    }

    /**
     * Creates Content values for testing purposes
     *
     * @return Content values for Project Db
     */
    static ContentValues createProjectValues() {
        ContentValues values = new ContentValues();
        values.put(ProjectContract.ProjectEntry.COLUMN_TITLE, "Flower");
        values.put(ProjectContract.ProjectEntry.COLUMN_FREQUENCY, 0.2);//every 5 seconds
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        Date date = null;
        try {
            date = formatter.parse("2016-10-01 13:30:10.123");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AssertionFailedError();
        }
        values.put(ProjectContract.ProjectEntry.COLUMN_START_TIME, date.getTime());

        try {
            date = formatter.parse("2017-10-01 13:30:10.123");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AssertionFailedError();
        }
        values.put(ProjectContract.ProjectEntry.COLUMN_END_TIME, date.getTime());
        values.put(ProjectContract.ProjectEntry.COLUMN_ALARM_ACTIVE, true);

        return values;
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
}
