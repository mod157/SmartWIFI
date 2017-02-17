package com.nammu.smartwifi.model;

import android.util.Log;

import com.nammu.smartwifi.UI.permission.InitActivity;

/**
 * Created by SunJae on 2017-02-17.
 */

public class SLog {
    private static boolean debug = InitActivity.LOG_STATE;
    /** Log Level Error **/

    public static final void e(String message) {
        if (debug)Log.e(tagLog(), message);}

    /** Log Level Warning **/
    public static final void w(String message) {
        if (debug)Log.w(tagLog(), message); }

    /** Log Level Information **/
    public static final void i(String message) {
        if (debug)Log.i(tagLog(), message); }

    /** Log Level Debug **/
    public static final void d(String message) {
        if (debug)Log.d(tagLog(), message); }

    /** Log Level Verbose **/
    public static final void v(String message) {
        if (debug)Log.v(tagLog(),message); }

    public static String tagLog(){
        String tag = "SmartWIFI ";
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]  ");
        return sb.toString();
    }


}
