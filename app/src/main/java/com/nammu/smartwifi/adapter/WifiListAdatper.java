package com.nammu.smartwifi.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.object.WifiList_Item;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SunJae on 2017-02-13.
 */

public class WifiListAdatper extends RecyclerView.Adapter<WifiListAdatper.ViewHolder>  {
    ArrayList<WifiList_Item> wifiList_Data;
    public WifiListAdatper(ArrayList<WifiList_Item> wifiList_Data){
        Log.e("##### WIFIAdapter", "Create");
        this.wifiList_Data = wifiList_Data;
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
        holder.tv_dialog_ssid.setText(item.getSSID());
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
