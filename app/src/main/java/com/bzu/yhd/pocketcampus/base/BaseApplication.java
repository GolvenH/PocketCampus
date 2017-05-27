package com.bzu.yhd.pocketcampus.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.widget.utils.ActivityManagerUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.oubowu.slideback.ActivityHelper;

import java.io.File;

import cn.bmob.v3.BmobUser;


public class BaseApplication extends Application {

    private static String sCacheDir;
    public static Context sAppContext;

    public static boolean DEBUG = false;

    public static BaseApplication sBaseApplication = null;
    private ActivityHelper mActivityHelper;

    public static BaseApplication getInstance()
    {
        return sBaseApplication;
    }

    public User getCurrentUser()
    {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            return user;
        }
        return null;
    }

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

    public void addActivity(Activity ac)
    {
        ActivityManagerUtils.getInstance().addActivity(ac);
    }

    public void exit()
    {
        ActivityManagerUtils.getInstance().removeAllActivity();
    }

    public Activity getTopActivity()
    {
        return ActivityManagerUtils.getInstance().getTopActivity();
    }

    /**
     * 初始化imageLoader
     */
    public void initImageLoader()
    {
        File cacheDir = StorageUtils
                .getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCache(
                        new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(10 * 1024 * 1024)
                .discCache(new UnlimitedDiskCache(cacheDir))
                .discCacheFileNameGenerator(
                        new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    public DisplayImageOptions getOptions(int drawableId)
    {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(drawableId)
                .showImageForEmptyUri(drawableId)
                .showImageOnFail(drawableId)
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }


    /**
     * 退出登录,清空缓存数据
     */
/*    public void logout()
    {
        BmobUserManager.getInstance(getApplicationContext()).logout();
        setContactList(null);
        setLatitude(null);
        setLongtitude(null);
    }*/

}
