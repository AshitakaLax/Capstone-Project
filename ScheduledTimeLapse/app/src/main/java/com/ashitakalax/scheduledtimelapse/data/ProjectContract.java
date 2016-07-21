package com.ashitakalax.scheduledtimelapse.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Levi Balling on 7/19/2016.
 */
public class ProjectContract {

    public static final String CONTENT_AUTHORITY = "com.ashitakalax.scheduledtimelapse.settings";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PROJECT = "project";

    /* Inner class that defines the table contents of the weather table */
    public static final class ProjectEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROJECT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROJECT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROJECT;

        public static final String TABLE_NAME = "project";

        // Name of the project
        public static final String COLUMN_TITLE = "title";

        // Time between each picture
        public static final String COLUMN_FREQUENCY = "frequency";

        // Time Lapse start time
        public static final String COLUMN_START_TIME = "start_time";
        // Time Lapse end Time
        public static final String COLUMN_END_TIME = "end_time";

        /**
         * Gets the uri from the id to simplify creating the uri's
         *
         * @param id of the camera setting
         * @return uri for the specific camera setting
         */
        public static Uri buildProjectUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
