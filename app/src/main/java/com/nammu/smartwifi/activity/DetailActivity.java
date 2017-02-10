package com.nammu.smartwifi.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.classdata.ChangeSetting;
import com.nammu.smartwifi.realmdb.RealmDB;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {
    private String TAG = "##### SmartWIFI detail";
    private final int WIFI_STATE = 0;
    private final int BLUETOOTH_STATE = 1;
    private final int SOUND_STATE = 2;
    private final int BRIGHT_STATE = 3;
    private boolean vibrate = false;
    private boolean[] setting_state = new boolean[4];
    private AudioManager audioManager;
    private int init_sound;
    private int init_bright;

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
    @BindView(R.id.rb_sound_mute)
    RadioButton rb_sound_mute;
    @BindView(R.id.rb_sound_vibrate)
    RadioButton rb_sound_vibrate;
    @BindView(R.id.rbGroup)
    RadioGroup rbGroup;
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
        Intent getDataIntent = getIntent();
        String name = getDataIntent.getStringExtra("View_name");
        String ssid = getDataIntent.getStringExtra("View_ssid");
        String bssid = getDataIntent.getStringExtra("View_bssid");
        int pripority = getDataIntent.getIntExtra("View_Priority", 0);

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
        RealmDB.InsertOrUpdate_Data(this, name, ssid, bssid, pripority);
        //context, bssid, wifi_state, bluetooth_state, sound_state, sound_size, Bright_state, bright_size
        RealmDB.insertOrUpdate_Data_State(this, bssid, setting_state[WIFI_STATE], setting_state[BLUETOOTH_STATE],
                setting_state[SOUND_STATE], sound_size, setting_state[BRIGHT_STATE], bright_size);
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.btn_detail_back)
    public void backClick(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.support.v7.app.ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        //초기화
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        sb_sound.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        sb_sound.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
       // InitData();
        SetCurrentStatus();
    }

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
                rb_sound_mute.setChecked(true);
                break;
            //진동
            case AudioManager.RINGER_MODE_VIBRATE:
                tv_sound_state_value.setText("진동");
                sb_sound.setProgress(0);
                rb_sound_vibrate.setChecked(true);
                break;
            case  AudioManager.RINGER_MODE_NORMAL:
                tv_sound_state_value.setText(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)+"");
                break;
        }
        ChangeSetting.ChangeAudio(getApplicationContext(), sb_sound, rbGroup, rb_sound_vibrate, rb_sound_mute, tv_sound_state_value);
        ChangeSetting.ChangeBright(this.getContentResolver(), getWindow(), sb_bright, tv_bright_state_value);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        Log.d("클릭", "클릭됨");
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "Menu Click");
        SetCurrentStatus();
        return super.onOptionsItemSelected(item);
    }
}
