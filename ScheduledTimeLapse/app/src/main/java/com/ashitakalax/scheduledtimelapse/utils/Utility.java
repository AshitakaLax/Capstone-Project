package com.ashitakalax.scheduledtimelapse.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ashitakalax.scheduledtimelapse.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lballing on 8/9/2016.
 * This class is just to get and set preferences
 */
public class Utility {

    public static final String LATEST_USER_MANUAL_FILE = "latest_user_manual_file";
    public static final String LATEST_USER_MANUAL_RAW = "latest_user_manual_raw";


    public static String getLatestUserManualFile(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(LATEST_USER_MANUAL_FILE, Context.MODE_PRIVATE);
        return prefs.getString(LATEST_USER_MANUAL_RAW, readRawTextFile(context, R.raw.readme));
    }

    /**
     * this is pre conversion of the string
     * @param context
     * @param rawUserManual
     */
    public static void setLatestUserManualFile(Context context, String rawUserManual)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(LATEST_USER_MANUAL_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(LATEST_USER_MANUAL_RAW, rawUserManual);
        editor.apply();
    }


    /**
     * This function is here just in case I want to store the default user manual, if there never is an internet connection
     * @param ctx context
     * @param resId resource Id
     * @return string from the text file
     */
    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream=ctx.getResources().openRawResource(resId);
        InputStreamReader inputreader=new InputStreamReader(inputStream);
        BufferedReader buffreader=new BufferedReader(inputreader);
        String line;
        StringBuilder text=new StringBuilder();

        try {
            while ((line=buffreader.readLine())!=null) {
                text.append(line);
                text.append('\n');
            }
        }
        catch (IOException e) {
            return null;
        }
        return text.toString();
    }


}
