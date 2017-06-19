package com.bzu.yhd.pocketcampus.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.baidu.mapapi.SDKInitializer;
import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.base.CheckPermissionsActivity;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.bottomnav.find.FindFragment;
import com.bzu.yhd.pocketcampus.bottomnav.home.HomeListFragment;
import com.bzu.yhd.pocketcampus.bottomnav.im.ChatFragment;
import com.bzu.yhd.pocketcampus.bottomnav.im.event.RefreshEvent;
import com.bzu.yhd.pocketcampus.bottomnav.im.util.IMMLeaks;
import com.bzu.yhd.pocketcampus.bottomnav.user.UserInfoFragment;
import com.bzu.yhd.pocketcampus.widget.utils.PLog;
import com.bzu.yhd.pocketcampus.widget.utils.PrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.polaric.colorful.Colorful;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * 首页
 * </p>
 * @CreateBy Yhd On 2017/2/15 8:40
 */
public class  HomeActivity extends CheckPermissionsActivity {
    private List<Fragment> fragments = new ArrayList<>();
    private int currentTabIndex;
    private String mAutoSwitchedHint;

    private int[] tabColors;
    private AHBottomNavigationAdapter navigationAdapter;
    private AHBottomNavigation bottomNavigation;
    private static final String STATE_SELECTED_TAB_INDEX = "selected_tab_index";
    private int mPreSelectedTabIdx = 0;
    private static final String LTAG = HomeActivity.class.getSimpleName();


    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Log.d(LTAG, "action: " + s);

            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
              showToast("key 验证出错! 错误码 :" + intent.getIntExtra
                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        +  " ; 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                showToast("key 验证成功! 功能可以正常使用");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                showToast("网络出错");
            }
        }
    }

    private SDKReceiver mReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BaseApplication.getInstance().addActivity(this);

        initAHBottomNavigation(savedInstanceState);
        initFragment(savedInstanceState);

        mPreSelectedTabIdx = restoreSavedSelectedTabIdx(savedInstanceState);

        //connect server
        User user = BmobUser.getCurrentUser(User.class);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    PLog.i("connect success");
                    //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                    EventBus.getDefault().post(new RefreshEvent());
                } else {
                    PLog.e(e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                showToast("" + status.getMsg());
            }
        });
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());


        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (null != bottomNavigation) {
            int curTabIdx = bottomNavigation.getCurrentItem();
            mPreSelectedTabIdx = curTabIdx;
            outState.putInt(STATE_SELECTED_TAB_INDEX, curTabIdx);
        }
    }

    private int restoreSavedSelectedTabIdx(Bundle savedInstanceState) {
        int savedTabIdx = mPreSelectedTabIdx;
        if (null != savedInstanceState) {
            if (savedInstanceState.containsKey(STATE_SELECTED_TAB_INDEX)) {
                savedTabIdx = savedInstanceState.getInt(STATE_SELECTED_TAB_INDEX);
            }
        }
        return savedTabIdx;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_TAB_INDEX)) {
            if (null != bottomNavigation) {
                int savedTabIdx = savedInstanceState.getInt(STATE_SELECTED_TAB_INDEX);
                bottomNavigation.setCurrentItem(savedTabIdx);
            }
        }
    }

    private void initFragment(Bundle savedInstanceState) {
        fragments.add(HomeListFragment.newInstance(0));
        fragments.add(ChatFragment.newInstance("ss", "ss"));
        fragments.add(FindFragment.newInstance("",""));
        fragments.add(UserInfoFragment.newInstance("sss", "sss"));

        if (savedInstanceState == null) {
            showFragment(fragments.get(0));
        }
    }

    private void initAHBottomNavigation(Bundle savedInstanceState) {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_nocolors);
        navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottomnavigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);

        if (savedInstanceState == null) {
            checkAutoDayNightMode();
        }
        if (Colorful.getThemeDelegate().isNight())
        {
            bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(HomeActivity.this,
                    R.color.color_ahbn_background_night));

            bottomNavigation.setAccentColor(ContextCompat.getColor(HomeActivity.this,
                    Colorful.getThemeDelegate().getAccentColor().getColorRes()));

            bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        }else {

            bottomNavigation.setAccentColor(ContextCompat.getColor(HomeActivity.this,
                    Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
            bottomNavigation.setInactiveColor(Color.parseColor("#A0A0A0"));
        }


        bottomNavigation.setForceTint(true);
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        //默认启动时 选择的Item ，默认选择第一页
        bottomNavigation.setCurrentItem(0);

       /* //为每个项目添加或删除通知
        bottomNavigation.setNotification("1+", 1);*/
        //右上角数字的背景颜色
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentTabIndex != position) {
                    FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                    trx.hide(fragments.get(currentTabIndex));
                    if (!fragments.get(position).isAdded()) {
                        trx.add(R.id.main_content, fragments.get(position));
                    }
                    trx.show(fragments.get(position)).commit();
                }
                currentTabIndex = position;
                if (position == 1) {
                    //隐藏通知
                    bottomNavigation.setNotification(0, 1);
                }
                return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override
            public void onPositionChange(int y) {
                // Manage the new y position
            }
        });

    }

    private void showFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
    }

    /**
     * 检测是否自动日夜模式，如果是自动将根据时间判断是否切换
     */
    private void checkAutoDayNightMode() {
        boolean firstTime = PrefUtil.checkFirstTime();
        if (firstTime)
            PrefUtil.setNotFirstTime();
        boolean auto = PrefUtil.isAutoDayNightMode();
        if (firstTime || !auto)
            return;
        int[] dayTime = PrefUtil.getDayNightModeStartTime(true);
        int[] nightTime = PrefUtil.getDayNightModeStartTime(false);
        Calendar cal = Calendar.getInstance();
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int m = cal.get(Calendar.MINUTE);
        if (Colorful.getThemeDelegate().isNight()) {
            if ((dayTime[0] < h && h < nightTime[0]) ||
                    (dayTime[0] == h && dayTime[1] <= m)) {
                switchDayNightModeSmoothly(false, true);
            }
        } else {
            if ((nightTime[0] < h) || (nightTime[0] == h && nightTime[1] <= m)) {
                switchDayNightModeSmoothly(true, true);
            }
        }
    }
    private void switchDayNightModeSmoothly(final boolean isDark, boolean delay) {
        if (delay) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Colorful.config(HomeActivity.this).night(isDark).apply();
                    getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                    mAutoSwitchedHint = "已自动切换为" +
                            (isDark ? getString(R.string.night_mode) : getString(R.string.day_mode));
                    recreate();
                }
            }, 1000);
        } else {
            Colorful.config(HomeActivity.this).night(isDark).apply();
            getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
            recreate();
        }
    }

    long mStartMills;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mStartMills > 1000) {
            showToast("再按一次，退出程序");
            mStartMills = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onResume() {
       super.onResume();
        //显示小红点
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
        unregisterReceiver(mReceiver);

    }

    /**注册消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        checkRedPoint();
    }

    /**注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        checkRedPoint();
    }

    /**注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        PLog.i("---主页接收到自定义消息---");
        checkRedPoint();
    }

    private void checkRedPoint(){
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if(count>0)
        {
            bottomNavigation.setNotification(count+"+", 1);
        }
    }

}

