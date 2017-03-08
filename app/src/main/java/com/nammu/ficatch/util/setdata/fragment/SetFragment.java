package com.nammu.ficatch.util.setdata.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nammu.ficatch.R;
import com.nammu.ficatch.util.setdata.dialog.WifiListDialog;
import com.nammu.ficatch.model.WifiListItem;
import com.nammu.ficatch.util.setlist.MainActivity;
import com.nammu.ficatch.model.OnInterface;
import com.nammu.ficatch.model.SLog;
import com.nammu.ficatch.model.manager.WifiScan;
import com.nammu.ficatch.realmdb.realmobject.WifiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SunJae on 2017-02-09.
 */

public class SetFragment extends Fragment {
    private String bssid = "";
    private WifiListDialog listDialog;
    private WifiData data = new WifiData();
    private OnChangedListener mCallback;
    private WifiManager wm;
    private List<WifiConfiguration> configNetworkList;
    private boolean wifi_state;
    private Context set_Context;
    private ProgressDialog progressDialog;
    private boolean threadState = true;
    private WifiScan wifiScan;
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
                while(threadState){
                    SLog.d("list DIalog is state : " + (listDialog != null));
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
        }
    };

    @OnClick(R.id.btn_set_Success)
    public void setButtonClick(View view){
        SLog.d("Button Click");
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
        SLog.d("CreateView");
        set_Context = view.getContext();
        ButterKnife.bind(this, view);

        //Activity로 부터 전달
        if(MainActivity.VIEW_EDIT) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                SLog.d("Bundler get Parcelable");
                data = bundle.getParcelable("WifiData_Bundle");
                SLog.d(data.getName() + data.getSSID() + data.getBSSID());
                bssid = data.getBSSID();
                tv_add_WifiName.setText(data.getSSID());
                et_add_name.setText(data.getName());
                sb_add_Priority.setProgress(data.getPripority());
                tv_add_WifiName.setTextColor(Color.GRAY);
                SLog.d("isSave : " + data.getisPlay());
            }
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initWifiScan();
                }
            }).start();

        }
        SLog.d("Scan 이후");
        return view;
    }

    private void initWifiScan(){
        wm = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi_state = wm.isWifiEnabled();
        wifiScan = new WifiScan(getContext(),wm, wifiScanResultInterface);
        configNetworkList = wifiScan.configScan();
        wifiScan.Scan();
    }

    private boolean isChecking(){
        if(et_add_name.getText().toString().equals("")){
            SLog.d("'name' no edit");
            Toast.makeText(set_Context,"이름을 입력해주십시오.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(tv_add_WifiName.getText().toString().equals("")){
            SLog.d("Wifi no select");
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

    // wifi 스캔 후에 스캔 한 갯수만큼 출력하기
    public void searchWifiList(List<ScanResult> scanList) {
        SLog.d("ScanList");
        int size;
        List apList = scanList;
        if ((size = apList.size()) != 0) {
            SLog.d(size + "");
            List list = new ArrayList();
            for(int j = 0; j<apList.size(); j++){ //SSID 중복 제거 (맥주소 통일화)
                ScanResult sr2 = (ScanResult) apList.get(j);
                if(sr2.SSID.toString().trim().equals(""))
                    continue;
                list.add(sr2.SSID.toString());
            }
            ArrayList<String> wifilist = new ArrayList(new HashSet(list));
            ArrayList<WifiListItem> setlist = new ArrayList<>();
            for(int i = 0; i<wifilist.size(); i++) {
                WifiListItem item = new WifiListItem();
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

    private ArrayList<WifiListItem> SortList(ArrayList<WifiListItem> item){
        ArrayList<WifiListItem> tempList = item;

        // 내림 차순 정렬
        ArrayList<WifiListItem> sortList = new ArrayList<>();
        DescendingObj descending = new DescendingObj();
        Collections.sort(tempList, descending);
        for(int i = 0; i < tempList.size(); i++){
           if(tempList.get(i).getSave()){
                SLog.d("OK save : " + tempList.get(i).getSSID());
                sortList.add(tempList.get(i));
                tempList.remove(i--);
            }
        }
        sortList.addAll(sortList.size(),tempList);
        SLog.d("sortList size : " + sortList.size());
        return sortList;
    }

    class DescendingObj implements Comparator<WifiListItem> {
        @Override
        public int compare(WifiListItem o1, WifiListItem o2) {
            return ((Integer)o2.getLevel()).compareTo((Integer)o1.getLevel());
        }
    }

    private WifiListItem ItemSet(List apList, WifiListItem item){
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
                    bssid += "@" + sr.BSSID;
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
        try {
            if (configNetworkList.isEmpty()) {
                SLog.d("저장된 list가 빔");
            }

            for (WifiConfiguration i : configNetworkList) {
                if (i.SSID != null && i.SSID.equals("\"" + scanSSID + "\"")) {
                    SLog.d("SSID : " + i.SSID + ", BSSID" + i.BSSID);
                    return true;
                }
            }
            return false;
        }catch(NullPointerException e){
            SLog.d(""+e.toString());
            configNetworkList = wifiScan.configScan();
            return false;
        }
    }

    public SetFragment.WifiListClickListener wifiListClickListener = new SetFragment.WifiListClickListener() {
        @Override
        public void WifiListClick(WifiListItem item) {
            tv_add_WifiName.setText(item.getSSID());
            bssid = item.getBSSID();
            if(listDialog.isShowing())
                listDialog.cancel();
        }
    };

    public OnInterface.WifiScanResultInterface wifiScanResultInterface = new OnInterface.WifiScanResultInterface() {
        @Override
        public void setScanResult(List<ScanResult> list) {
            SLog.d("Interface Result");
            searchWifiList(list);
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        SLog.d("Resume");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        SLog.d("destroy");
        threadState = false;
    }

    //SetFragment -> SetActivity로 전달하여 Detail 실행
    public interface OnChangedListener {
        public void onChangeFragment(WifiData data);
    }

    public interface WifiListClickListener {
        public void WifiListClick(WifiListItem item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }
}
