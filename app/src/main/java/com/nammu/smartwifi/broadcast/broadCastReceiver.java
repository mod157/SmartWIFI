package com.nammu.smartwifi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * Created by SunJae on 2017-02-13.
 */

public class broadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){

        }

        //종료 후 자동실행
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            //실행할 액티비티

        }
    }
}
