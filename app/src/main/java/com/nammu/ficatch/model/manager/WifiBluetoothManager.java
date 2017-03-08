package com.nammu.ficatch.model.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public void bluetoothEnable(boolean state){
        if(blueAdapter != null) {
            if (state)
                blueAdapter.enable();
            else
                blueAdapter.disable();
        }
    }

    public void selectDevice(Context context) {
        Set<BluetoothDevice> mDevices = blueAdapter.getBondedDevices();
        final int devicesSize = mDevices.size();

        if (devicesSize == 0) {
            //  페어링 된 장치가 없는 경우
            return;    // 어플리케이션 종료
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("블루투스 장치 선택");


        // 페어링 된 블루투스 장치의 이름 목록 작성
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }
        listItems.add("취소");    // 취소 항목 추가


        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == devicesSize) {
                    // 연결할 장치를 선택하지 않고 '취소'를 누른 경우
                    return;
                } else {
                    // 연결할 장치를 선택한 경우
                    // 선택한 장치와 연결을 시도함
                }
            }
        });

        builder.setCancelable(false);    // 뒤로 가기 버튼 사용 금지
        AlertDialog alert = builder.create();
        alert.show();
    }
    public BluetoothDevice selectDevice(String name) {
        BluetoothDevice selectedDevice = null;
        Set<BluetoothDevice> mDevices = blueAdapter.getBondedDevices();

        for (BluetoothDevice device : mDevices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }
}
