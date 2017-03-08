package com.nammu.ficatch.model.manager;

import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by SunJae on 2017-02-18.
 */

public class WifiBrightManager {
    public static final int BRIGHT_MIN = 10;
    public static final int BRIGHT_MAX = 250;

    private Context context;

    public WifiBrightManager(Context context){
        this.context = context;
    }

    public void setWindowBright(Window window, float bright){
        WindowManager.LayoutParams parms =  window.getAttributes();
        parms.screenBrightness = bright;
        parms.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        window.setAttributes(parms);
    }

    public int getBrightSize() {
        try {
            if (Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) != 0) {
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }
            int bright = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            return bright;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setSettingBright(int size){
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, size);
    }
}
