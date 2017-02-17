package com.nammu.smartwifi.UI.setdata.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
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
import com.nammu.smartwifi.UI.setlist.MainActivity;
import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.realmdb.RealmDB;
import com.nammu.smartwifi.realmdb.realmobject.WifiData;
import com.nammu.smartwifi.realmdb.realmobject.WifiData_State;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by SunJae on 2017-02-09.
 */


public class DetailSetFragment extends Fragment  {
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
        SLog.d("setting value : "  + setting_state.toString());
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

        if(setting_state[BRIGHT_STATE]){
            bright_size = sb_bright.getProgress();
        }
        setting_state[WIFI_STATE] = sw_wifi.isChecked();
        //context, name, ssid. bssid. pripority, isPlay
        RealmDB.InsertOrUpdate_Data(getContext(), name, ssid, bssid, pripority);
        //context, bssid, wifi_state, bluetooth_state, sound_state, sound_size, Bright_state, bright_size
        RealmDB.insertOrUpdate_Data_State(getContext(), bssid, setting_state[WIFI_STATE], setting_state[BLUETOOTH_STATE],
                setting_state[SOUND_STATE], sound_size, setting_state[BRIGHT_STATE], bright_size);
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        //오디오 초기화
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, init_system_Sound, AudioManager.FLAG_PLAY_SOUND);
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
        audioManager = (AudioManager) detail_Context.getSystemService(Context.AUDIO_SERVICE);
        init_system_Sound = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        sb_sound.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        SLog.d("Crete OK");
        Bundle bundle = getArguments();
        if (bundle != null) {
            SLog.d("Bundler get Parcelable");
            data = bundle.getParcelable("WifiData_Bundle");
        }
        if (MainActivity.VIEW_EDIT) {
            EditStatus();
        }else{
            SetCurrentStatus();
        }


        ChangeAudio();
        ChangeBright();
        return view;
    }

    private void EditStatus(){
        SLog.d(data.getSSID() + " : " + data.getBSSID());
        Realm realm = RealmDB.RealmInit(getContext());
        WifiData_State state = realm.where(WifiData_State.class).equalTo("BSSID", data.getBSSID()).findFirst();
        try {
            sw_wifi.setChecked(state.getWifi_State());
            sw_bluetooth.setChecked(state.getBluetooth_State());
            boolean soundCheck = state.getSound_State();
            boolean brightCheck = state.getBright_State();

            if (soundCheck) {
                int soundSize = state.getSound_Size();
                sw_sound.setChecked(soundCheck);
                if (soundSize == 0) {
                    sb_sound.setProgress(0);
                    tv_sound_state_value.setText(getString(R.string.detail_Sound_Mute));
                } else if (soundSize > 0) {
                    sb_sound.setProgress(soundSize);
                    tv_sound_state_value.setText(soundSize + "");
                } else {
                    sb_sound.setProgress(0);
                    tv_sound_state_value.setText(getString(R.string.detail_Sound_Vibrate));
                }
            }

            if (brightCheck) {
                sw_bright.setChecked(brightCheck);
                sb_bright.setProgress(state.getBright_Size());
                tv_bright_state_value.setText(((float) (state.getBright_Size()) / 250) * 100 + "%");
            }

        }catch(Exception e){
            SLog.d("realm error");
            e.printStackTrace();
        }
    }

    //현재 상태
    private void  SetCurrentStatus() {
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
                tv_sound_state_value.setText(init_system_Sound+"");
                sb_sound.setProgress(init_system_Sound);
                cb_sound_vibrate.setChecked(false);
                break;
        }
        try {
            if(Settings.System.getInt(detail_Context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE)!=0 ) {
                Settings.System.putInt(detail_Context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }
            int bright = Settings.System.getInt(detail_Context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
            sb_bright.setProgress (bright);
            tv_bright_state_value.setText((float)bright/250 * 100 + "%");
            SLog.d(bright/250 * 100 + "%");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
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
                if(isChecked) {
                    SLog.d("진동 Check");
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    sb_sound.setProgress(0);
                    cb_sound_vibrate.setChecked(isChecked);
                    tv_sound_state_value.setText("진동");
                }
            }
        });
    }

    public void ChangeBright(){
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
                float bright_value = (float)progress / 250;

                //TODO 첫째자리만
                tv_bright_state_value.setText(bright_value * 100 + "%");

                WindowManager.LayoutParams parms =  window.getAttributes();
                parms.screenBrightness = bright_value;
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

    public interface ResetFragmentListener{
        public void updateView();
    }

    ResetFragmentListener resetFragmentListener = new ResetFragmentListener() {
        @Override
        public void updateView() {
            if (MainActivity.VIEW_EDIT) {
                EditStatus();
            }else{
                SetCurrentStatus();
            }
        }
    };


    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
