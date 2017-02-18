package com.nammu.smartwifi.UI.setlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.UI.setdata.SetActivity;
import com.nammu.smartwifi.model.SLog;
import com.nammu.smartwifi.model.ServiceCheck;
import com.nammu.smartwifi.realmdb.RealmDB;
import com.nammu.smartwifi.realmdb.realmobject.WifiData;
import com.nammu.smartwifi.realmdb.realmobject.WifiDataState;
import com.nammu.smartwifi.service.SystemService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    public static boolean VIEW_EDIT = false;
    private ArrayList<WifiData> itemList = new ArrayList<>();
    private long backKeyTime = 0;
    @BindView(R.id.Rec_WifiList)
    RecyclerView recyclerView;

    @OnClick(R.id.iv_main_toolbar)
    public void toolbarClick(View view) {
        //TODO 리스트 삭제형으로 변경

    }

    @OnClick(R.id.fab)
    public void fabClick(View view) {
        //리스트 보여주는 형태
        Intent intent = new Intent(MainActivity.this, SetActivity.class);
        VIEW_EDIT = false;
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ListView();
        if (ServiceCheck.isServiceRunningCheck(this)) {
            SLog.d("Service Start");
            Intent intent = new Intent(this, SystemService.class);
            startService(intent);
        }
    }

    //TODO 삭제
    public void testPrint() {
        Realm realm2 = RealmDB.RealmInit(this);
        RealmResults<WifiData> itemResult = realm2.where(WifiData.class).findAll();
        RealmResults<WifiDataState> itemResult2 = realm2.where(WifiDataState.class).findAll();
        SLog.d("List : " + itemResult.toString() + "\n" + itemResult2.toString());
    }

    private void ListView() {
        testPrint();
        Realm realm = RealmDB.RealmInit(this);
        RealmResults<WifiData> itemResult = realm.where(WifiData.class).findAll();
        for (int i = 0; i < itemResult.size(); i++) {
            WifiData itemData = itemResult.get(i);
            itemList.add(itemData);
        }
        ItemAdapter adapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Toast toast;
        if (System.currentTimeMillis() > backKeyTime + 2000) {
            backKeyTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyTime + 2000) {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
