package com.nammu.smartwifi.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.nammu.smartwifi.realmdb.WifiData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SunJae on 2017-02-09.
 */

public class SetFragment extends Fragment {
    private final String TAG = "##### SetFragment";
    OnChangedListener mCallback;
    private WifiData data = new WifiData();
    Context set_Context;

    @BindView(R.id.et_add_name)
    EditText et_add_name;
    @BindView(R.id.tv_add_WifiSelectName)
    TextView tv_add_WifiName;
    @BindView(R.id.sb_add_Priority)
    SeekBar sb_add_Priority;

    @OnClick(R.id.tv_add_WifiSelectName)
    public void wifiSelect(View view){
        //주변 리스트와 저장된 wifi 리스트를 보여줘서 선택할 수 있게 설정
        //RecyclerView 하나를 더 생성 해야할 듯\
        tv_add_WifiName.setText("Ok");
    }

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
            }
        }
        return view;
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
        data.setBSSID("01010");
        data.setPripority(sb_add_Priority.getProgress());
    }

    // Fragment를 담고 있는 Activity는 이 interface를 구현해야 한다.
    public interface OnChangedListener {
        public void onChangeFragment(WifiData data);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG,"destroy");

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
