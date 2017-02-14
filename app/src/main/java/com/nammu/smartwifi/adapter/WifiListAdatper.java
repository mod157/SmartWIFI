package com.nammu.smartwifi.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.interfaces.OnInterface;
import com.nammu.smartwifi.object.WifiList_Item;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SunJae on 2017-02-13.
 */

public class WifiListAdatper extends RecyclerView.Adapter<WifiListAdatper.ViewHolder>  {
    private OnInterface.WifiListClickListener wifiListClickListener;
    ArrayList<WifiList_Item> wifiList_Data;
    Activity activity;
    public WifiListAdatper(ArrayList<WifiList_Item> wifiList_Data, OnInterface.WifiListClickListener listener){
        Log.e("##### WIFIAdapter", "Create");
        this.wifiList_Data = wifiList_Data;
        wifiListClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_list, parent, false);
        return new WifiListAdatper.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WifiList_Item item = wifiList_Data.get(position);
        Log.e("##### WIFIAdapter", "String : ["+ position + "]\n" + item.getSSID()+"\n"+ item.getBSSID()+"\n"+item.getLevel() + item.getSave());
        holder.tv_dialog_ssid.setText(item.getSSID()+"     ");
        //TODo 세기 마다 글자 색 변경? 표시;
        if(item.getSave())
            holder.iv_dialog_save.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return wifiList_Data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_dialog_ssid)
        TextView tv_dialog_ssid;
        @BindView(R.id.iv_dialog_save)
        ImageView iv_dialog_save;

        @OnClick(R.id.tv_dialog_ssid)
        public void Click(View view){
            wifiListClickListener.WifiListClick(wifiList_Data.get(getPosition()));
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
