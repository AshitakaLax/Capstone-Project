package com.ashitakalax.scheduledtimelapse.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by lballing on 7/19/2016.
 */
public class TestProjectDb extends AndroidTestCase {

    public static final String LOG_TAG = TestProjectDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(ProjectDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(ProjectContract.ProjectEntry.TABLE_NAME);

        mContext.deleteDatabase(ProjectDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ProjectDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + ProjectContract.ProjectEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> ProjectColumnHashSet = new HashSet<String>();
        ProjectColumnHashSet.add(ProjectContract.ProjectEntry._ID);
        ProjectColumnHashSet.add(ProjectContract.ProjectEntry.COLUMN_TITLE);
        ProjectColumnHashSet.add(ProjectContract.ProjectEntry.COLUMN_FREQUENCY);
        ProjectColumnHashSet.add(ProjectContract.ProjectEntry.COLUMN_START_TIME);
        ProjectColumnHashSet.add(ProjectContract.ProjectEntry.COLUMN_END_TIME);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            ProjectColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                ProjectColumnHashSet.isEmpty());
        db.close();
    }

    public void testProjectTable() {

        ProjectDbHelper dbHelper = new ProjectDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues projectValues = TestUtilities.createProjectValues();

        long rowId = db.insert(ProjectContract.ProjectEntry.TABLE_NAME, null, projectValues);
        assertTrue(rowId != -1);

        Cursor cursor = db.query(
                ProjectContract.ProjectEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                cursor, projectValues);

        assertFalse("Error: More than one record returned from weather query",
                cursor.moveToNext());

        cursor.close();
        dbHelper.close();
    }

}
