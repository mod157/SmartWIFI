package com.nammu.smartwifi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.service.SystemService;

/**
 * Created by SunJae on 2017-02-13.
 */

public class broadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SLog.d("Receive");
        String action = intent.getAction();
        //종료 후 자동실행
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            //실행할 액티비티
            Intent service = new Intent(context, SystemService.class);
            context.startService(service);
        }
    }
}
