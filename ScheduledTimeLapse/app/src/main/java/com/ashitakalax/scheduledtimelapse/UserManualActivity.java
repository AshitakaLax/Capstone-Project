package com.ashitakalax.scheduledtimelapse;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.commonsware.cwac.anddown.AndDown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PublicKey;

/**
 * Created by lballing on 7/26/2016.
 * this will use AndDown to render markup to  simplify formatting the user manual
 */
public class UserManualActivity extends AppCompatActivity {

    private String userManualUrl = "https://raw.githubusercontent.com/AshitakaLax/Capstone-Project/master/README.md";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.markdown_layout);
        TextView userManualTextView = (TextView)findViewById(R.id.user_manual_textview);

        String raw=readRawTextFile(this, R.raw.readme);

        AndDown convert = new AndDown();

        String cooked = convert.markdownToHtml(raw);

        CharSequence charSequence = Html.fromHtml(cooked);

        userManualTextView.setText(charSequence);
    }

    /**
     * This function is here just in case I want to store the user manual updates as a text file
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
