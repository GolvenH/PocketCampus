package com.bzu.yhd.pocketcampus.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.oubowu.slideback.SlideBackHelper;
import com.oubowu.slideback.SlideConfig;

import org.polaric.colorful.ColorfulActivity;

public class BaseActivity extends ColorfulActivity {
    
    private static String TAG = BaseActivity.class.getSimpleName();



    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeSlideBack();
    }

    /**
     * 初始化滑动返回
     */
    private void initializeSlideBack() {
        SlideBackHelper.attach(
                this, // 当前Activity
                BaseApplication.getActivityHelper(), // Activity栈管理工具
                new SlideConfig.Builder() // 参数的配置
                        .rotateScreen(false) // 屏幕是否旋转
                        .edgeOnly(true) // 是否侧滑
                        .lock(false) // 是否禁止侧滑
                        .edgePercent(0.1f) // 边缘滑动的响应阈值，0~1，对应屏幕宽度*percent
                        .slideOutPercent(0.5f) // 关闭页面的阈值，0~1，对应屏幕宽度*percent
                        .create(),
                null); // 滑动的监听
    }

    /**
     * Activity跳转导航
     *
     * @param activity 目标Activity.class
     */
    protected void navigateTo(Class activity) {
        startActivity(new Intent(this, activity));
    }

    /**
     * 检测系统版本并使状态栏全透明
     */
    protected void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 初始化Toolbar的功能
     */
    protected void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startActivity(Class<? extends Activity> target, Bundle bundle, boolean finish) {
        Intent intent = new Intent();
        intent.setClass(this, target);
        if (bundle != null) intent.putExtra(getPackageName(), bundle);
        startActivity(intent);
        if (finish)
            finish();
    }

    protected void initView() {}
}
