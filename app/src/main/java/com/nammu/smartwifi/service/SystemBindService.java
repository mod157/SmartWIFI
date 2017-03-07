package com.nammu.smartwifi.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.nammu.smartwifi.model.SLog;

/**
 * Created by SunJae on 2017-02-22.
 */

public class SystemBindService extends IntentService {
    private SystemService boundService;
    Intent eventIntent;
    public SystemBindService() {
        super("BindService");
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            SystemService.ServiceBinder binder = (SystemService.ServiceBinder) service;
            boundService = binder.getService();
            SLog.d("ServiceConnection");
            switch (eventIntent.getAction()){
                case "stopService":
                    boundService.onServiceStop();
                    break;
                case "startService":
                    boundService.onServiceStart();
                    break;
                case "changeConnection":
                    boundService.onChangeConnection();
                    break;
                case "resetConnection":
                    boundService.onReStart();
                    break;
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            boundService = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        SLog.d("BindService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        eventIntent = intent;
        SLog.d("HandelrIntent");
        getApplicationContext().bindService(new Intent(this, SystemService.class), mConnection, Context.BIND_AUTO_CREATE);
    }
}
