package com.nammu.smartwifi.UI.setdata;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.UI.setdata.fragment.DetailSetFragment;
import com.nammu.smartwifi.UI.setdata.fragment.SetFragment;
import com.nammu.smartwifi.UI.setlist.MainActivity;
import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.realmdb.realmobject.WifiData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetActivity extends AppCompatActivity implements SetFragment.OnChangedListener {
    public static boolean isFrag = true;
    WifiData data;
    @BindView(R.id.iv_toolbar)
    ImageView iv_toolbar;

    @OnClick(R.id.iv_toolbar)
    public void toolbarImageClick(View view){
        resetFragment();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        Toolbar toolbar = (Toolbar)findViewById(R.id.set_toolbar);
        if(MainActivity.VIEW_EDIT){
            WifiData data = getIntent().getExtras().getParcelable("Edit_WifiData");
            toolbar.setTitle(getString(R.string.title_activity_edit));
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_set, SetFragment.newInstance(data), "set").commit();
        }else{
            toolbar.setTitle(getString(R.string.title_activity_add));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
    }

    private void resetFragment() {
        SLog.d("toolbar click");
        iv_toolbar.setVisibility(View.VISIBLE);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("detail");
        if (DetailSetFragment.ResetFragmentListener.class.isAssignableFrom(fragment.getClass())) {
            DetailSetFragment.ResetFragmentListener resetFragmentListener = (DetailSetFragment.ResetFragmentListener) fragment;
            resetFragmentListener.updateView();
        }
    }


    //Set -> Detail로 전환
    @Override
    public void onChangeFragment(WifiData data) {
        iv_toolbar.setVisibility(View.VISIBLE);
        this.data = data;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_set, DetailSetFragment.newInstance(data), "detail").addToBackStack(null).commit();
        SLog.d("changeFragment");
    }

}

