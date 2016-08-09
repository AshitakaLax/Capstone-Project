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
        }
        else
        {
            //todo we need to revert to a previous version of the usermanual already stored on the phone in a key Value Pair
            userManualRaw = readRawTextFile(this, R.raw.readme);
        }

        AndDown convert = new AndDown();

        String cooked = convert.markdownToHtml(userManualRaw);

        CharSequence charSequence = Html.fromHtml(cooked);

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

    /**
     * This will be the converted CharSequence that we need to display on the UI
     * @param sequence formatted Char Sequence
     */
    @Override
    public void OnUserManualReceived(CharSequence sequence) {
        mUserManualTextView.setText(sequence);
    }
}
