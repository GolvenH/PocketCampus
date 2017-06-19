package com.bzu.yhd.pocketcampus.bottomnav.find;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.yhd.pocketcampus.base.BaseFragment;

public abstract class BaseHomeFragment extends BaseFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(getLayoutId(),container,false);
		findViews(contentView);
		setupViews(savedInstanceState);
		setListener();
		fetchData();
		return contentView;
	}

	protected abstract int getLayoutId();

	protected abstract void findViews(View view);

	protected abstract void setupViews(Bundle bundle);

	protected abstract void setListener();

	protected abstract void fetchData();

}
