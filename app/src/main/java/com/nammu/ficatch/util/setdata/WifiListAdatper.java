package com.nammu.ficatch.util.setdata;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nammu.ficatch.R;
import com.nammu.ficatch.model.WifiListItem;
import com.nammu.ficatch.util.setdata.fragment.SetFragment;
import com.nammu.ficatch.model.SLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SunJae on 2017-02-13.
 */

public class WifiListAdatper extends RecyclerView.Adapter<WifiListAdatper.ViewHolder>  {
    private SetFragment.WifiListClickListener wifiListClickListener;
    private ArrayList<WifiListItem> wifiList_Data;
    public WifiListAdatper(ArrayList<WifiListItem> wifiList_Data, SetFragment.WifiListClickListener listener){
        SLog.d("Create");
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
        WifiListItem item = wifiList_Data.get(position);
        SLog.d("String : ["+ position + "]\n" + item.getSSID()+"\n"+ item.getBSSID()+"\n"+(item.getLevel()/10) + item.getSave());
        holder.tv_dialog_ssid.setText(item.getSSID()+"     ");
        changeLevelImage(holder, item.getLevel()/10);
        if(item.getSave())
            holder.iv_dialog_save.setVisibility(View.VISIBLE);
    }

    private void changeLevelImage(ViewHolder holder, int level){
        switch (level){
            case -6:
            case -7:
                holder.iv_list_level.setBackgroundResource(R.drawable.shadow_border_level_normal);
                break;
            case -8:
            case -9:
            case -10:
                holder.iv_list_level.setBackgroundResource(R.drawable.shadow_border_level_low);
                break;
            default:
                holder.iv_list_level.setBackgroundResource(R.drawable.shadow_border_level_good);
        }
    }
    @Override
    public int getItemCount() {
        return wifiList_Data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_list_level)
        ImageView iv_list_level;
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
