package com.nammu.smartwifi.model.manager;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.nammu.smartwifi.model.OnInterface;
import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.realmdb.realmobject.WifiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Created by SunJae on 2017-02-14.
 */

public class WifiScan {
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
            SLog.d("true");
            wm.startScan();
        } else {
            // 현재 wifi가 꺼져 있으면
            // 항상 검색 허용이 가능한지 확인
            if (Build.VERSION.SDK_INT >= 18 && wm.isScanAlwaysAvailable()) {
                //  4.3 버전 이상인지 체크한다.
                //  항상검색 허용 설정이 활성화상태인지 체크한다.
                SLog.d("false, true");
                wm.startScan();  // 바로 스캔 실행
            } else {
                SLog.d("false, false");
                //나머지는 Wifi를 잠시 켜서 확인한다.
                wm.setWifiEnabled(true);
                wm.startScan();
            }
        }
        SLog.d("startScan");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiR, filter);
    }

    private BroadcastReceiver wifiR = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                SLog.d("BroadCast");
                if(!wifi_state)
                    wm.setWifiEnabled(false);

                scanResultInterface.setScanResult(wm.getScanResults());
                context.unregisterReceiver(wifiR);
            }
        }
    };
    public void wifiState(boolean state){
        wm.setWifiEnabled(state);
    }
    //TODO 대기시간 Callback
    public void wifiConnetion(String ssid){
        SLog.d("WifiConnection");
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
            wifiConnetion(ssid);
        }
    }

    public void sortLevelResult(List<ScanResult> results){
        //Level순으로 정렬 원하는 위치면 다른 Wifi보다 세기가 강할 확률이 높음
        Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return (lhs.level <rhs.level ? 1 : (lhs.level==rhs.level ? 0 : -1));
            }
        };
        Collections.sort(results, comparator);
    }

    public static ArrayList<String> sortList(ArrayList<WifiData> itemList){
        ArrayList<String> ssidList = new ArrayList<>();
        for(int j = 0; j<itemList.size(); j++){ //SSID 중복 제거 (맥주소 통일화)
            WifiData item = itemList.get(j);
            if(item.getSSID().equals(""))
                continue;
            ssidList.add(item.getSSID());
        }
        ssidList = new ArrayList(new HashSet(ssidList));
        return ssidList;
    }
}
