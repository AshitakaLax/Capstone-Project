package com.ashitakalax.scheduledtimelapse;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by lballing on 8/10/2016.
 * this will do all the things specific to this flavor to support adds
 */
public class AdSupport {

    private AdView mAdView;
    public AdSupport()
    {

    }

    public void handleOnCreate(Context context, View adView)
    {
        //todo pull the string from my publisher value
        String adModUnitId = context.getResources().getString(R.string.admob_app_id);
        MobileAds.initialize(context, adModUnitId);
        this.mAdView = (AdView)adView;//.findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }

    public void handleOnPause()
    {
        if(this.mAdView != null)
        {
            this.mAdView.pause();
        }
    }

    public void handleOnResume()
    {
        if(this.mAdView != null)
        {
            this.mAdView.resume();
        }
    }

    public void handleOnDestroy()
    {
        if(this.mAdView != null)
        {
            this.mAdView.destroy();
        }
    }


}
