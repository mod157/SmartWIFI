package com.nammu.smartwifi.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
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

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.UI.setlist.MainActivity;
import com.nammu.smartwifi.WifiAudioManager;
import com.nammu.smartwifi.model.OnInterface;
import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.model.WifiScan;
import com.nammu.smartwifi.realmdb.RealmDB;
import com.nammu.smartwifi.realmdb.realmobject.WifiData;
import com.nammu.smartwifi.realmdb.realmobject.WifiDataState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by SunJae on 2017-02-08.
 */

public class SystemService extends Service {
    private final int DELAY_MAX = 20; // 5분
    private final int DELAY_ADD = 2;
    private final int SCAN_TIME = 15000;
    private WifiAudioManager wifiAudioManager;
    private WifiManager wm;
    private WifiScan wifiScan;
    private Handler scanHandler;
    private int saveTime = 1;
    private WifiDataState initData;
    private String lastSSID = "";
    private boolean flag = false;
    private  Realm realm;
    private ArrayList<WifiData> results;
    private int resultsItemNumber = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        SLog.d("Start Service");
        realm = RealmDB.RealmInit(this);
        wifiAudioManager = WifiAudioManager.getInstance();
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiScan = new WifiScan(this, wm, wifiScanResultInterface);
        initData = new WifiDataState();
        scanHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkingAddList())
                    wifiScan.Scan();
                else
                    delay();
                scanHandler.postDelayed(this, SCAN_TIME * saveTime);
            }
        }).start();
    }
    private boolean checkingAddList(){
        Realm sizeRealm = RealmDB.RealmInit(getBaseContext());
        RealmResults<WifiData> result = sizeRealm.where(WifiData.class).findAll();
        SLog.d("Size Check : " + result.size());
        if(result.size() != 0)
            return  true;
        else
            return  false;
    }

   public OnInterface.WifiScanResultInterface wifiScanResultInterface = new OnInterface.WifiScanResultInterface() {
       @Override
       public void setScanResult(List<ScanResult> list) {
            searchWifiList(list);
       }
   };

    public void searchWifiList(List<ScanResult> scanList) {
        if(scanList !=null) {
            wifiScan.sortLevelResult(scanList);
            results = new ArrayList<>();
            scanListEqualBSSID(scanList, results);

            if(results.size() != 0) {
                sortPripority(results);
                for(int i = 0 ; i < results.size(); i++) {
                    WifiData wifiData = results.get(i);
                    SLog.d(wifiData.getSSID() + " : " + wifiData.getPripority());
                }
                for (int i = 0; i < results.size(); i++) {
                    WifiData item = results.get(i);
                    boolean ssidCheck = wm.getConnectionInfo().getSSID().equals("\"" + item.getSSID() + "\"");
                    /*if (ssidCheck && flag) {
                        SLog.d("이미 연결됨 " + wm.getConnectionInfo().getSSID() + " : " + item.getSSID());
                        delay();
                        return;
                    }*/

                    if(lastSSID.equals(item.getSSID())){
                        SLog.d("이미 연결됨 " + wm.getConnectionInfo().getSSID() + " : " + item.getSSID());
                        delay();
                        return;
                    }

                    if (!ssidCheck && flag) {
                        //TODO 사용자가 직접 변경?
                        SLog.d("직접 변경");
                        //otification_button(item.getSSID(), results);
                        return;
                    }

                    //초기화 작업
                    if (!flag)
                        resetDataInit();
                    addItemWifiConnection(results);
                    return;
                }
            }
            //1당 15초 딜레이 2배씩 증가
            delay();
            if(flag) {
                SLog.d("주변에 와이파이가 없음");
                setSetting(initData, lastSSID);
                notification("없음");
                flag = false;
            }
        }
    }

    private void addItemWifiConnection(ArrayList<WifiData> results){
        SLog.d("Setting");
        if(results.size() == resultsItemNumber)
            resultsItemNumber = 0;
        flag = true;

        WifiData item = results.get(resultsItemNumber);
        lastSSID = item.getSSID();
        WifiDataState data_state = realm.where(WifiDataState.class).equalTo("BSSID",item.getBSSID()).findFirst();
        setSetting(data_state, item.getSSID());
        //TODO noti set
        setNotification(item.getSSID(), results);
        saveTime = 1;
    }

    private void sortPripority(ArrayList<WifiData> data){
        Comparator<WifiData> comparatorPripority = new Comparator<WifiData>() {
            @Override
            public int compare(WifiData t1, WifiData t2) {
                return (((Integer)t2.getPripority()).compareTo((Integer)t1.getPripority()));
            }
        };
        Collections.sort(data, comparatorPripority);
    }

    private void delay(){
        SLog.d("dealy");
        saveTime *= DELAY_ADD;
        if(saveTime > DELAY_MAX)
            saveTime = DELAY_MAX;
    }

    private void resetDataInit() {
        SLog.d("초기화 Data 저장");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        lastSSID = wm.getConnectionInfo().getSSID();
        initData.setWifiState(wm.isWifiEnabled());
        initData.setBrightState(blueAdapter.isEnabled());
        initData.setSoundState(true);
        initData.setSoundSize(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
        initData.setBrightState(true);
        try {
            if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) != 0) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }
            initData.setBrightSize(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setSetting(WifiDataState data_state, String ssid){
        lastSSID = ssid;
        SLog.d("SetSetting : " + data_state.getWifiState() + " : " +  data_state.getBluetoothState() +" : "+ data_state.getSoundState() + " : " + data_state.getBrightState());
        if(data_state.getWifiState()){
            wifiScan.wifiConnetion(ssid);
        }
        if(data_state.getBluetoothState()){
            BluetoothConnection();
        }
        if(data_state.getSoundState()) {
            SoundSet(data_state.getSoundSize());
        }
        if(data_state.getBrightState()){
            BrightSet(data_state.getBrightSize());
        }
    }


    private void BluetoothConnection(){
        SLog.d("Blue");
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        blueAdapter.enable();
    }

    private void SoundSet(int size){
        SLog.d("Sound");
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, size, AudioManager.FLAG_PLAY_SOUND);
    }

    private void BrightSet(int size){
        SLog.d("Bright");
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, size);
    }

    private void scanListEqualBSSID(List<ScanResult> scanList, ArrayList<WifiData> results){
        int size = scanList.size();
        for(int i = 0; i < size; i++) {
            ScanResult sr = scanList.get(i);
            WifiData itemResult = realm.where(WifiData.class).equalTo("SSID", sr.SSID).equalTo("isPlay", true).findFirst();

            //존재하지 않으면 다음 SSID
            //또는 현재 연결된 와이파이가 맞다면
            if (itemResult == null)
                continue;

            String bssid = itemResult.getBSSID();
            String[] bssids = bssid.split("@");
            for(int j = 0; j < bssids.length; j++) {
                if (sr.BSSID.equals(bssids[j])) {
                    SLog.d("Set List : " + itemResult.getSSID() + ", " + itemResult.getPripority() + sr.level);
                    results.add(itemResult);
                    break;
                }
            }
        }
    }

    private void setNotification(String ssid, ArrayList<WifiData> itemList){
        if (results.size() == 1)
            notification(ssid);
        else
            notification_button(ssid, itemList);
    }

    private void notification_button(String ssid, ArrayList<WifiData> itemList){
        ArrayList<String> ssidList = new ArrayList<>();
        for(int j = 0; j<itemList.size(); j++){ //SSID 중복 제거 (맥주소 통일화)
            WifiData item = itemList.get(j);
            if(item.getSSID().equals(""))
                continue;
            ssidList.add(item.getSSID());
        }
        ArrayList<String> wifilist = new ArrayList(new HashSet(ssidList));
        for(int i = 0; i< wifilist.size(); i++){
            SLog.d("Noti value :  " + wifilist.get(i));
        }

        /*//TODO intent로 broadcast로 던져준다.
        Intent intent = new Intent(this, wifiConnectionChangeReceiver);
        intent.setAction("edit");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);*/

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 알림 객체 생성
        Notification noti = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cogwheel_1)
                .setTicker("실행 중")
                .setContentTitle("WIFI Change - '" + ssid + "'")
                .setContentText("다른 '" + (wifilist.size()-1) +"' 개 존재합니다.")
                .setWhen(System.currentTimeMillis())
              //  .addAction(R.drawable.clickborder,"변경",pIntent)
                .build();

        // 알림 방식 지정
        //TODO 설정값에 다른 사운드
        if(true)
            noti.defaults |= Notification.DEFAULT_SOUND;
        noti.flags = Notification.FLAG_NO_CLEAR;
        nm.notify(100, noti);
    }

    private void notification(String ssid){
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
        noti.flags = Notification.FLAG_NO_CLEAR;
        nm.notify(100, noti);

    }

    BroadcastReceiver wifiConnectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

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
