package com.nammu.ficatch.model;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by SunJae on 2017-02-14.
 */

public class OnInterface {
    public interface WifiScanResultInterface{
        public void setScanResult(List<ScanResult> list);
    }
}
