package com.ashitakalax.scheduledtimelapse.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Levi Balling on 7/19/2016.
 */
public class CameraContract {
    public static final String CONTENT_AUTHORITY = "com.ashitakalax.scheduledtimelapse.data";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CAMERA = "camera";

    /* Inner class that defines the table contents of the weather table */
    public static final class CameraEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAMERA).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAMERA;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAMERA;

        public static final String TABLE_NAME = "camera";

        // Camera Sensor sensitivity
        public static final String COLUMN_ISO = "iso";

        // How fast the shutter speed should be
        public static final String COLUMN_SHUTTER_SPEED = "shutter_spd";

        // Whether to enable flash or not
        public static final String COLUMN_FLASH = "flash";

        /**
         * Gets the uri from the id to simplify creating the uri's
         *
         * @param id of the camera setting
         * @return uri for the specific camera setting
         */
        public static Uri buildCameraUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
