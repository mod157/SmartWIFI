package com.nammu.smartwifi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.fragment.DetailSetFragment;
import com.nammu.smartwifi.fragment.SetFragment;
import com.nammu.smartwifi.realmdb.WifiData;

public class SetActivity extends AppCompatActivity implements SetFragment.OnChangedListener {
    private String TAG = "##### SetActiviy";
    public static boolean isFrag = true;
    private WifiData wifiData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_add));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wifiData = new WifiData();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_set, SetFragment.newInstance(wifiData)).commit();
        }
    }

    //Set -> Detail로 전환
    @Override
    public void onChangeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_set, DetailSetFragment.newInstance(wifiData)).addToBackStack(null).commit();
        Log.e(TAG, "changeFragment");
    }
}

