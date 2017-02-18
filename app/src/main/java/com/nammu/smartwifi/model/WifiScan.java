package com.nammu.smartwifi.model;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

/**
 * Created by SunJae on 2017-02-14.
 */

public class WifiScan {
    private String TAG = "##### WIFISCAN";
    private WifiManager wm;
    private boolean wifi_state;
    private Context context;
    private OnInterface.WifiScanResultInterface scanResultInterface;
    private List<WifiConfiguration> configNetworkList;


    public WifiScan(Context context, WifiManager wm, OnInterface.WifiScanResultInterface scan){
        this.wm = wm;
        this.context = context;
        scanResultInterface = scan;
    }

    public List<WifiConfiguration> configScan() {
        boolean wifiEnabled = wm.isWifiEnabled();
        if (wifiEnabled)
            configNetworkList = getConfiguredNetworks();
        else {
            wm.setWifiEnabled(true);
            configNetworkList = getConfiguredNetworks();
        }
        if(wm.isWifiEnabled() != wifiEnabled)
            wm.setWifiEnabled(!wm.isWifiEnabled());
        return configNetworkList;
    }

    private List<WifiConfiguration> getConfiguredNetworks() {
        try {
            return wm.getConfiguredNetworks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void Scan() {
        //TODO 3초가 걸림
        wifi_state = wm.isWifiEnabled();
        if (wifi_state) {
            // 켜져 있는 상태면 바로 스캔
            wm.startScan();
        } else {
            // 현재 wifi가 꺼져 있으면
            // 항상 검색 허용이 가능한지 확인
            if (Build.VERSION.SDK_INT >= 18 && wm.isScanAlwaysAvailable()) {
                //  4.3 버전 이상인지 체크한다.
                //  항상검색 허용 설정이 활성화상태인지 체크한다.
                wm.startScan();  // 바로 스캔 실행
            } else {
                //나머지는 Wifi를 잠시 켜서 확인한다.
                wm.setWifiEnabled(true);
                wm.startScan();
            }
        }
        Log.e(TAG, "startScan");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiR, filter);
    }

    private BroadcastReceiver wifiR = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                Log.e(TAG, "BroadCast");
                if(!wifi_state)
                    wm.setWifiEnabled(false);
                scanResultInterface.setScanResult(wm.getScanResults());
                context.unregisterReceiver(wifiR);
            }
        }
    };
}
