package com.nammu.smartwifi.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
        SecurityPermission();
    }

    @Override
    public void onStart(){
        super.onStart();
        SecurityPermission();
    }

    private void SecurityPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
              //  Toast.makeText(this, "onCreate: Already Granted", Toast.LENGTH_SHORT).show();
                PermissionCheck();
            } else {
               // Toast.makeText(this, "onCreate: Not Granted. Permission Requested", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
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
