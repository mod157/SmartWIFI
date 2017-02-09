package com.nammu.smartwifi;

/**
 * Created by SunJae on 2017-02-08.
 */

public class WifiItem {
    private String wifi_Name;
    private String wifi_SSID;
    private String wifi_BSSID;
    private boolean wifi_state;

    WifiItem(String name, String ssid, String bssid, boolean isState){
        wifi_Name = name;
        wifi_SSID = ssid;
        wifi_BSSID = bssid;
        wifi_state = isState;
    }

    public String getWifi_Name(){
        return wifi_Name;
    }

    public String getWifi_SSID(){
        return wifi_SSID;
    }

    public boolean isWifi_state(){
        return wifi_state;
    }
}
