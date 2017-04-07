package com.bzu.yhd.pocketcampus.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.util.PrefUtil;
import com.bzu.yhd.pocketcampus.view.base.CheckPermissionsActivity;
import com.bzu.yhd.pocketcampus.view.fragment.FirstFragment;
import com.bzu.yhd.pocketcampus.view.fragment.SecondFragment;
import com.bzu.yhd.pocketcampus.view.bottom.user.UserInfoFragment;
import com.bzu.yhd.pocketcampus.view.bottom.home.HomeListFragment;

import org.polaric.colorful.Colorful;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 首页
 * </p>
 * @CreateBy Yhd On 2017/2/15 8:40
 */
public class HomeActivity extends CheckPermissionsActivity {
    private List<Fragment> fragments = new ArrayList<>();
    private int currentTabIndex;
    private String mAutoSwitchedHint;

    private int[] tabColors;
    private AHBottomNavigationAdapter navigationAdapter;
    private AHBottomNavigation bottomNavigation;
    private static final String STATE_SELECTED_TAB_INDEX = "selected_tab_index";
    private int mPreSelectedTabIdx = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initAHBottomNavigation(savedInstanceState);
        initFragment(savedInstanceState);

        mPreSelectedTabIdx = restoreSavedSelectedTabIdx(savedInstanceState);
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
        fragments.add(FirstFragment.newInstance(0));
        fragments.add(SecondFragment.newInstance("ss", "ss"));
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

        //为每个项目添加或删除通知
        bottomNavigation.setNotification("99+", 2);
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
                if (position == 2) {
                    //隐藏通知
                    bottomNavigation.setNotification(0, 2);
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
            if ((dayTime[0] < h && h < nightTime[0]) || (dayTime[0] == h && dayTime[1] <= m)) {
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
    }
}
