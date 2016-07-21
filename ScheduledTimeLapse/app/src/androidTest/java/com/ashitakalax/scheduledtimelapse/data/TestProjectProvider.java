package com.ashitakalax.scheduledtimelapse.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.ashitakalax.scheduledtimelapse.data.ProjectContract.ProjectEntry;

/**
 * Created by lballing on 7/20/2016.
 * Tests the Camera Content Provider
 */
public class TestProjectProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProjectProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                ProjectEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                ProjectEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                ProjectSettingsProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: ProjectProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + ProjectContract.CONTENT_AUTHORITY,
                    providerInfo.authority, ProjectContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: ProjectProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(ProjectEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the ProjectEntry CONTENT_URI should return ProjectEntry.CONTENT_TYPE",
                ProjectEntry.CONTENT_TYPE, type);
    }

    public void testBasicProjectQuery() {
        // insert our test records into the database
        CameraDbHelper dbHelper = new CameraDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        // Fantastic.  Now that we have a location, add some weather!
        ContentValues cameraValues = TestUtilities.createCameraValues();

        long ProjectRowId = db.insert(ProjectEntry.TABLE_NAME, null, cameraValues);
        assertTrue("Unable to Insert ProjectEntry into the Database", ProjectRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                ProjectEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicProjectQuery", cursor, cameraValues);
    }

    public void testUpdateProject() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createCameraValues();

        Uri projectUri = mContext.getContentResolver().
                insert(ProjectEntry.CONTENT_URI, values);
        long ProjectRowId = ContentUris.parseId(projectUri);

        // Verify we got a row back.
        assertTrue(ProjectRowId != -1);
        Log.d(LOG_TAG, "New row id: " + ProjectRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(ProjectEntry._ID, ProjectRowId);
        updatedValues.put(ProjectEntry.COLUMN_TITLE, "Bulb");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor projectCursor = mContext.getContentResolver().query(ProjectEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        projectCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                ProjectEntry.CONTENT_URI, updatedValues, ProjectEntry._ID + "= ?",
                new String[]{Long.toString(ProjectRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        projectCursor.unregisterContentObserver(tco);
        projectCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ProjectEntry.CONTENT_URI,
                null,   // projection
                ProjectEntry._ID + " = " + ProjectRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateProject.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }
}
