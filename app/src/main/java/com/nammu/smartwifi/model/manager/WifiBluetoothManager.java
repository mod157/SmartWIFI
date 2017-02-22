package com.nammu.smartwifi.model.manager;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by SunJae on 2017-02-21.
 */

public class WifiBluetoothManager {
    BluetoothAdapter blueAdapter;
    public WifiBluetoothManager(){
        blueAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isEnableBluetooth(){
        if (blueAdapter == null) {
            return false;
        } else {
            return blueAdapter.isEnabled();
        }
    }
}
