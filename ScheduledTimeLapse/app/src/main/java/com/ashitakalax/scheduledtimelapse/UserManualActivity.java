package com.ashitakalax.scheduledtimelapse;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.ashitakalax.scheduledtimelapse.data.FetchUsermanualTask;
import com.ashitakalax.scheduledtimelapse.utils.NetworkingUtils;
import com.ashitakalax.scheduledtimelapse.utils.Utility;
import com.commonsware.cwac.anddown.AndDown;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PublicKey;

/**
 * Created by lballing on 7/26/2016.
 * this will use AndDown to render markup to  simplify formatting the user manual
 */
public class UserManualActivity extends AppCompatActivity implements FetchUsermanualTask.OnUserManualReceiveInterface {

    private String userManualUrl = "https://raw.githubusercontent.com/AshitakaLax/Capstone-Project/master/README.md";
    private FetchUsermanualTask mFetchUserManual;
    private TextView mUserManualTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.markdown_layout);
        this.mUserManualTextView = (TextView)findViewById(R.id.user_manual_textview);


        String userManualRaw = "";
        if(NetworkingUtils.isNetworkAvailable(this))
        {
            new FetchUsermanualTask(this).execute(userManualUrl);
            return;
        }
        else
        {
            //todo we need to revert to a previous version of the usermanual already stored on the phone in a key Value Pair
            userManualRaw  = Utility.getLatestUserManualFile(this);
        }

        AndDown convert = new AndDown();

        String cooked = convert.markdownToHtml(userManualRaw);

        CharSequence formatted = Html.fromHtml(cooked);

        mUserManualTextView.setText(formatted);
    }

    /**
     * This will be the converted CharSequence that we need to display on the UI
     * @param raw raw string from the fetch
     * @param formatted formatted Char Sequence
     */
    @Override
    public void OnUserManualReceived(String raw, CharSequence formatted) {
        Utility.setLatestUserManualFile(this, raw);
        mUserManualTextView.setText(formatted);
    }

    @Override
    public void OnUserManualFailedFetch() {

        //todo we need to revert to a previous version of the usermanual already stored on the phone in a key Value Pair
        String userManualRaw  = Utility.getLatestUserManualFile(this);
        AndDown convert = new AndDown();

        String cooked = convert.markdownToHtml(userManualRaw);

        CharSequence charSequence = Html.fromHtml(cooked);
    }
}
