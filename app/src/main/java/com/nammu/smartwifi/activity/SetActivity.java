package com.nammu.smartwifi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.fragment.DetailSetFragment;
import com.nammu.smartwifi.fragment.SetFragment;
import com.nammu.smartwifi.interfaces.OnInterface;
import com.nammu.smartwifi.realmdb.WifiData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetActivity extends AppCompatActivity implements OnInterface.OnChangedListener, OnInterface.ToolbarImageVisible {
    private String TAG = "##### SetActiviy";
    public static boolean isFrag = true;
    WifiData data;
    @BindView(R.id.iv_toolbar)
    ImageView iv_toolbar;

    @OnClick(R.id.iv_toolbar)
    public void toolbarImageClick(View view){
        onChangeFragment(data);
    }
    
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
        ButterKnife.bind(this);
    }
    //Set -> Detail로 전환
    @Override
    public void onChangeFragment(WifiData data) {
        iv_toolbar.setVisibility(View.VISIBLE);
        this.data = data;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_set, DetailSetFragment.newInstance(data)).addToBackStack(null).commit();
        Log.e(TAG, "changeFragment");
    }

    @Override
    public void setToolbarimage() {
        iv_toolbar.setVisibility(View.INVISIBLE);
    }
}

