package com.bzu.yhd.pocketcampus.bottomnav.find;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.widget.MConstant;
import com.bzu.yhd.pocketcampus.widget.Sputil;

public class BaseActivity extends FragmentActivity implements OnSharedPreferenceChangeListener{
	
	protected static String TAG ;
	
	protected BaseApplication mMyApplication;
	protected Sputil sputil;
	protected Resources mResources;
	protected Context mContext;

	protected int mScreenWidth;
	protected int mScreenHeight;
	
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		initConfigure();

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}
	


	private void initConfigure() {
		mContext = this;
		if(null == mMyApplication){
			mMyApplication = BaseApplication.getInstance().getInstance();
		}
		mMyApplication.addActivity(this);
		if(null == sputil){
			sputil = new Sputil(this, MConstant.PRE_NAME);
		}
		sputil.getInstance().registerOnSharedPreferenceChangeListener(this);
		mResources = getResources();
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		//可用于监听设置参数，然后作出响应
	}

	/**
	 * Activity跳转
	 * @param context
	 * @param targetActivity
	 * @param bundle
	 */
	public void redictToActivity(Context context,Class<?> targetActivity,Bundle bundle){
		Intent intent = new Intent(context, targetActivity);
		if(null != bundle){
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		MobclickAgent.onPause(this);
	}
	
	
	Toast mToast;

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								Toast.LENGTH_LONG);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});
			
		}
	}

	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mToast == null) {
					mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}
}
