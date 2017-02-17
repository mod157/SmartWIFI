package com.nammu.smartwifi.realmdb;

import android.content.Context;
import android.util.Log;

import com.nammu.smartwifi.realmdb.realmobject.WifiData;
import com.nammu.smartwifi.realmdb.realmobject.WifiData_State;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by SunJae on 2017-01-26.
 */

public class RealmDB {

    public static Realm RealmInit(Context context){
        Realm realm = null;
        realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
        return realm;
    }

    public static void InsertOrUpdate_Data(Context context, final WifiData data, final boolean isCheck){
        Realm realm = RealmInit(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                data.setisPlay(isCheck);
                realm.copyToRealmOrUpdate(data);
                Log.e("##### Change isPlay", isCheck+"");
            }
        });
        realm.close();
    }

    public static void InsertOrUpdate_Data(Context context, String name, String ssid, String bssid, int pripority){
        //name, ssid. bssid. pripority, isPlay
        Realm realm = RealmInit(context);
        final WifiData data = new WifiData();
        data.setName(name);
        data.setSSID(ssid);
        data.setBSSID(bssid);
        data.setPripority(pripority);
        data.setisPlay(true);
        //같은 primary 키가 존재한다면 데이터만 변경
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
       // realm.commitTransaction();
        realm.close();
    }

    public static void insertOrUpdate_Data_State(Context context, final String bssid, boolean wifi_state, boolean bluetooth_state, boolean sound_state,
                                                 int sound_size, boolean bright_state, int bright_size){
        //bssid, wifi_state, bluetooth_state, sound_state, sound_size, Bright_state, bright_size
        Realm realm = RealmInit(context);
        final WifiData_State data = new WifiData_State();
        data.setBSSID(bssid);
        data.setWifi_State(wifi_state);
        data.setBluetooth_State(bluetooth_state);
        data.setSound_State(sound_state);
        data.setSound_Size(sound_size);
        data.setBright_State(bright_state);
        data.setBright_Size(bright_size);
        //같은 primary 키가 존재한다면 데이터만 변경
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
        realm.close();
    }

    public static void deleteData(Context context, final WifiData data){
        Realm realm = RealmInit(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                WifiData_State state = realm.where(WifiData_State.class).equalTo("BSSID",data.getBSSID()).findFirst();
                data.deleteFromRealm();
                state.deleteFromRealm();
            }
        });
    }
}
