package com.nammu.smartwifi;

import android.app.Application;

import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.model.manager.WifiAudioManager;

/**
 * Created by SunJae on 2017-02-18.
 */

public class SmartWifiAplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SLog.debug = true;
        SLog.e("Application Start");
        WifiAudioManager.getInstance(getApplicationContext());
    }
}
