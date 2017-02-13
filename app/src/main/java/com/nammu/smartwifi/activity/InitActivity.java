package com.nammu.smartwifi.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nammu.smartwifi.R;

import java.util.ArrayList;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        PermissionCheck();
    }

    private void PermissionCheck(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(InitActivity.this, MainActivity.class);
                startActivity(intent);
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Log.e("#### 위치허가","NO");
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("주변 WIFI 리스트를 알기 위해서는 권한이 필요합니다.")
                .setDeniedMessage("권한 설정을 하지 않으면 이용하기 어렵습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .setGotoSettingButton(true)
                .check();
    }
}
