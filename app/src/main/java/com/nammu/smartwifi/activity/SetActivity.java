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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        Toolbar toolbar = (Toolbar)findViewById(R.id.set_toolbar);

        if(MainActivity.VIEW_EDIT){
            WifiData data = getIntent().getExtras().getParcelable("Edit_WifiData");
            toolbar.setTitle(getString(R.string.title_activity_edit));
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_set, SetFragment.newInstance(data)).commit();
        }else{
            toolbar.setTitle(getString(R.string.title_activity_add));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Set -> Detail로 전환
    @Override
    public void onChangeFragment(WifiData data) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_set, DetailSetFragment.newInstance(data)).addToBackStack(null).commit();
        Log.e(TAG, "changeFragment");
    }
}

