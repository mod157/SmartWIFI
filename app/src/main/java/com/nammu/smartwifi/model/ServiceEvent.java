package com.nammu.smartwifi.model;

/**
 * Created by SunJae on 2017-02-21.
 */

public class ServiceEvent {
    private static changeNotificationEventListener eventListener;

    private ServiceEvent(changeNotificationEventListener eventListener){
        if(eventListener != null)
            setService(eventListener);
    }

    public void setService(changeNotificationEventListener eventListener){
        this.eventListener = eventListener;
    }

    public static changeNotificationEventListener getInstance(){
        return getInstance(null);
    }

    public static changeNotificationEventListener getInstance(changeNotificationEventListener changeNotificationEventListener) {
        if(eventListener == null && changeNotificationEventListener != null)
           eventListener = changeNotificationEventListener;
        return eventListener;
    }

    public interface changeNotificationEventListener{
        public void onChangeConnection();
        public void onServiceStop();
        public void onServiceStart();
    }
}
