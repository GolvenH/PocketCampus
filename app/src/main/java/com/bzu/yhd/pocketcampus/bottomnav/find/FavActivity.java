package com.bzu.yhd.pocketcampus.bottomnav.find;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BasePageActivity;


public class FavActivity extends BasePageActivity
{

	@Override
	protected void setLayoutView()
	{
		setContentView(R.layout.activity_fav);
	}

	@Override
	protected void findViews()
	{

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		initializeToolbar();
		toolbar.setTitle("我的收藏");
	}

	@Override
	protected void setupViews(Bundle bundle)
	{

		setInitialFragment();
	}

	private void setInitialFragment()
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.content_frame_fav, FavFragment.newInstance()).commit();
	}

	@Override
	protected void setListener()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void fetchData()
	{
		// TODO Auto-generated method stub

	}

}
