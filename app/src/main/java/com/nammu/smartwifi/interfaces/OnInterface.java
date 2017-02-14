package com.nammu.smartwifi.interfaces;

import com.nammu.smartwifi.object.WifiList_Item;
import com.nammu.smartwifi.realmdb.WifiData;

/**
 * Created by SunJae on 2017-02-14.
 */

public class OnInterface {
    //SetFragment -> SetActivity로 전달하여 Detail 실행
    public interface OnChangedListener {
        public void onChangeFragment(WifiData data);
    }

    //List클릭시 TextView에 ssid와 bssid 입력
    public interface WifiListClickListener {
        public void WifiListClick(WifiList_Item item);
    }
}
