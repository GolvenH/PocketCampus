package com.bzu.yhd.pocketcampus.bottomnav.find;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.bzu.yhd.pocketcampus.model.Ushare;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.CommentActivity;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter.AIContentAdapter;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter.BaseContentAdapter;

public class FavFragment extends BaseContentFragment
{

	public static FavFragment newInstance()
	{
		FavFragment fragment = new FavFragment();
		return fragment;
	}

	@Override
	public BaseContentAdapter<Ushare> getAdapter()
	{
		// TODO Auto-generated method stub
		return new AIContentAdapter(mContext, mListItems);
	}

	@Override
	public void onListItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(getActivity(), CommentActivity.class);
		intent.putExtra("data", mListItems.get(position - 1));
		startActivity(intent);
	}
}
