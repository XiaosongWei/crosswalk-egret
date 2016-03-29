package org.egret.egretruntimelauncher.exception;

import android.app.Application;

public class CrashApplication extends Application {
    private static CrashApplication instance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
    }
    
    public static CrashApplication getInstance() {
        if (instance == null) {
            instance = new CrashApplication();
        }
        return instance;
    }
}