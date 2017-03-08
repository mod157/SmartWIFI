package com.nammu.ficatch.realmdb;

import android.content.Context;
import android.util.Log;

import com.nammu.ficatch.realmdb.realmobject.WifiData;
import com.nammu.ficatch.realmdb.realmobject.WifiDataState;

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

    public static void InsertOrUpdate_Data(Context context,final WifiData data){
        //name, ssid. bssid. pripority, isPlay
        Realm realm = RealmInit(context);
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

    public static void insertOrUpdate_Data_State(Context context,final WifiDataState data){
        //bssid, wifi_state, bluetooth_state, sound_state, sound_size, Bright_state, bright_size
        Realm realm = RealmInit(context);
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
                WifiDataState state = realm.where(WifiDataState.class).equalTo("BSSID",data.getBSSID()).findFirst();
                data.deleteFromRealm();
                state.deleteFromRealm();
            }
        });
    }
}
