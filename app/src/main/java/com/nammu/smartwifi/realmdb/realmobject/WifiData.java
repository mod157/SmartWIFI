package com.nammu.smartwifi.realmdb.realmobject;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by SunJae on 2017-02-06.
 */


public class WifiData extends RealmObject implements Parcelable {
    private String Name;
    private String SSID;
    @PrimaryKey
    @Required
    private String BSSID;
    private int Pripority;
    private boolean isPlay;

    public void setName(String name){
        Name = name;
    }
    public void setSSID(String ssid){
        SSID = ssid;
    }
    public void setBSSID(String bssid){
        BSSID = bssid;
    }
    public void setPripority(int pripority){Pripority = pripority;}
    public void setisPlay(boolean isPlay){this.isPlay = isPlay;}
    public String getName(){return Name;}
    public String getSSID() {return SSID;}
    public String getBSSID(){return BSSID;}
    public boolean getisPlay(){return isPlay;}
    public int getPripority(){return Pripority;}
    public WifiData(){}

    protected WifiData (Parcel pl){
        Name = pl.readString();
        SSID = pl.readString();
        BSSID = pl.readString();
        Pripority = pl.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeString(SSID);
        parcel.writeString(BSSID);
        parcel.writeInt(Pripority);
    }

    public static final Parcelable.Creator<WifiData> CREATOR = new Creator<WifiData>() {
        @Override
        public WifiData createFromParcel(Parcel parcel) {
            return new WifiData(parcel);
        }

        @Override
        public WifiData[] newArray(int i) {
            return new WifiData[i];
        }
    };
}
