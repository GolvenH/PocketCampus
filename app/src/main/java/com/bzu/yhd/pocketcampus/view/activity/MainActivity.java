package com.bzu.yhd.pocketcampus.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.presenter.IMainActivityPresenter;
import com.bzu.yhd.pocketcampus.presenter.impl.MainActivityPresenter;
import com.bzu.yhd.pocketcampus.util.PrefUtil;
import com.bzu.yhd.pocketcampus.view.adapter.TabPagerAdapter;
import com.bzu.yhd.pocketcampus.view.fragment.MovieListFragment;
import com.bzu.yhd.pocketcampus.view.iview.IMainActivity;
import com.bzu.yhd.pocketcampus.view.service.LocationService;

import org.polaric.colorful.Colorful;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页
 * <p/>
 * Created by woxingxiao on 2017-01-25.
 */
public class MainActivity extends CheckPermissionsActivity implements NavigationView
        .OnNavigationItemSelectedListener, AppBarLayout.OnOffsetChangedListener, IMainActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private SwitchCompat mSwitch;
    private TextView mCityText;

    private MyReceiver mReceiver;
    private String mAutoSwitchedHint;
    private boolean isCollapsed = false; // AppBar是否折叠

    private IMainActivityPresenter mPresenter;

    public static void navigation(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transparentStatusBar();
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mPresenter = new MainActivityPresenter(this);
        mPresenter.loadMovieData();

        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, new IntentFilter(getString(R.string.action_locate_succeed)));

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAppBar.addOnOffsetChangedListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCollapsed) return;

                mAppBar.setExpanded(true, true);

                String tag = "android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment != null && fragment instanceof MovieListFragment) {
                    ((MovieListFragment) fragment).scrollToTop();
                }
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        mSwitch = (SwitchCompat) navView.getHeaderView(0).findViewById(R.id.day_night_mode_switch);
        mCityText = (TextView) navView.getHeaderView(0).findViewById(R.id.nav_city_text);
        ImageView img = (ImageView) navView.getHeaderView(0).findViewById(R.id.nav_header_img);
        Glide.with(this).load(R.drawable.pic_movies).into(img);

        MovieListFragment[] fragments = new MovieListFragment[2];
        fragments[0] = MovieListFragment.newInstance(0);
        fragments[1] = MovieListFragment.newInstance(1);

        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);
        adapter.setTabTitles(new String[]{getString(R.string.has_released), getString(R.string.going_to_release)});
        mViewPager.setAdapter(adapter);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
        mCityText.setText(PrefUtil.getCityShort());
        mCityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocatedCityDialog(false, false);
            }
        });

        if (savedInstanceState == null) {
            LocationService.start(this);

            checkAutoDayNightMode();
        } else {
            String hint = savedInstanceState.getString("hint", null);
            if (hint != null) {
                Snackbar.make(mAppBar, hint, 2000)
                        .setAction(getString(R.string.revoke), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PrefUtil.setAutoDayNightMode(false);
                                switchDayNightModeSmoothly(!Colorful.getThemeDelegate().isNight(), false);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSwitch.setChecked(Colorful.getThemeDelegate().isNight());
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDrawerLayout.addDrawerListener(new MyDrawerListener());
                mDrawerLayout.closeDrawer(GravityCompat.START);

                if (PrefUtil.isAutoDayNightMode()) {
                    Toast.makeText(MainActivity.this, getString(R.string.hint_auto_day_night_disabled),
                            Toast.LENGTH_LONG).show();
                    PrefUtil.setAutoDayNightMode(false);
                }
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        isCollapsed = verticalOffset < 0; // 监听AppBar是否被折叠
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.action_box_office);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(item);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_box_office) {
            navigateTo(BoxOfficeActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_theme:
                navigateTo(ThemeActivity.class);
                break;
            case R.id.nav_settings:
                navigateTo(SettingsActivity.class);
                break;
            case R.id.nav_share: // 分享
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_out_description));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "分享"));
                break;
            case R.id.nav_evaluate: // 评分
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent2, "评价"));
                break;
            case R.id.nav_about:
                navigateTo(AboutActivity.class);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                    Colorful.config(MainActivity.this).night(isDark).apply();
                    getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                    mAutoSwitchedHint = "已自动切换为" +
                            (isDark ? getString(R.string.night_mode) : getString(R.string.day_mode));
                    recreate();
                }
            }, 1000);
        } else {
            Colorful.config(MainActivity.this).night(isDark).apply();
            getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
            recreate();
        }
    }

    @Override
    protected void onAllPermissionsGranted() {
        LocationService.start(this);
    }

    @Override
    public void onFragmentRefreshRequest(int fragmentId) {
        mPresenter.onFragmentRefreshCheckData(fragmentId);
    }

    @Override
    public void onFragmentInitOK(int fragmentId) {
        mPresenter.onFragmentInitOK(fragmentId);
    }

    @Override
    public void onFragmentRefreshDataReady(int fragmentId) {
        String tag = "android:switcher:" + mViewPager.getId() + ":" + fragmentId;
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null && fragment instanceof MovieListFragment) {
            ((MovieListFragment) fragment).onDataReady(mPresenter.getMovieModels(fragmentId));
        }
    }

    @Override
    public void onDataError(int code, String msg) {
        if (code == 209405) { // "查询不到热映电影相关信息"，以上一级城市名进行查询
            showLocatedCityDialog(false, true);
            return;
        }

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment instanceof MovieListFragment) {
                    ((MovieListFragment) fragment).onDataError();
                }
            }
        }
        showToast(msg);
    }

    long mStartMills;

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - mStartMills > 1000) {
                showToast("再按一次，退出程序");
                mStartMills = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("hint", mAutoSwitchedHint);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPresenter.unregister();
        unregisterReceiver(mReceiver);
    }

    private class MyDrawerListener implements DrawerLayout.DrawerListener {

        @Override
        public void onDrawerClosed(View drawerView) {
            switchDayNightModeSmoothly(mSwitch.isChecked(), false);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    }

    /**
     * 监听定位完成广播
     */
    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (getString(R.string.action_locate_succeed).equals(intent.getAction())) {
                mCityText.setText(PrefUtil.getCityShort());

                showLocatedCityDialog(true, intent.getBooleanExtra(getString(R.string.param_is_upper_city), false));
            }
        }
    }

    private void showLocatedCityDialog(final boolean refresh, final boolean upperCity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.location_default));
        if (upperCity) {
            builder.setMessage(getString(R.string.hint_query_by_upper_city, PrefUtil.getCity(),
                    PrefUtil.getUpperCity()));
        } else {
            builder.setMessage(getString(R.string.hint_located_city, PrefUtil.getCity()));
        }
        builder.setNegativeButton(getString(R.string.locate_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PrefUtil.clearCity();
                LocationService.start(MainActivity.this);
            }
        });
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (upperCity) {
                    PrefUtil.setCity(PrefUtil.getUpperCity());
                    mCityText.setText(PrefUtil.getCityShort());
                }
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment instanceof MovieListFragment) {
                            if (refresh) {
                                ((MovieListFragment) fragment).onRefresh();
                            } else {
                                ((MovieListFragment) fragment).onDataError();
                            }
                        }
                    }
                }
            }
        });
        builder.show();
    }
}
