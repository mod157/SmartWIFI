package com.nammu.smartwifi.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.activity.MainActivity;
import com.nammu.smartwifi.dialog.WifiListDialog;
import com.nammu.smartwifi.interfaces.OnInterface;
import com.nammu.smartwifi.object.WifiList_Item;
import com.nammu.smartwifi.realmdb.WifiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by SunJae on 2017-02-09.
 */

public class SetFragment extends Fragment {
    private final String TAG = "##### SetFragment";
    private String bssid = "";
    private WifiListDialog listDialog;
    private WifiData data = new WifiData();
    OnInterface.OnChangedListener mCallback;
    private WifiManager wm;
    private List<WifiConfiguration> configNetworkList;
    private boolean wifi_state;
    Context set_Context;
    private ProgressDialog progressDialog;

    @BindView(R.id.et_add_name)
    EditText et_add_name;
    @BindView(R.id.tv_add_WifiSelectName)
    TextView tv_add_WifiName;
    @BindView(R.id.sb_add_Priority)
    SeekBar sb_add_Priority;

    @OnClick(R.id.tv_add_WifiSelectName)
    public void wifiSelect(View view) {
        //TODO Wifi 변경을 불가 or 기존에 존재하는 WIFI만을 제외하고 다시 보여줌
        if (MainActivity.VIEW_EDIT)
            return;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("읽어 오는 중");
        progressDialog.setMessage("잠시만 기다려 주십시오.");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Log.e(TAG,"list DIalog is state : " + (listDialog != null));
                    if(listDialog != null && configNetworkList != null) {
                        handler.sendEmptyMessage(0);
                        progressDialog.dismiss();
                        break;
                    }else{
                        try{
                            Thread.sleep(500);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            listDialog.show();
            //Intent intent = new Intent(getContext(), WifiListDialog.class);
           // startActivity(intent);
        }
    };

    @OnClick(R.id.btn_set_Success)
    public void SetButtonClick(View view){
        Log.e(TAG,"Button Click");
        if(isChecking()) {
            mCallback.onChangeFragment(data);
        }
    }

    public static Fragment newInstance(WifiData data){
        Fragment fragment = new SetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("WifiData_Bundle",data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_set, container, false);
        Log.e(TAG, "CreateView");
        set_Context = view.getContext();
        ButterKnife.bind(this, view);
        //Activity로 부터 전달
        if(MainActivity.VIEW_EDIT) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                Log.e(TAG, "Bundler get Parcelable");
                data = bundle.getParcelable("WifiData_Bundle");
                Log.e(TAG,data.getName() + data.getSSID());
                tv_add_WifiName.setText(data.getSSID());
                et_add_name.setText(data.getName());
                sb_add_Priority.setProgress(data.getPripority());
            }
        }else{
            initWifiScan();
        }
        Log.e(TAG,"Scan 이후");
        return view;
    }
    private void initWifiScan(){
        wm = (WifiManager) getContext().getSystemService(WIFI_SERVICE);
        if(wm.isWifiEnabled())
            configNetworkList = getConfiguredNetworks();
        else{
            wm.setWifiEnabled(true);
            configNetworkList = getConfiguredNetworks();
        }
        scan();
    }

    private boolean isChecking(){
        if(et_add_name.getText().toString().equals("")){
            Log.e(TAG,"'name' no edit");
            Toast.makeText(set_Context,"이름을 입력해주십시오.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(tv_add_WifiName.getText().toString().equals("")){
            Log.e(TAG,"Wifi no select");
            Toast.makeText(set_Context,"Wifi를 선택해주십시오.",Toast.LENGTH_SHORT).show();
            return false;
        }
        data_insert();
        return true;
    }

    public void data_insert(){
        data.setName(et_add_name.getText().toString());
        //# wifi데이터
        data.setSSID(tv_add_WifiName.getText().toString());
        data.setBSSID(bssid);
        data.setPripority(sb_add_Priority.getProgress());
    }


    public void scan() {
        //TODO 3초가 걸림
        //TODO single톤으로 데이터를 저장해서 가져오면?
        wifi_state = wm.isWifiEnabled();
        if (wifi_state) {
            // 켜져 있는 상태면 바로 스캔
            wm.startScan();
        } else {
            // 현재 wifi가 꺼져 있으면
            // 항상 검색 허용이 가능한지 확인
            if (Build.VERSION.SDK_INT >= 18 && wm.isScanAlwaysAvailable()) {
                //  4.3 버전 이상인지 체크한다.
                //  항상검색 허용 설정이 활성화상태인지 체크한다.
                wm.startScan();  // 바로 스캔 실행
            } else {
                //나머지는 Wifi를 잠시 켜서 확인한다.
                wm.setWifiEnabled(true);
                wm.startScan();
            }
        }

        Log.e("##### WIFIMANGER", "startScan");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiR, filter);
    }

    private BroadcastReceiver wifiR = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                Log.e("##### WIFIMANGER", "BroadCast");
                if(!wifi_state)
                    wm.setWifiEnabled(false);
                searchWifiList();
                context.unregisterReceiver(wifiR);
            }
        }
    };

    // wifi 스캔 후에 스캔 한 갯수만큼 출력하기
    public void searchWifiList() {
        Log.e("##### WIFIMANGER", "ScanList");
        int size;
        List setList = null;

        List apList = wm.getScanResults();
        if ((size = apList.size()) != 0) {
            Log.e(TAG, size + "");
            List list = new ArrayList();
            for(int j = 0; j<apList.size(); j++){ //SSID 중복 제거 (맥주소 통일화)
                ScanResult sr2 = (ScanResult) apList.get(j);
                if(sr2.SSID.toString().trim().equals(""))
                    continue;
                list.add(sr2.SSID.toString());
            }
            ArrayList<String> wifilist = new ArrayList(new HashSet(list));
            ArrayList<WifiList_Item> setlist = new ArrayList<>();
            for(int i = 0; i<wifilist.size(); i++) {
                WifiList_Item item = new WifiList_Item();
                String ssid = wifilist.get(i);
                item.setSSID(ssid);
                item = ItemSet(apList, item);
                item.setSave(checkingSaveWifi(configNetworkList, ssid));
                setlist.add(item);
            }

            setlist = SortList(setlist);
            if(wm.isWifiEnabled() && wifi_state)
                wm.setWifiEnabled(false);
            listDialog = new WifiListDialog(getContext(), setlist, getActivity(), wifiListClickListener );
        }
    }

    private ArrayList<WifiList_Item> SortList(ArrayList<WifiList_Item> item){
        ArrayList<WifiList_Item> tempList = item;
        ArrayList<WifiList_Item> sortList = new ArrayList<>();
        for(int i = 0; i<tempList.size(); i++){
            if(tempList.get(i).getSave()){
                sortList.add(tempList.get(i));
                tempList.remove(i);
            }
        }
        // 내림 차순 정렬
        DescendingObj descending = new DescendingObj();
        Collections.sort(tempList, descending);
        sortList.addAll(sortList.size(),tempList);
        return sortList;
    }

    class DescendingObj implements Comparator<WifiList_Item> {
        @Override
        public int compare(WifiList_Item o1, WifiList_Item o2) {
            return ((Integer)o2.getLevel()).compareTo((Integer)o1.getLevel());
        }
    }

    private WifiList_Item ItemSet(List apList, WifiList_Item item){
        String ssid = item.getSSID();
        String bssid = "";
        int level = 0;
        int listsize = apList.size();
        for(int i = 0; i<listsize; i++) {
            ScanResult sr = (ScanResult) apList.get(i);
            if(ssid.equals(sr.SSID.toString())) {
                if(bssid.equals("")) {
                    bssid += sr.BSSID;
                }else {
                    bssid += "*" + sr.BSSID;
                }
                if (level == 0) {
                    item.setLevel(sr.level);
                } else {
                    if(sr.level < level){
                        item.setLevel(level);
                    }
                }
            }
        }
        item.setBSSID(bssid);
        return item;
    }

    private boolean checkingSaveWifi(List<WifiConfiguration> configNetworkList,String scanSSID){
        //Log.e(TAG, "Configured list \n" + getConfiguredNetworks());
      //  List<WifiConfiguration> list = getConfiguredNetworks();
        if (configNetworkList.isEmpty()) {
            Log.v(TAG, "저장된 list가 빔");
        }

        for (WifiConfiguration i : configNetworkList) {
            if (i.SSID != null && i.SSID.equals("\"" + scanSSID + "\"")) {
                Log.e(TAG, "SSID : " + i.SSID + ", BSSID" + i.BSSID);
                /*wm.disconnect();
                wm.enableNetwork(i.networkId, true);
                wm.reconnect();
                break;*/
                return true;
            }
        }
      return false;
    }

    private List<WifiConfiguration> getConfiguredNetworks() {
        try {
            return wm.getConfiguredNetworks();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }

    public OnInterface.WifiListClickListener wifiListClickListener = new OnInterface.WifiListClickListener() {
        @Override
        public void WifiListClick(WifiList_Item item) {
            tv_add_WifiName.setText(item.getSSID());
            bssid = item.getBSSID();
            if(listDialog.isShowing())
                listDialog.cancel();
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG,"destroy");

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnInterface.OnChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }
}
