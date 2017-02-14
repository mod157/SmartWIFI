package com.nammu.smartwifi.object;

import com.nammu.smartwifi.realmdb.WifiData;
import com.nammu.smartwifi.realmdb.WifiData_State;

import java.util.ArrayList;

/**
 * Created by SunJae on 2017-02-14.
 */

public class addWifiDataList {
    ArrayList<WifiData> wifiDatas;
    ArrayList<WifiData_State> wifiData_states;

    public void setWifiDatas(ArrayList<WifiData> datas){
        wifiDatas = datas;
    }
    public void setWifiData_states(ArrayList<WifiData_State> data_states){
        wifiData_states = data_states;
    }

    public ArrayList<WifiData> getWifiDatas(){
        return wifiDatas;
    }

    public ArrayList<WifiData_State> getWifiData_states(){
        return wifiData_states;
    }
}
