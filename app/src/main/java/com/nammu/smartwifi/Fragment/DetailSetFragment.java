package com.nammu.smartwifi.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.activity.MainActivity;
import com.nammu.smartwifi.realmdb.RealmDB;
import com.nammu.smartwifi.realmdb.WifiData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by SunJae on 2017-02-09.
 */


public class DetailSetFragment extends Fragment  {
    private String TAG = "##### SmartWIFI detail";
    private final int WIFI_STATE = 0;
    private final int BLUETOOTH_STATE = 1;
    private final int SOUND_STATE = 2;
    private final int BRIGHT_STATE = 3;
    private boolean vibrate = false;
    private boolean[] setting_state = new boolean[4];
    private WifiData data;
    private AudioManager audioManager;
    private int init_system_Sound;
    private Context detail_Context;
    @BindView(R.id.linear_sound)
    LinearLayout linear_sound;
    @BindView(R.id.linear_bright)
    LinearLayout linear_bright;
    @BindView(R.id.tv_bright_state_value)
    TextView tv_bright_state_value;
    @BindView(R.id.tv_sound_state_value)
    TextView tv_sound_state_value;
    @BindView(R.id.sw_bluetooth)
    Switch sw_bluetooth;
    @BindView(R.id.sw_bright)
    Switch sw_bright;
    @BindView(R.id.sw_sound)
    Switch sw_sound;
    @BindView(R.id.sw_wifi)
    Switch sw_wifi;
    @BindView(R.id.sb_sound)
    SeekBar sb_sound;
    @BindView(R.id.sb_bright)
    SeekBar sb_bright;
    @BindView(R.id.cb_sound_vibrate)
    CheckBox cb_sound_vibrate;

    @OnCheckedChanged({R.id.sw_bluetooth, R.id.sw_bright, R.id.sw_sound, R.id.sw_wifi})
    public void checkedChange(CompoundButton compoundButton, boolean isCheck){
        switch(compoundButton.getId()){
            case R.id.sw_wifi:
                setting_state[WIFI_STATE] = isCheck;
                break;
            case R.id.sw_bluetooth:
                setting_state[BLUETOOTH_STATE] = isCheck;
                break;
            case R.id.sw_sound:
                setting_state[SOUND_STATE] = isCheck;
                if(isCheck){
                    linear_sound.setVisibility(View.VISIBLE);
                }else
                    linear_sound.setVisibility(View.GONE);
                break;
            case R.id.sw_bright:
                setting_state[BRIGHT_STATE] = isCheck;
                if(isCheck){
                    linear_bright.setVisibility(View.VISIBLE);
                }else
                    linear_bright.setVisibility(View.GONE);
                break;
        }
        Log.e(TAG, "setting value : "  + setting_state.toString());
    }

    @OnClick(R.id.btn_detail_Success)
    public void SuccessClick(View view){
        //WifiData Set
        String name = data.getName();
        String ssid = data.getSSID();
        String bssid = data.getBSSID();
        int pripority = data.getPripority();

        //WifiData_state Data
        int sound_size = 0;
        int bright_size = 0;
        if(setting_state[SOUND_STATE]){
            if(vibrate) {
                sound_size = -1;
            }else{
                sound_size = sb_sound.getProgress();
            }
        }

        if(setting_state[BLUETOOTH_STATE]){
            bright_size = sb_bright.getProgress();
        }

        //context, name, ssid. bssid. pripority, isPlay
        RealmDB.InsertOrUpdate_Data(getContext(), name, ssid, bssid, pripority);
        //context, bssid, wifi_state, bluetooth_state, sound_state, sound_size, Bright_state, bright_size
        RealmDB.insertOrUpdate_Data_State(getContext(), bssid, setting_state[WIFI_STATE], setting_state[BLUETOOTH_STATE],
                setting_state[SOUND_STATE], sound_size, setting_state[BRIGHT_STATE], bright_size);
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public static Fragment newInstance(WifiData data){
        Fragment fragment = new DetailSetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("WifiData_Bundle",data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_detail, container, false );
        ButterKnife.bind(this, view);
        detail_Context = view.getContext();
        Log.e(TAG,"Crete OK");
        Bundle bundle = getArguments();
        if(bundle != null) {
            Log.e(TAG, "Bundler get Parcelable");
            data = bundle.getParcelable("WifiData_Bundle");
        }
        SetCurrentStatus();
        return view;
    }

    private void  SetCurrentStatus() {
        audioManager = (AudioManager) detail_Context.getSystemService(Context.AUDIO_SERVICE);
        init_system_Sound = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();

        if (blueAdapter == null) {
            sw_bluetooth.setChecked(false);
        } else {
            sw_bluetooth.setChecked(blueAdapter.isEnabled());
        }
        sw_sound.setChecked(true);
        linear_sound.setVisibility(View.VISIBLE);
        sw_bright.setChecked(true);
        linear_bright.setVisibility(View.VISIBLE);

        switch(audioManager.getRingerMode()) {
            //무음
            case AudioManager.RINGER_MODE_SILENT:
                //# string으로 변경
                tv_sound_state_value.setText("무음");
                sb_sound.setProgress(0);
                cb_sound_vibrate.setChecked(false);
                break;
            //진동
            case AudioManager.RINGER_MODE_VIBRATE:
                tv_sound_state_value.setText("진동");
                sb_sound.setProgress(0);
                cb_sound_vibrate.setChecked(true);
                break;
            case  AudioManager.RINGER_MODE_NORMAL:
                tv_sound_state_value.setText(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)+"");
                cb_sound_vibrate.setChecked(false);
                break;
        }
        ChangeAudio();
        ChangeBright();
    }

    public void ChangeAudio(){
        sb_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(cb_sound_vibrate.isChecked()){
                    cb_sound_vibrate.setChecked(false);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                //TODO 변경 될 때마다 소리 알람
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, AudioManager.FLAG_PLAY_SOUND);
                tv_sound_state_value.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        cb_sound_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                tv_sound_state_value.setText("진동");
            }
        });
    }

    public void ChangeBright(){
        try {
            if(Settings.System.getInt(detail_Context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE)!=0 ) {
                Settings.System.putInt(detail_Context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        sb_bright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Window window = getActivity().getWindow();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress < 10) {
                    progress = 10;
                    seekBar.setProgress(progress);
                } else if (progress > 250) {
                    progress = 250;
                }
                tv_bright_state_value.setText(progress+"");

                //TODO 객체화
                WindowManager.LayoutParams parms =  window.getAttributes();
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
    @Override
    public void onDestroy(){
        super.onDestroy();
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, init_system_Sound, AudioManager.FLAG_PLAY_SOUND);
    }
}
