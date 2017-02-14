package com.nammu.smartwifi.realmdb;

import io.realm.RealmObject;

/**
 * Created by SunJae on 2017-02-14.
 */

public class AppEnv extends RealmObject {
    int scanTime;
    int saveTime;
    boolean isNoti;

    public void setScanTime(int scanTime){
        this.scanTime = scanTime;
    }

    public void setSaveTime(int saveTime){
        this.saveTime = saveTime;
    }

    public void setisNoti(boolean noti){
        isNoti = noti;
    }
}
