package com.nammu.smartwifi.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.interfaces.OnInterface;
import com.nammu.smartwifi.object.WifiScan;
import com.nammu.smartwifi.realmdb.RealmDB;
import com.nammu.smartwifi.realmdb.WifiData;
import com.nammu.smartwifi.realmdb.WifiData_State;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;

/**
 * Created by SunJae on 2017-02-08.
 */

public class SystemService extends Service {
    private String TAG = "##### Service";
    private WifiManager wm;
    private WifiScan wifiScan;
    private Handler scanHandler;
    private int saveTime = 1;
    private int startScan = 0;
    private int scanTimer = 15000;
    private WifiData_State initData;
    private String lastSSID = "";

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiScan = new WifiScan(this, wm, wifiScanResultInterface);
        initData = new WifiData_State();
        scanHandler = new Handler();
        scanStart.run();
    }

    Runnable scanStart = new Runnable() {
        @Override
        public void run() {
           /* Log.e(TAG, "dealy : " +startScan + " : " + saveTime);
           if(startScan >= saveTime){
               startScan = 0;
               wifiScan.Scan();
           }else
               startScan++;*/
            wifiScan.Scan();
            scanHandler.postDelayed(this, scanTimer * saveTime);
        }
    };

   public OnInterface.WifiScanResultInterface wifiScanResultInterface = new OnInterface.WifiScanResultInterface() {
       @Override
       public void setScanResult(List<ScanResult> list) {
            searchWifiList(list);
       }
   };

    public void searchWifiList(List<ScanResult> scanList) {
        int size = 0;
        if(scanList !=null) {
            ScanResult sr;
            size = scanList.size();

            //Level순으로 정렬 원하는 위치면 다른 Wifi보다 세기가 강할 확률이 높음
            Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return (lhs.level <rhs.level ? 1 : (lhs.level==rhs.level ? 0 : -1));
                }
            };
            Collections.sort(scanList, comparator);

            //TODO 삭제
            /*for(int i = 0; i < scanList.size(); i++)
                Log.e(TAG, scanList.get(i).toString());*/

            for(int i = 0; i < size; i++) {
                sr = scanList.get(i);
                Realm realm = RealmDB.RealmInit(this);
                WifiData itemResult = realm.where(WifiData.class).equalTo("SSID", sr.SSID).findFirst();

                //존재하지 않으면 다음 SSID
                //또는 현재 연결된 와이파이가 맞다면
                if (itemResult == null)
                    continue;

                if(wm.getConnectionInfo().getSSID().equals("\""+sr.SSID+"\"")){
                    Log.e(TAG,wm.getConnectionInfo().getSSID() +" : " + sr.SSID);
                    delay();
                    return;
                }

                String bssid = itemResult.getBSSID();
                String[] bssids = bssid.split("@");
                for(int j = 0; j < bssids.length; j++){
                    if(sr.BSSID.equals(bssids[j])){
                        resetDataInit();
                        WifiData_State data_state = realm.where(WifiData_State.class).equalTo("BSSID", itemResult.getBSSID()).findFirst();
                        SetSetting(data_state, itemResult.getSSID());
                        //TODO noti set
                        Notification(itemResult.getSSID());
                        saveTime = 1;
                        return;
                    }
                }
            }
            //1당 15초 딜레이 2배씩 증가
            delay();
            SetSetting(initData, lastSSID);
        }
    }
    private void delay(){
        saveTime *= 2;
        if(saveTime > 20)
            saveTime = 20;
    }
    private void resetDataInit() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        lastSSID = wm.getConnectionInfo().getSSID();
        initData.setWifi_State(wm.isWifiEnabled());
        initData.setBright_State(blueAdapter.isEnabled());
        initData.setSound_State(true);
        initData.setSound_Size(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
        initData.setBright_State(true);
        try {
            if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) != 0) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }
            initData.setBright_Size(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void SetSetting(WifiData_State data_state, String ssid){
        lastSSID = ssid;
        Log.e(TAG, "SetSetting : " + data_state.getWifi_State() + " : " +  data_state.getBluetooth_State() +" : "+ data_state.getSound_State() + " : " + data_state.getBright_State());
        if(data_state.getWifi_State()){
            WifiConnetion(ssid);
        }
        if(data_state.getBluetooth_State()){
            BluetoothConnection();
        }
        if(data_state.getSound_State()) {
            SoundSet(data_state.getSound_Size());
        }
        if(data_state.getBright_State()){
            BrightSet(data_state.getBright_Size());
        }
    }
    private void WifiConnetion(String ssid){
        Log.e(TAG, "WifiConnection");
        wm.setWifiEnabled(true);
        List<WifiConfiguration> list = wm.getConfiguredNetworks();
        try {
            if (list.isEmpty()) {
                Log.v("Error", "list가 빔");
            }

            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                    wm.disconnect();
                    wm.enableNetwork(i.networkId, true);
                    wm.reconnect();
                    break;
                }
            }
        }catch(Exception e){
            WifiConnetion(ssid);
        }
    }

    private void BluetoothConnection(){
        Log.e(TAG, "Blue");
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        blueAdapter.enable();
    }

    private void SoundSet(int size){
        Log.e(TAG, "Sound");
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, size, AudioManager.FLAG_PLAY_SOUND);
    }

    private void BrightSet(int size){
        Log.e(TAG, "Bright");
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, size);
    }

    private void Notification(String ssid){
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 알림 객체 생성
        Notification noti = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cogwheel_1)
                .setTicker("실행 중")
                .setContentTitle("WIFI Change - '" + ssid + "'") //와이파이 ssid값 뽑아야
               // .setContentText("'" + setResult.get(0) +"'") //와이파이 name 뽑아야
                .setWhen(System.currentTimeMillis())
                .build();

        // 알림 방식 지정
        //TODO 설정값에 다른 사운드
        if(true)
            noti.defaults |= Notification.DEFAULT_SOUND;

        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(100, noti);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
