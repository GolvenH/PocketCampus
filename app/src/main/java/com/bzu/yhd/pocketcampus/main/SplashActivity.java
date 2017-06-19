package com.bzu.yhd.pocketcampus.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bzu.yhd.pocketcampus.widget.utils.SharedPrefHelper;

import org.polaric.colorful.Colorful;

import java.lang.ref.WeakReference;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * Created by hugo on 2015/10/25 0025.
 * 闪屏页
 * @see <a herf="http://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html">How to Leak a Context: Handlers & Inner Classes</a>
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private SwitchHandler mHandler = new SwitchHandler(this);
    private Handler mHandlers;
    Boolean user_first;
    SharedPreferences setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //activity切换的淡入淡出效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);


        setting = getSharedPreferences("com.bzu.yhd.pocketcampus", 0);
        user_first = setting.getBoolean("FIRST", true);

        Bmob.initialize(this, "1fbf365963748879b31dcef9fdbe38f8","bmob");


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
        mHandler.sendEmptyMessageDelayed(1, 1000);
        /**
         * 三方初始化放入工作线程，加速App启动
         */
        new Thread() {
            @Override
            public void run() {
                super.run();
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                Colorful.defaults()
                        .primaryColor(Colorful.ThemeColor.BLUE)
                        .accentColor(Colorful.ThemeColor.LIME)
                        .translucent(false)
                        .night(false);
                Colorful.init(getApplicationContext());

                SharedPrefHelper.init(getApplicationContext());
            }
        }.start();
    }

    private class SwitchHandler extends Handler {
        private WeakReference<SplashActivity> mWeakReference;

        SwitchHandler(SplashActivity activity) {
            mWeakReference = new WeakReference<SplashActivity>(activity);
        }



        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mWeakReference.get();

            if (activity != null)
            {
                BmobUser bmobUser = BmobUser.getCurrentUser();
                if(bmobUser != null){
                    // 允许用户使用应用
                    Intent i=new Intent();
                    i.setClass(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    activity.finish();
                }else{
                    //缓存用户对象为空时， 可打开用户注册界面…
                    Intent in=new Intent();
                    if (user_first) {// 第一次则跳转到欢迎页面
                        setting.edit().putBoolean("FIRST", false).commit();

                        in.setClass(SplashActivity.this, MainActivity.class);
                        startActivity(in);
                        activity.finish();
                    } else {//如果是第二次启动则直接跳转到主页面
                        in.setClass(SplashActivity.this, LoginActivity.class);
                        startActivity(in);
                        activity.finish();
                    }

                }

            }
        }
    }
}