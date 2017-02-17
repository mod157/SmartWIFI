package com.nammu.smartwifi.UI.setdata.interfaces;

import com.nammu.smartwifi.UI.setdata.domain.WifiList_Item;
import com.nammu.smartwifi.realmdb.realmobject.WifiData;

/**
 * Created by SunJae on 2017-02-17.
 */

public class SetInterfaces {
    //SetFragment -> SetActivity로 전달하여 Detail 실행
    public interface OnChangedListener {
        public void onChangeFragment(WifiData data);
    }

    //List클릭시 TextView에 ssid와 bssid 입력
    public interface WifiListClickListener {
        public void WifiListClick(WifiList_Item item);
    }
}
