package com.nammu.ficatch.util.permission;

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
import com.nammu.ficatch.R;
import com.nammu.ficatch.model.SLog;
import com.nammu.ficatch.util.setlist.MainActivity;

import java.util.ArrayList;


public class InitActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
       // permissionCheck();
        SLog.e("Permission Create");
    }

    @Override
    public void onStart(){
        super.onStart();
        SLog.e("Permission Start");
        if(Build.VERSION.SDK_INT > 23)
            permissionCheck();
        else{
            Intent intent = new Intent(InitActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void securityPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                Intent intent = new Intent(InitActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void permissionCheck(){

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                    securityPermission();
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
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
