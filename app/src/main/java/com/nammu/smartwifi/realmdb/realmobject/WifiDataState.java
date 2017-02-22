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
    private boolean WifiState;
    private boolean BluetoothState;
    //진동까지 할것인지 포함
    private boolean SoundState;
    private int SoundSize;
    private boolean BrightState;
    private int BrightSize;



    public WifiDataState(){}
    public WifiDataState(String BSSID, boolean WIFI_STATE, boolean BLUETOOTH_STATE,
                              boolean SOUND_STATE, boolean BRIGHT_STATE, int soundSize, int brightSize){
        this.BSSID = BSSID;
        WifiState = WIFI_STATE;
        BluetoothState = BLUETOOTH_STATE;
        SoundState = SOUND_STATE;
        BrightState = BRIGHT_STATE;
        SoundSize = soundSize;
        BrightSize = brightSize;
    }

    public void setBSSID(String bssid){
        BSSID = bssid;
    }
    public void setWifiState(boolean wifi_state){
        WifiState = wifi_state;
    }
    public void setBluetoothState(boolean bluetooth_state){
        BluetoothState = bluetooth_state;
    }
    public void setSoundState(boolean sound_state){
        SoundState = sound_state;
    }
    public void setSoundSize(int sound_size){
        SoundSize = sound_size;
    }
    public void setBrightState(boolean bright_state){
        BrightState = bright_state;
    }
    public void setBrightSize(int bright_size){
        BrightSize = bright_size;
    }

    public String getBSSID(){return BSSID;}
    public boolean getWifiState(){return WifiState;}
    public boolean getBluetoothState(){return BluetoothState;}
    public boolean getSoundState(){return SoundState;}
    public int getSoundSize(){return SoundSize;}
    public boolean getBrightState(){return BrightState;}
    public int getBrightSize(){return BrightSize;}
}
