package com.nammu.smartwifi.model;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by SunJae on 2017-02-17.
 */

public class ServiceCheck {
    public static boolean isServiceRunningCheck(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("ServiceName".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
