package com.bzu.yhd.pocketcampus.base;

import android.app.Application;
import android.content.Context;

import com.oubowu.slideback.ActivityHelper;


public class BaseApplication extends Application {

    private static String sCacheDir;
    public static Context sAppContext;

    public static boolean DEBUG = false;


    public static BaseApplication sBaseApplication;
    private ActivityHelper mActivityHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
        mActivityHelper = new ActivityHelper();
        registerActivityLifecycleCallbacks(mActivityHelper);
        sBaseApplication = this;
        CrashHandler.init(new CrashHandler(getApplicationContext()));

        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */
        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    public static ActivityHelper getActivityHelper() {
        return sBaseApplication.mActivityHelper;
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getAppCacheDir() {
        return sCacheDir;
    }
}
