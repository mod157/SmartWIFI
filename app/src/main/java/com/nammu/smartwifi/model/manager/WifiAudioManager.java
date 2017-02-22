package com.nammu.smartwifi.model.manager;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by SunJae on 2017-02-18.
 */

public class WifiAudioManager {
    private static WifiAudioManager wifiAudioManager;
    private AudioManager audioManager;

    private WifiAudioManager(Context context){
        if(context != null)
          loaderManager(context);
    }

    public void loaderManager(Context context){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static WifiAudioManager getInstance(){
       return getInstance(null);
    }

    public static WifiAudioManager getInstance(Context context) {
        if(wifiAudioManager == null && context != null)
            wifiAudioManager = new WifiAudioManager(context);
        return wifiAudioManager;
    }

    public void setRingerMode(int mode){
          audioManager.setRingerMode(mode);
    }
    public int getRingerMode(){
        return audioManager.getRingerMode();
    }
    public int getRingerModeInt(){
        int modeInt = 0;
        switch(wifiAudioManager.getRingerMode()) {
            //무음
            case AudioManager.RINGER_MODE_SILENT: modeInt = 0;
            //진동
            case AudioManager.RINGER_MODE_VIBRATE:modeInt = -1;
            case  AudioManager.RINGER_MODE_NORMAL:modeInt = getSystemVolume();
        }
        return modeInt;
    }

    public int getSystemVolume(){
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }
    public void setSystemVolume(int volume){
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, AudioManager.FLAG_PLAY_SOUND);
    }
    public int getSystemVolumeMax(){
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }
}
