package com.nammu.smartwifi.classdata;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.nammu.smartwifi.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Created by SunJae on 2017-02-06.
 */

public class ItemAdapter  extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
    ArrayList<WifiItem> itemList;
    int layout;
    Context context;

    public ItemAdapter(ArrayList<WifiItem> list, int layout, Context context){
        itemList = list;
        this.layout = layout;
        this.context = context;
    }
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        WifiItem item = itemList.get(position);
        holder.tv_wifiName.setText(item.getWifi_Name());
        holder.tv_wifiSSID.setText(item.getWifi_SSID());
        holder.tv_wifistate.setChecked(item.isWifi_state());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_rec_WifiName)
        TextView tv_wifiName;
        @BindView(R.id.tv_rec_WifiSSID)
        TextView tv_wifiSSID;
        @BindView(R.id.sw_item)
        Switch tv_wifistate;

        @OnCheckedChanged(R.id.sw_item)
        public void checkedChange(CompoundButton compoundButton, boolean isCheck) {
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
