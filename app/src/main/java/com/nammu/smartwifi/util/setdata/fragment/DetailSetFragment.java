package com.nammu.smartwifi.util.setdata.fragment;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.model.manager.WifiAudioManager;
import com.nammu.smartwifi.model.manager.WifiBluetoothManager;
import com.nammu.smartwifi.model.manager.WifiBrightManager;
import com.nammu.smartwifi.realmdb.RealmDB;
import com.nammu.smartwifi.realmdb.realmobject.WifiData;
import com.nammu.smartwifi.realmdb.realmobject.WifiDataState;
import com.nammu.smartwifi.util.setdata.SetActivity;
import com.nammu.smartwifi.util.setlist.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by SunJae on 2017-02-09.
 */


public class DetailSetFragment extends Fragment implements SetActivity.ResetFragmentListener {
    private final int WIFI_STATE = 0;
    private final int BLUETOOTH_STATE = 1;
    private final int SOUND_STATE = 2;
    private final int BRIGHT_STATE = 3;
    private final int BRIGHT_DIV = 100;
    private boolean vibrate = false;
    private boolean[] settingstate = new boolean[4];
    private WifiData data;
    private WifiAudioManager wifiAudioManager;
    private WifiBrightManager wifiBrightManager;
    private WifiBluetoothManager wifiBluetoothManager;
    private int init_system_Sound;

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
        switchButtonChange(compoundButton.getId(), isCheck);
        SLog.d("setting value : "  + settingstate.toString());
    }

    @OnClick(R.id.btn_detail_Success)
    public void SuccessClick(View view){
        RealmDB.InsertOrUpdate_Data(getContext(),data);
        WifiDataState dataState = getDataState();
        RealmDB.insertOrUpdate_Data_State(getContext(), dataState);
        //오디오 초기화
        wifiAudioManager.setSystemVolume(init_system_Sound);

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
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            SLog.d("Bundler get Parcelable");
            data = bundle.getParcelable("WifiData_Bundle");
        }
        wifiAudioManager = WifiAudioManager.getInstance();
        wifiBrightManager = new WifiBrightManager(getContext());
        wifiBluetoothManager = new WifiBluetoothManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_detail, container, false );
        ButterKnife.bind(this, view);
        init_system_Sound = wifiAudioManager.getSystemVolume();
        sb_sound.setMax(wifiAudioManager.getSystemVolumeMax());
        SLog.d("Crete OK : " + init_system_Sound);
        if (MainActivity.VIEW_EDIT) {
            editStatus();
        }else{
            setCurrentStatus();
            data.setisPlay(true);
        }
        sb_sound.setOnSeekBarChangeListener(audioSeekBarChangeListener);
        sb_bright.setOnSeekBarChangeListener(brightSeekBarChangeListener);
        cb_sound_vibrate.setOnCheckedChangeListener(vibrateButtonClickListener);
        return view;
    }

    private void editStatus(){
        SLog.d(data.getSSID() + " : " + data.getBSSID());
        Realm realm = RealmDB.RealmInit(getContext());
        WifiDataState state = realm.where(WifiDataState.class).equalTo("BSSID", data.getBSSID()).findFirst();
        try {
           initialSetView(state);
        }catch(Exception e){
            SLog.d("realm error");
            e.printStackTrace();
        }
    }

    //현재 상태
    private void  setCurrentStatus() {
        WifiDataState state = new WifiDataState();
        state.setWifiState(true);
        state.setBluetoothState(wifiBluetoothManager.isEnableBluetooth());
        state.setSoundState(true);
        state.setBrightState(true);
        state.setSoundSize(wifiAudioManager.getRingerModeInt());
        state.setBrightSize(wifiBrightManager.getBrightSize());
        initialSetView(state);
    }

    private void initialSetView(WifiDataState state){
        sw_wifi.setChecked(state.getWifiState());
        sw_bluetooth.setChecked(state.getBluetoothState());
        boolean soundCheck = state.getSoundState();
        boolean brightCheck = state.getBrightState();

        if (soundCheck) {
            int soundSize = state.getSoundSize();
            sw_sound.setChecked(soundCheck);
            if (soundSize == 0) {
                sb_sound.setProgress(0);
                tv_sound_state_value.setText(getString(R.string.detail_Sound_Mute));
            } else if (soundSize > 0) {
                sb_sound.setProgress(soundSize);
                tv_sound_state_value.setText(soundSize + "");
            } else {
                sb_sound.setProgress(0);
                cb_sound_vibrate.setChecked(true);
                tv_sound_state_value.setText(getString(R.string.detail_Sound_Vibrate));
            }
        }

        if (brightCheck) {
            sw_bright.setChecked(brightCheck);
            sb_bright.setProgress(state.getBrightSize());
            tv_bright_state_value.setText(((float) (state.getBrightSize()) / 250) * 100 + "%");
        }
    }

    private SeekBar.OnSeekBarChangeListener audioSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            setChangeAudio(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private void setChangeAudio(int progress){
        if(cb_sound_vibrate.isChecked()){
            cb_sound_vibrate.setChecked(false);
            wifiAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
        //TODO 변경 될 때마다 소리 알람
        wifiAudioManager.setSystemVolume(progress);
        tv_sound_state_value.setText(progress+"");
    }

    private void setChangeBright(SeekBar seekBar, int progress){
        Window window = getActivity().getWindow();
        if (progress < WifiBrightManager.BRIGHT_MIN) {
            progress = WifiBrightManager.BRIGHT_MIN;
            seekBar.setProgress(progress);
        } else if (progress > WifiBrightManager.BRIGHT_MAX) {
            progress = WifiBrightManager.BRIGHT_MAX;
        }
        float bright_value = (float)progress / WifiBrightManager.BRIGHT_MAX;
        tv_bright_state_value.setText((int)(bright_value * BRIGHT_DIV) + "%");

        wifiBrightManager.setWindowBright(window, bright_value);
    }

    SeekBar.OnSeekBarChangeListener brightSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            setChangeBright(seekBar, progress);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };


    private WifiDataState getDataState(){
        int sound_size = 0;
        int bright_size = 0;
        if(settingstate[SOUND_STATE]){
            if(vibrate) {
                sound_size = -1;
            }else{
                sound_size = sb_sound.getProgress();
            }
        }

        if(settingstate[BRIGHT_STATE]){
            bright_size = sb_bright.getProgress();
        }
        settingstate[WIFI_STATE] = sw_wifi.isChecked();

        WifiDataState dataState = new WifiDataState(data.getBSSID(),settingstate[WIFI_STATE],
                settingstate[BLUETOOTH_STATE],settingstate[SOUND_STATE],
                settingstate[BRIGHT_STATE],sound_size,bright_size);

        return dataState;
    }

    CompoundButton.OnCheckedChangeListener vibrateButtonClickListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                SLog.d("진동 Check");
                wifiAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                sb_sound.setProgress(0);
                cb_sound_vibrate.setChecked(true);
                tv_sound_state_value.setText("진동");
            }
        }
    };

    private void switchButtonChange(int id, boolean isCheck){
        switch(id){
            case R.id.sw_wifi:
                settingstate[WIFI_STATE] = isCheck;
                break;
            case R.id.sw_bluetooth:
                settingstate[BLUETOOTH_STATE] = isCheck;
                break;
            case R.id.sw_sound:
                settingstate[SOUND_STATE] = isCheck;
                if(isCheck){
                    linear_sound.setVisibility(View.VISIBLE);
                }else
                    linear_sound.setVisibility(View.GONE);
                break;
            case R.id.sw_bright:
                settingstate[BRIGHT_STATE] = isCheck;
                if(isCheck){
                    linear_bright.setVisibility(View.VISIBLE);
                }else
                    linear_bright.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void updateView() {
        if (MainActivity.VIEW_EDIT) {
            editStatus();
        }else{
            setCurrentStatus();
        }
    }



    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
