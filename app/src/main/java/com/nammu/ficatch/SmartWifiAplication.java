package com.nammu.ficatch;

import android.app.Application;

import com.nammu.ficatch.model.SLog;
import com.nammu.ficatch.model.manager.WifiAudioManager;

/**
 * Created by SunJae on 2017-02-18.
 */

public class SmartWifiAplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SLog.debug = false;
        SLog.e("Application Start");
        WifiAudioManager.getInstance(getApplicationContext());
    }
}
