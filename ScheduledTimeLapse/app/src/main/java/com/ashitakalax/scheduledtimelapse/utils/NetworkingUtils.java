package com.ashitakalax.scheduledtimelapse.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by lballing on 8/9/2016.
 * This class is to provide helpful networking snippets for this app.
 */
public class NetworkingUtils {

    /**
     * This check is to optimize whether we should even attempt to request the json data
     * Note: this code snippet was taken from
     * http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
     * answer by Alexandre Jasmin
     * @return true if network connection is setup(not direct internet connection)
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
