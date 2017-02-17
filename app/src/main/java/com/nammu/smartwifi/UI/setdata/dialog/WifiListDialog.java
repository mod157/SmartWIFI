package com.nammu.smartwifi.UI.setdata.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.UI.setdata.WifiListAdatper;
import com.nammu.smartwifi.UI.setdata.domain.WifiList_Item;
import com.nammu.smartwifi.UI.setdata.interfaces.SetInterfaces;
import com.nammu.smartwifi.model.SLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SunJae on 2017-02-13.
 */

public class WifiListDialog extends Dialog {
    @BindView(R.id.rv_wifiList)
    RecyclerView rv_wifiList;
    Context context;
    ArrayList<WifiList_Item> wifi_List;
    Activity activity;
    SetInterfaces.WifiListClickListener listener;

    public WifiListDialog(Context context, ArrayList<WifiList_Item> list, Activity activity, SetInterfaces.WifiListClickListener listener) {
        super(context);
        this.context = context;
        wifi_List = list;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.9f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_wifilist);
        ButterKnife.bind(this);
        ListView();
    }

    private void ListView(){
        rv_wifiList.setLayoutManager(new LinearLayoutManager(activity));
        WifiListAdatper adapter = new WifiListAdatper(wifi_List, listener);
        SLog.d("size : " + adapter.getItemCount());
        rv_wifiList.setAdapter(adapter);
    }
}
