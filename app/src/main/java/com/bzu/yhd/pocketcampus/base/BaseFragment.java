package com.bzu.yhd.pocketcampus.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.widget.MConstant;
import com.bzu.yhd.pocketcampus.widget.Sputil;
import com.trello.rxlifecycle.components.support.RxFragment;

import cn.bmob.v3.Bmob;

/**
 * Created by HugoXie on 16/7/9.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
public abstract class BaseFragment extends RxFragment {

    protected boolean mIsCreateView = false;
    public static String TAG;
    protected Context mContext;
    protected Sputil sputil;
    protected int mScreenWidth;
    protected int mScreenHeight;
    //requestWindowFeature(Window.FEATURE_NO_TITLE);
    //获取当前屏幕宽高
    DisplayMetrics metric = new DisplayMetrics();

    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Bmob.initialize(getActivity(), MConstant.BMOB_APP_ID);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        mContext = getActivity();

        TAG = this.getClass().getSimpleName();
        mContext = getActivity();
        if(null == sputil){
            sputil = new Sputil(mContext, MConstant.PRE_NAME);
        }
    }
    /**
     * 加载数据操作,在视图创建之前初始化
     */
    Toast mToast;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    /**启动指定Activity
     * @param target
     * @param bundle
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), target);
        if (bundle != null)
            intent.putExtra(getActivity().getPackageName(), bundle);
        getActivity().startActivity(intent);
    }
    protected void safeSetTitle(String title) {
        ActionBar appBarLayout = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (appBarLayout != null) {
            appBarLayout.setTitle(title);
        }
    }
    public void ShowToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getActivity().getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    public  int getStateBar(){
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    public static int dip2px(Context context,float dipValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int) (scale*dipValue+0.5f);
    }



    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //MobclickAgent.onPageEnd(TAG);
    }
    public void setContentView() {
        // TODO Auto-generated method stub
    }
}

