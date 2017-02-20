package com.nammu.smartwifi;

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
