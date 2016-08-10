package com.ashitakalax.scheduledtimelapse.data;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.ashitakalax.scheduledtimelapse.utils.Utility;
import com.commonsware.cwac.anddown.AndDown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lballing on 8/9/2016.
 * This Class is to fetch the Usermanual from the given URL, and then update what is stored based on that.
 */
public class FetchUsermanualTask extends AsyncTask<String, Void, CharSequence> {
    private final String TAG = this.getClass().getSimpleName();

    public interface OnUserManualReceiveInterface{
        void OnUserManualReceived(String rawManual, CharSequence formattedCharSequence);

        void OnUserManualFailedFetch();
    }

    public FetchUsermanualTask(OnUserManualReceiveInterface userManualCallback)
    {
        if(userManualCallback == null)
        {
            throw new IllegalArgumentException("userManualCallback can't be null");
        }
        this.mRawUserManual = null;
        this.mUserManualCallback = userManualCallback;
    }

    private OnUserManualReceiveInterface mUserManualCallback;
    private String mRawUserManual;

    /**
     * This is where we fetch the json String
     * @param urlTarget to fetch the data
     * @return the Markdown string
     */
    @Override
    protected CharSequence doInBackground(String... urlTarget) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        if(this.mUserManualCallback == null)
        {
            return null;
        }
        try {
            URL url = new URL(urlTarget[0]);

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return "";
            }
            this.mRawUserManual = buffer.toString();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        //store the latest string in preferences
        // convert the raw markdown text into html, then to a character sequence to be displayed

        AndDown convert = new AndDown();

        String cooked = convert.markdownToHtml( this.mRawUserManual);

        CharSequence charSequence = Html.fromHtml(cooked);

        return charSequence;
    }

    /**
     * The data has come back, and we want to just forward that data on
     * @param formattedSequence
     */
    @Override
    protected void onPostExecute(CharSequence formattedSequence) {
        super.onPostExecute(formattedSequence);
        if(this.mUserManualCallback == null)
        {
            return;
        }
        if(formattedSequence == null)
        {
            this.mUserManualCallback.OnUserManualFailedFetch();
        }
        this.mUserManualCallback.OnUserManualReceived(this.mRawUserManual, formattedSequence);
    }
}
