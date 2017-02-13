package com.nammu.smartwifi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.nammu.smartwifi.R;
import com.nammu.smartwifi.realmdb.RealmDB;
import com.nammu.smartwifi.realmdb.WifiData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by SunJae on 2017-02-11.
 */

public class RecyclerMenuDialog extends Dialog {
    private WifiData data;
    private Realm realm;
    private View.OnClickListener clickState;
    private View.OnClickListener clickEdit;
    private View.OnClickListener clickDelete;

    @BindView(R.id.tv_dialog_state)
    TextView tv_dialog_state;
    @BindView(R.id.tv_dialog_edit)
    TextView tv_dialog_edit;
    @BindView(R.id.tv_dialog_delete)
    TextView tv_dialog_delete;

    @OnClick(R.id.tv_dialog_state)
    public void StateClick(View view){
        data.setisPlay(!data.getisPlay());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
    }


    public RecyclerMenuDialog(Context context, WifiData data, View.OnClickListener stateClickListener,
                              View.OnClickListener editClickListener, View.OnClickListener deleteClickListener ) {
        super(context);
        realm = RealmDB.RealmInit(context);
        this.data = data;
        clickState = stateClickListener;
        clickEdit = editClickListener;
        clickDelete = deleteClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_menu);
        ButterKnife.bind(this);

        tv_dialog_state.setOnClickListener(clickState);
        tv_dialog_edit.setOnClickListener(clickEdit);
        tv_dialog_delete.setOnClickListener(clickDelete);
    }
}
