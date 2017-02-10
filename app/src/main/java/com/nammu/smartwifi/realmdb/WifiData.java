package com.nammu.smartwifi.realmdb;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by SunJae on 2017-02-06.
 */


public class WifiData extends RealmObject {
    private String Name;
    private String SSID;
    @PrimaryKey
    @Required
    private String BSSID;
    private int Pripority;
    private boolean isPlay;

    public void setName(String name){
        Name = name;
    }
    public void setSSID(String ssid){
        SSID = ssid;
    }
    public void setBSSID(String bssid){
        BSSID = bssid;
    }
    public void setPripority(int pripority){Pripority = pripority;}
    public void setisPlay(boolean isPlay){this.isPlay = isPlay;}
    public String getName(){return Name;}
    public String getSSID() {return SSID;}
    public String getBSSID(){return BSSID;}
    public boolean getisPlay(){return isPlay;}

}
