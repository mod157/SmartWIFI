package com.nammu.smartwifi;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by SunJae on 2017-02-09.
 */

public class ChangeSetting {
    public static void ChangeAudio(Context context, SeekBar sb_sound, final RadioGroup rg, final RadioButton mute, final RadioButton vibrate, final TextView stateView){
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        sb_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {



            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(mute.isChecked() || vibrate.isChecked()){
                    mute.setChecked(false);
                    vibrate.setChecked(false);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
               // audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, AudioManager.FLAG_PLAY_SOUND);
                stateView.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_sound_vibrate) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    stateView.setText("진동");
                }else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    stateView.setText("무음");
                }
            }
        });
    }

    public static void ChangeBright(ContentResolver cr, final Window window, SeekBar sb_Bright, final TextView stateView){
        try {
            if(Settings.System.getInt(cr,Settings.System.SCREEN_BRIGHTNESS_MODE)!=0 ) {
                Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        sb_Bright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress < 10) {
                    progress = 10;
                    seekBar.setProgress(progress);
                } else if (progress > 250) {
                    progress = 250;
                }
                stateView.setText(progress+"");
                WindowManager.LayoutParams parms = window.getAttributes();
                parms.screenBrightness = (float) progress / 250;
                parms.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                window.setAttributes(parms);
                //직접 적용
                //Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, set_Brite);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
