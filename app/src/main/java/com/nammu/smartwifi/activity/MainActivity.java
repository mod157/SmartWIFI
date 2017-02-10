package com.nammu.smartwifi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.classdata.ItemAdapter;
import com.nammu.smartwifi.classdata.WifiItem;
import com.nammu.smartwifi.realmdb.RealmDB;
import com.nammu.smartwifi.realmdb.WifiData;
import com.nammu.smartwifi.realmdb.WifiData_State;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private String TAG = "##### SmartWIFI";
    @BindView(R.id.Rec_WifiList)
    RecyclerView recyclerView;
    private ArrayList<WifiItem> itemList = new ArrayList<>();
    private Realm realm;
    private long backKeyTime=0;

    @OnClick(R.id.fab)
    public void fabClick(View view){
        //리스트 보여주는 형태
        Intent intent = new Intent(MainActivity.this, SetActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ListView();
    }

    public void testPrint(){
        Realm realm2 = RealmDB.RealmInit(this);
        RealmResults<WifiData> itemResult = realm2.where(WifiData.class).findAll();
        RealmResults<WifiData_State> itemResult2 = realm2.where(WifiData_State.class).findAll();
        Log.e(TAG, "List : " + itemResult.toString()+"\n"+itemResult2.toString());
    }
    private void ListView(){
        testPrint();
        realm = RealmDB.RealmInit(this);
        RealmResults<WifiData> itemResult = realm.where(WifiData.class).findAll();
        for(int i = 0; i<itemResult.size(); i++){
            WifiData itemData = itemResult.get(i);
            String name = itemData.getName();
            String ssid = itemData.getSSID();
            String bssid = itemData.getBSSID();
            boolean isPlay = itemData.getisPlay();
            WifiItem item = new WifiItem(name,ssid,bssid,isPlay);
            itemList.add(item);
        }
        ItemAdapter adapter = new ItemAdapter(itemList, R.layout.recycler_item_layout,this);
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
