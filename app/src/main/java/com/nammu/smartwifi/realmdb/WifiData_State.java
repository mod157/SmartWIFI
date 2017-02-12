package com.nammu.smartwifi.realmdb;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by SunJae on 2017-02-08.
 */

public class WifiData_State extends RealmObject{

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
    public void setWifi_State(boolean wifi_state){
        Wifi_State = wifi_state;
    }
    public void setBluetooth_State(boolean bluetooth_state){
        Bluetooth_State = bluetooth_state;
    }
    public void setSound_State(boolean sound_state){
        Sound_State = sound_state;
    }
    public void setSound_Size(int sound_size){
        Sound_Size = sound_size;
    }
    public void setBright_State(boolean bright_state){
        Bright_State = bright_state;
    }
    public void setBright_Size(int bright_size){
        Bright_Size = bright_size;
    }

    public String getBSSID(){return BSSID;}
    public boolean getWifi_State(){return Wifi_State;}
    public boolean getBluetooth_State(){return Bluetooth_State;}
    public boolean getSound_State(){return Sound_State;}
    public int getSound_Size(){return Sound_Size;}
    public boolean getBright_State(){return Bright_State;}
    public int getBright_Size(){return Bright_Size;}
}
