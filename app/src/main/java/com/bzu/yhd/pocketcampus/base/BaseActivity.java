package com.bzu.yhd.pocketcampus.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.widget.utils.PLog;
import com.oubowu.slideback.SlideBackHelper;
import com.oubowu.slideback.SlideConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.polaric.colorful.ColorfulActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseActivity extends ColorfulActivity {
    
    private static String TAG = BaseActivity.class.getSimpleName();
    private Unbinder mUnbinder;
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mUnbinder = ButterKnife.bind(this);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        mUnbinder = ButterKnife.bind(this);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Subscribe
    public void onEvent(Boolean empty){

    }


    protected void runOnMain(Runnable runnable) {
        runOnUiThread(runnable);
    }

    protected final static String NULL = "";
    private Toast toast;
    public void toast(final Object obj) {
        try {
            runOnMain(new Runnable() {

                @Override
                public void run() {
                    if (toast == null)
                        toast = Toast.makeText(BaseActivity.this, NULL,Toast.LENGTH_SHORT);
                    toast.setText(obj.toString());
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Class<? extends Activity> target, Bundle bundle,boolean finish) {
        Intent intent = new Intent();
        intent.setClass(this, target);
        if (bundle != null)
            intent.putExtra(getPackageName(), bundle);
        startActivity(intent);
        if (finish)
            finish();
    }

    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName()))
            return getIntent().getBundleExtra(getPackageName());
        else
            return null;
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**隐藏软键盘-一般是EditText.getWindowToken()
     * @param token
     */
    public void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**Log日志
     * @param msg
     */
    public void log(String msg){
        if(Config.DEBUG){
            PLog.i(msg);
        }
    }

    protected void initView() {}
}
