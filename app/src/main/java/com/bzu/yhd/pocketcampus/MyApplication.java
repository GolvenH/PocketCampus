package com.bzu.yhd.pocketcampus;

import android.app.Application;

import com.oubowu.slideback.ActivityHelper;

/**
 * <p/>
 * Created by woxingxiao on 2017-2017-01-25.
 */
public class MyApplication extends Application {

    public static boolean DEBUG = false;

    public static MyApplication sMyApplication;

    private ActivityHelper mActivityHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        mActivityHelper = new ActivityHelper();
        registerActivityLifecycleCallbacks(mActivityHelper);
        sMyApplication = this;

        //CrashHandler.getInstance().init(this);

    }

    public static ActivityHelper getActivityHelper() {
        return sMyApplication.mActivityHelper;
    }
}
