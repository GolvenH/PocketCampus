package com.bzu.yhd.pocketcampus.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.model.NuShare;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.model.Ushare;
import com.bzu.yhd.pocketcampus.widget.utils.ActivityManagerUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.oubowu.slideback.ActivityHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;


public class BaseApplication extends Application {

    private static String sCacheDir;
    public static Context sAppContext;

    public static boolean DEBUG = false;
    private Ushare currentUshare = null;
    private NuShare currentNuShare = null;
    public static int H,W;

    public static BaseApplication sBaseApplication = null;
    private ActivityHelper mActivityHelper;
    public static List<?> images=new ArrayList<>();
    public static List<String> titles=new ArrayList<>();
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

        setInstance(this);
        //初始化Logger
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new MyMessageHandler(this));
            //百度地图初始化
            SDKInitializer.initialize(this);
            //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
            //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
            SDKInitializer.setCoordType(CoordType.BD09LL);
        }
        //uil初始化
        UniversalImageLoader.initImageLoader(this);
        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */
        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }

        getScreen(this);
        Fresco.initialize(this);
        String[] urls = getResources().getStringArray(R.array.url);
        String[] tips = getResources().getStringArray(R.array.title);
        List list = Arrays.asList(urls);
        images = new ArrayList(list);
        List list1 = Arrays.asList(tips);
        titles= new ArrayList(list1);

    }


    public void getScreen(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        H=dm.heightPixels;
        W=dm.widthPixels;
    }
    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Ushare getCurrentUshare()
    {
        return currentUshare;
    }

    public void setCurrentUshare(Ushare currentUshare)
    {
        this.currentUshare = currentUshare;
    }

    public NuShare getCurrentNuShare()
    {
        return currentNuShare;
    }


    public void setCurrentNuShare(NuShare currentNuShare)
    {
        this.currentNuShare = currentNuShare;
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
    public  void initImageLoader()
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


   @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }

    private static BaseApplication INSTANCE;
    public static BaseApplication INSTANCE(){
        return INSTANCE;
    }
    private void setInstance(BaseApplication app) {
        setBaseApplication(app);
    }
    private static void setBaseApplication(BaseApplication a) {
        BaseApplication.INSTANCE = a;
    }
}


