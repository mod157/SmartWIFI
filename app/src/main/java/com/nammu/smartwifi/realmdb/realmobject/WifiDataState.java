package com.nammu.smartwifi.realmdb.realmobject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by SunJae on 2017-02-08.
 */

public class WifiDataState extends RealmObject{
    @PrimaryKey
    @Required
    private String BSSID;
    private boolean Wifi_State;
    private boolean Bluetooth_State;
    //진동까지 할것인지 포함
    private boolean Sound_State;
    private int Sound_Size;
    private boolean Bright_State;
    private int Bright_Size;
    public void setBSSID(String bssid){
        BSSID = bssid;
    }
    public void setWifiState(boolean wifi_state){
        Wifi_State = wifi_state;
    }
    public void setBluetoothState(boolean bluetooth_state){
        Bluetooth_State = bluetooth_state;
    }
    public void setSoundState(boolean sound_state){
        Sound_State = sound_state;
    }
    public void setSoundSize(int sound_size){
        Sound_Size = sound_size;
    }
    public void setBrightState(boolean bright_state){
        Bright_State = bright_state;
    }
    public void setBrightSize(int bright_size){
        Bright_Size = bright_size;
    }

    public String getBSSID(){return BSSID;}
    public boolean getWifiState(){return Wifi_State;}
    public boolean getBluetoothState(){return Bluetooth_State;}
    public boolean getSoundState(){return Sound_State;}
    public int getSoundSize(){return Sound_Size;}
    public boolean getBrightState(){return Bright_State;}
    public int getBrightSize(){return Bright_Size;}
}
