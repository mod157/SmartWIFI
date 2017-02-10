package com.nammu.smartwifi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nammu.smartwifi.R;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActivity extends AppCompatActivity {
    private String TAG = "##### SmartWIFI add";
    @BindView(R.id.et_add_name)
    EditText et_add_name;
    @BindView(R.id.tv_add_WifiSelectName)
    TextView tv_add_WifiName;
    @BindView(R.id.sb_add_Priority)
    SeekBar sb_add_Priority;
    @OnClick(R.id.tv_add_WifiSelectName)
    public void wifiSelect(View view){
        //주변 리스트와 저장된 wifi 리스트를 보여줘서 선택할 수 있게 설정
        //RecyclerView 하나를 더 생성 해야할 듯\
        tv_add_WifiName.setText("Ok");
    }

    @OnClick(R.id.btn_add_next)
    public void nextClick(View view){
        if(isChecking()) {
            Log.e(TAG,"Add -> Detail");
            Intent intent = new Intent(AddActivity.this, DetailActivity.class);
            intent.putExtra("View_name",et_add_name.getText().toString());
            intent.putExtra("View_ssid",tv_add_WifiName.getText().toString());
            Random rnd = new Random();
            intent.putExtra("View_bssid","ooo" + rnd.nextInt(10));
            intent.putExtra("View_Priority",sb_add_Priority.getProgress());
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_add_back)
    public void backClick(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
    }

    private boolean isChecking(){
        if(et_add_name.getText().toString().equals("")){
            Log.e(TAG,"'name' no edit");
            Toast.makeText(this,"이름을 입력해주십시오.",Toast.LENGTH_SHORT).show();
            et_add_name.setFocusable(true);
            return false;
        }
        if(tv_add_WifiName.getText().toString().equals("")){
            Log.e(TAG,"Wifi no select");
            Toast.makeText(this,"Wifi를 선택해주십시오.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        Log.e(TAG, "Navigate 클릭됨");
        return super.onSupportNavigateUp();
    }
}
