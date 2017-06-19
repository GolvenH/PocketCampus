package com.bzu.yhd.pocketcampus.bottomnav.find;

import android.view.View;

import com.bzu.yhd.pocketcampus.base.BaseFragment;


public class MyTopicActivity extends BaseHomeActivity implements MyTopicFragment.IProgressControllor {

	@Override
	protected String getActionBarTitle() {
		// TODO Auto-generated method stub
		return "个人中心";
	}

	@Override
	protected boolean isHomeAsUpEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void onHomeActionClick() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	protected BaseFragment getFragment() {
		// TODO Auto-generated method stub
		return MyTopicFragment.newInstance();
	}

	@Override
	protected void addActions() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void showActionBarProgress(){
		actionBar.setProgressBarVisibility(View.VISIBLE);
	}
	
	@Override
	public void hideActionBarProgress(){
		actionBar.setProgressBarVisibility(View.GONE);
	}
}
