package com.nammu.smartwifi.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.nammu.smartwifi.interfaces.OnInterface;
import com.nammu.smartwifi.object.WifiScan;
import com.nammu.smartwifi.realmdb.RealmDB;
import com.nammu.smartwifi.realmdb.WifiData;

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
    private int scanTimer = 3000;


    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiScan = new WifiScan(this, wm, wifiScanResultInterface);
        scanHandler = new Handler();
        scanStart.run();
    }

    Runnable scanStart = new Runnable() {
        @Override
        public void run() {
           if(startScan > saveTime){
               wifiScan.Scan();
           }else
               startScan++;
            scanHandler.postDelayed(this, scanTimer);
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
        List apList = scanList;
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

            for(int i = 0; i < size; i++) {
                sr = scanList.get(i);
                //addWifiDataList wifidata = new addWifiDataList();
                //ArrayList<WifiData> data = wifidata.getWifiDatas();
                Realm realm = RealmDB.RealmInit(this);
                WifiData itemResult = realm.where(WifiData.class).equalTo("SSID", sr.SSID).findFirst();

                //존재하지 않으면 다음 SSID
                if (itemResult == null)
                    continue;
                else{

                }
                String bssid = itemResult.getBSSID();
                String[] bssids = bssid.split("@");
                for(int j = 0; j < bssids.length; j++){
                    if(sr.BSSID.equals(bssids[j])){

                    }
                }

            }

        }

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
