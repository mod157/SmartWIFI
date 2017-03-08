package com.nammu.ficatch.model;

/**
 * Created by SunJae on 2017-02-13.
 */

public class WifiListItem {
    String ssid;
    String bssid;
    int level;
    boolean save;

    public void setSSID(String ssid){
        this.ssid = ssid;
    }
    public void setBSSID(String bssid){
        this.bssid = bssid;
    }
    public void setLevel(int level){
        this.level = level;
    }
    public void setSave(boolean save){
        this.save = save;
    }

    public String getSSID(){
        return ssid;
    }

    public String getBSSID(){
        return bssid;
    }

    public int getLevel(){
        return level;
    }
    public boolean getSave(){
        return save;
    }
}
