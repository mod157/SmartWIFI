package com.nammu.smartwifi.model.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.realmdb.realmobject.WifiData;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by SunJae on 2017-02-21.
 */

public class WifiNotificationManager {
    private Context context;
    private NotificationManager nm;

    public WifiNotificationManager(Context context){
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        this.context = context;
    }

    public void notification(String name, String ssid){
        if(name.equals("중지"))
            noti(name, ssid,startAction(),null);
        else
            noti(name, ssid, stopAction(), null);
    }

    public void notification(String ssid, ArrayList<WifiData> itemList, int resultNumber) {
        SLog.d(resultNumber+"");
        int itemNumber = resultNumber+1;
        String nameSSID = itemList.get(resultNumber).getName() + "(" + ssid + ")";

        if(itemList.size() == itemNumber)
            itemNumber = 0;

        SLog.d("result : " + resultNumber + ", itemNumber : " + itemNumber);
        String contentText = context.getString(R.string.noti_contentTextFirst) +" '" + itemList.get(itemNumber).getSSID() +"' " + context.getString(R.string.noti_contentTextEnd);
        NotificationCompat.Action changeAction = changeAction();
        NotificationCompat.Action stopAction = stopAction();
        // 알림 객체 생성
        noti(nameSSID, contentText, stopAction, changeAction);
    }

    private NotificationCompat.Action stopAction(){
        Intent intent = new Intent("stopService");
        PendingIntent pIntent = PendingIntent.getBroadcast(context,0,intent,0);
        NotificationCompat.Action changeAction = new NotificationCompat.Action(R.drawable.pause,
                context.getString(R.string.noti_stop),
                pIntent);
        return changeAction;
    }
    private NotificationCompat.Action startAction(){
        //TODO intent로 broadcast로 던져준다.
        Intent intent = new Intent("startService");
        PendingIntent pIntent = PendingIntent.getBroadcast(context,0,intent,0);
        NotificationCompat.Action startAction = new NotificationCompat.Action(R.drawable.play,
                context.getString(R.string.noti_start),
                pIntent);
        return startAction;
    }

    private NotificationCompat.Action changeAction(){
        //TODO intent로 broadcast로 던져준다.
        Intent intent = new Intent("setChangeWifiConnection");
        PendingIntent pIntent = PendingIntent.getBroadcast(context,0,intent,0);
        NotificationCompat.Action changeAction = new NotificationCompat.Action(R.drawable.trans_arrows,
                context.getString(R.string.noti_wifiChange),
                pIntent);
        return changeAction;
    }

    private void noti(String ssid, String contentText, NotificationCompat.Action stopAction, NotificationCompat.Action changeAction){
        int color = 0xff3F51B5;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notiicon)
                .setTicker("Service Set")
                .setContentTitle("WIFI - '" + ssid + "'")
                .setContentText(contentText)
                .setWhen(System.currentTimeMillis())
                .setColor(color)
                .addAction(stopAction);
        if(changeAction != null)
            builder.addAction(changeAction);
        Notification noti = builder.build();
        // 알림 방식 지정
        //TODO 설정값에 다른 사운드
        if(true)
            noti.defaults |= Notification.DEFAULT_SOUND;
        noti.flags = Notification.FLAG_NO_CLEAR;
        nm.notify(100, noti);
    }
}
