package com.nammu.ficatch.util.setlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.nammu.ficatch.R;
import com.nammu.ficatch.model.SLog;
import com.nammu.ficatch.realmdb.RealmDB;
import com.nammu.ficatch.realmdb.realmobject.WifiData;
import com.nammu.ficatch.service.SystemBindService;
import com.nammu.ficatch.util.setdata.SetActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by SunJae on 2017-02-06.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
    ArrayList<WifiData> itemList;
    Context context;
    boolean deleteMode;

    public ItemAdapter(ArrayList<WifiData> list, Context context, boolean mode){
        itemList = list;
        this.context = context;
        deleteMode = mode;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        if(deleteMode){
            holder.iv_delete.setVisibility(View.VISIBLE);
        }else{
            holder.iv_delete.setVisibility(View.GONE);
        }
            WifiData item = itemList.get(position);
        if(!item.getisPlay()) {
            holder.tv_wifiName.setTextColor(Color.GRAY);
        }
            holder.tv_wifiName.setText(item.getName());
            holder.tv_wifiSSID.setText(item.getSSID());
            holder.sw_wifistate.setChecked(item.getisPlay());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        @BindView(R.id.img_delete)
        ImageView iv_delete;
        @BindView(R.id.tv_rec_WifiName)
        TextView tv_wifiName;
        @BindView(R.id.tv_rec_WifiSSID)
        TextView tv_wifiSSID;
        @BindView(R.id.sw_item)
        Switch sw_wifistate;

        @OnCheckedChanged(R.id.sw_item)
        public void checkedChange(CompoundButton compoundButton, boolean isCheck) {
            if (isCheck)
                tv_wifiName.setTextColor(Color.parseColor("#000000"));
            else
                tv_wifiName.setTextColor(Color.GRAY);
            isWifi_state(isCheck);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        private void isWifi_state(boolean isCheck) {
            WifiData data = itemList.get(getPosition());
            if (data.getisPlay() != isCheck)
                RealmDB.InsertOrUpdate_Data(context, data, isCheck);
        }

        private void setActivityStart() {
            MainActivity.VIEW_EDIT = true;
            SLog.d("isPlay :" + itemList.get(getPosition()).getisPlay());
            Intent intent = new Intent(context, SetActivity.class);
            intent.putExtra("Edit_WifiData", itemList.get(getPosition()));
            context.startActivity(intent);
        }

        @OnClick(R.id.img_delete)
        public void deleteClick(View view) {
            int position = getPosition();
            RealmDB.deleteData(context, itemList.get(position));
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
            Intent bindIntent = new Intent(context, SystemBindService.class);
            bindIntent.setAction("resetConnection");
            context.startService(bindIntent);

        }

        @OnClick(R.id.linear_item)
        @Override
        public void onClick(View view) {
            setActivityStart();
        }
    }

}
