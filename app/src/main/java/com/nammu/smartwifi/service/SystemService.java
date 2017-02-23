package com.nammu.smartwifi.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.nammu.smartwifi.model.OnInterface;
import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.model.ServiceEvent;
import com.nammu.smartwifi.model.manager.WifiAudioManager;
import com.nammu.smartwifi.model.manager.WifiBluetoothManager;
import com.nammu.smartwifi.model.manager.WifiBrightManager;
import com.nammu.smartwifi.model.manager.WifiNotificationManager;
import com.nammu.smartwifi.model.manager.WifiScan;
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

public class SystemService extends Service implements ServiceEvent.changeNotificationEventListener {
    private final int DELAY_MAX = 20; // 5분
    private final int DELAY_ADD = 2;
    private final int SCAN_TIME = 15000;
    private final int INIT_SAVETIME = 1;
    private WifiAudioManager wifiAudioManager;
    private WifiNotificationManager wifiNotificationManager;
    private WifiBrightManager brightManager;
    private WifiBluetoothManager bluetoothManager;
    private WifiManager wm;
    private WifiScan wifiScan;
    private Handler scanHandler;
    private int saveTime = INIT_SAVETIME;
    private WifiDataState initData;
    private String lastSSID = "";
    private boolean flag = false;
    private  Realm realm;
    private ArrayList<WifiData> results;
    private int resultsItemNumber;
    private Thread serviceThread;

    @Override
    public void onCreate() {
        super.onCreate();
        SLog.d("Start Service");
        realm = RealmDB.RealmInit(this);
        wifiAudioManager = WifiAudioManager.getInstance();
        wifiNotificationManager = new WifiNotificationManager(this);
        bluetoothManager = new WifiBluetoothManager();
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
        brightManager = new WifiBrightManager(this);
        ServiceEvent.getInstance(this);
        wifiScan = new WifiScan(this, wm, wifiScanResultInterface);
        initData = new WifiDataState();
        scanHandler = new Handler();
        serviceThread =
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkingAddList())
                    wifiScan.Scan();
                else
                    delay();
                scanHandler.postDelayed(this, SCAN_TIME * saveTime);
            }
        });
        serviceThread.start();
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
            HashSet<WifiData> hashSet = new HashSet<>(results);
            results = new ArrayList<>(hashSet);
            if(results.size() != 0) {
                sortPripority(results);
                for(int i = 0 ; i < results.size(); i++) {
                    WifiData wifiData = results.get(i);
                    SLog.d(wifiData.getSSID() + " : " + wifiData.getPripority());
                }
                for (int i = 0; i < results.size(); i++) {
                    WifiData item = results.get(i);

                    SLog.d(lastSSID + " : " + results.get(i+resultsItemNumber).getSSID());
                    if(lastSSID.equals(results.get(i+resultsItemNumber).getSSID()) && flag){
                        SLog.d("이미 연결됨 " + wm.getConnectionInfo().getSSID() + " : " + item.getSSID());
                        delay();
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
                wifiNotificationManager.notification("없음","");
                flag = false;
            }
        }
    }

    private void addItemWifiConnection(ArrayList<WifiData> results){
        SLog.d("Setting");

        if(results.size() == resultsItemNumber)
            resultsItemNumber = 0;

        flag = true;
        SLog.d(results.size() + " : " + resultsItemNumber+"");
        WifiData item = results.get(resultsItemNumber);
        lastSSID = item.getSSID();
        WifiDataState data_state = realm.where(WifiDataState.class).equalTo("BSSID",item.getBSSID()).findFirst();
        setSetting(data_state, item.getSSID());

        setNotification(item.getSSID(), results );
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
        SLog.d("dealy" + saveTime * 2);
        saveTime *= DELAY_ADD;
        if(saveTime == DELAY_MAX*2)
            saveTime = 1;
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
        initData.setBrightSize(brightManager.getBrightSize());
    }

    private void setSetting(WifiDataState data_state, String ssid){
        lastSSID = ssid;
        SLog.d("SetSetting : " + data_state.getWifiState() + " : " +  data_state.getBluetoothState() +" : "+ data_state.getSoundState() + " : " + data_state.getBrightState());
        if(data_state.getWifiState()){
            wifiScan.wifiConnetion(ssid);
        }else{
            wifiScan.wifiState(false);
        }
        if(data_state.getBluetoothState()){
            bluetoothManager.bluetoothEnable(true);
        }else{
            bluetoothManager.bluetoothEnable(false);
        }
        if(data_state.getSoundState()) {
            wifiAudioManager.setSystemVolume(data_state.getSoundSize());
        }
        if(data_state.getBrightState()){
           brightManager.setSettingBright(data_state.getBrightSize());
        }
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
            wifiNotificationManager.notification(itemList.get(resultsItemNumber).getName(), ssid);
        else
            wifiNotificationManager.notification(ssid, itemList, resultsItemNumber);
    }

    @Override
    public void onChangeConnection() {
        resultsItemNumber++;
        addItemWifiConnection(results);
        SLog.d("onChangeConnection : " + resultsItemNumber);
    }

    @Override
    public void onServiceStop() {
        scanHandler.removeCallbacksAndMessages(null);
        wifiNotificationManager.notification("중지","");
        SLog.d("Thread Stop");
    }

    @Override
    public void onServiceStart() {
        flag = false;
        saveTime = INIT_SAVETIME;
        resultsItemNumber = 0;
        scanHandler.postDelayed(serviceThread,0);
        wifiNotificationManager.notification("실행","");
        SLog.d("Thread start");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SLog.d("Service finish");
        if(serviceThread.isAlive())
            serviceThread.stop();
    }

    IBinder mBinder = new ServiceBinder();

    class ServiceBinder extends Binder {
        SystemService getService() { // 서비스 객체를 리턴
            return SystemService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
