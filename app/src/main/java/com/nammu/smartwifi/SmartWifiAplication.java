package com.nammu.smartwifi;

import android.app.Application;

/**
 * Created by SunJae on 2017-02-18.
 */

public class SmartWifiAplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        WifiAudioManager.getInstance(getApplicationContext());
    }
}
