package com.nammu.smartwifi.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nammu.smartwifi.R;

import butterknife.ButterKnife;

/**
 * Created by SunJae on 2017-02-09.
 */

public class DetailSetFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_set, container, false );
        ButterKnife.bind(view);
        return view;
    }
}
