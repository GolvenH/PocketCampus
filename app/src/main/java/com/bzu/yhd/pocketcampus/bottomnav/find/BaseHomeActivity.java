package com.bzu.yhd.pocketcampus.bottomnav.find;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;
import com.bzu.yhd.pocketcampus.base.BaseFragment;
import com.bzu.yhd.pocketcampus.widget.MConstant;
import com.markupartist.android.widget.ActionBar;

import cn.bmob.v3.Bmob;

public abstract class BaseHomeActivity extends BaseActivity
	{

		protected ActionBar actionBar;

		@Override
		protected void onCreate(Bundle bundle)
			{
				getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				super.onCreate(bundle);
				Bmob.initialize(this, MConstant.BMOB_APP_ID);
				setContentView(R.layout.activity_base_home);

				initActionBar();

				initFragment();
			}

		private void initActionBar()
			{
				actionBar = (ActionBar) findViewById(R.id.actionbar_base);
				actionBar.setTitle(getActionBarTitle());
				actionBar.setDisplayHomeAsUpEnabled(isHomeAsUpEnabled() == true ? true : false);
				actionBar.setHomeAction(new ActionBar.Action()
					{
						@Override
						public void performAction(View view)
							{
								onHomeActionClick();
							}
						@Override
						public int getDrawable()
							{
								return R.drawable.ic_launcher;
							}
					});
				addActions();
			}

		private void initFragment()
			{
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.add(R.id.content_frame_base, getFragment()).commit();
			}

		protected abstract String getActionBarTitle();

		protected abstract boolean isHomeAsUpEnabled();

		protected abstract void onHomeActionClick();

		protected abstract BaseFragment getFragment();

		protected abstract void addActions();

	}
