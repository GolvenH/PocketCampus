package com.bzu.yhd.pocketcampus.bottomnav.home.circle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.model.Ushare;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.widget.MConstant;
import com.bzu.yhd.pocketcampus.widget.utils.ActivityUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PersontalkFragment extends BaseHomeFragment
		implements OnClickListener
	{

		private PullToRefreshListView mPullToRefreshListView;
		private ListView mListView;

		private ArrayList<Ushare> mUshares;
		private PersonCenterContentAdapter mAdapter;

		private User mUser;

		private int pageNum;

		public static final int EDIT_USER = 1;

		public enum RefreshType
			{
				REFRESH, LOAD_MORE
			}

		private RefreshType mRefreshType = RefreshType.LOAD_MORE;

		public static PersontalkFragment newInstance()
			{
				PersontalkFragment fragment = new PersontalkFragment();
				return fragment;
			}

		@Override
		protected int getLayoutId()
			{
				// TODO Auto-generated method stub
				return R.layout.fragment_personal_talk;
			}

		@Override
		protected void findViews(View view)
			{		
				mPullToRefreshListView = (PullToRefreshListView) view
						.findViewById(R.id.pull_refresh_list_personal);
			}

		@Override
		protected void setupViews(Bundle bundle)
			{
				// TODO Auto-generated method stub
				mUser = BaseApplication.getInstance().getCurrentUshare()
						.getAuthor();


				initMyPublish();

			}

		private void initMyPublish()
			{

				mPullToRefreshListView.setMode(Mode.BOTH);
				mPullToRefreshListView
						.setOnRefreshListener(new OnRefreshListener2<ListView>()
							{

								@Override
								public void onPullDownToRefresh(
										PullToRefreshBase<ListView> refreshView)
									{
										// TODO Auto-generated method stub
										String label = DateUtils.formatDateTime(
												getActivity(),
												System.currentTimeMillis(),
												DateUtils.FORMAT_SHOW_TIME
														| DateUtils.FORMAT_SHOW_DATE
														| DateUtils.FORMAT_ABBREV_ALL);
										refreshView.getLoadingLayoutProxy()
												.setLastUpdatedLabel(label);
										mRefreshType = RefreshType.REFRESH;
										pageNum = 0;
										fetchData();
									}

								@Override
								public void onPullUpToRefresh(
										PullToRefreshBase<ListView> refreshView)
									{
										// TODO Auto-generated method stub
										mRefreshType = RefreshType.LOAD_MORE;
										fetchData();
									}
							});
				mPullToRefreshListView.setOnLastItemVisibleListener(
						new OnLastItemVisibleListener()
							{

								@Override
								public void onLastItemVisible()
									{
										// TODO Auto-generated method stub

									}
							});
				mListView = mPullToRefreshListView.getRefreshableView();
				mUshares = new ArrayList<Ushare>();
				mAdapter = new PersonCenterContentAdapter(mContext, mUshares);
				mListView.setAdapter(mAdapter);
				mListView.setOnItemClickListener(new OnItemClickListener()
					{

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id)
							{
								// TODO Auto-generated method stub
								// BaseApplication.getInstance().setCurrentUshare(mUshares.get(position-1));
								Intent intent = new Intent();
								intent.setClass(getActivity(),
										CommentActivity.class);
								intent.putExtra("data",
										mUshares.get(position - 1));
								startActivity(intent);
							}
					});
			}

		

		/**
		 * 判断点击条目的用户是否是当前登录用户
		 * 
		 * @return
		 */
		private boolean isCurrentUser(User user)
			{
				if (null != user) {
					User cUser = BmobUser.getCurrentUser(User.class);
					if (cUser != null
							&& cUser.getObjectId().equals(user.getObjectId()))
					{
						return true;
					}
				}
				return false;
			}

		@Override
		protected void setListener()
			{
				
			}

		@Override
		protected void fetchData()
			{
				getPublishion();
			}

		private void getPublishion()
			{
				BmobQuery<Ushare> query = new BmobQuery<Ushare>();
				query.setLimit(MConstant.NUMBERS_PER_PAGE);
				query.setSkip(MConstant.NUMBERS_PER_PAGE * (pageNum++));
				query.order("-createdAt");
				query.include("author");
				query.addWhereEqualTo("author", mUser);
				query.findObjects( new FindListener<Ushare>()
					{

						@Override
						public void done(List<Ushare> list, BmobException e)
						{
							if(e==null)
							{
								if (list.size() != 0
										&& list.get(list.size() - 1) != null)
								{
									if (mRefreshType == RefreshType.REFRESH) {
										mUshares.clear();
									}

									if (list.size() < MConstant.NUMBERS_PER_PAGE) {
										ActivityUtil.show(getActivity(),
												"已加载完所有数据~");
									}

									mUshares.addAll(list);
									mAdapter.notifyDataSetChanged();
									mPullToRefreshListView.onRefreshComplete();
								} else {
									ActivityUtil.show(getActivity(), "暂无更多数据~");
									pageNum--;
									mPullToRefreshListView.onRefreshComplete();
								}
							}else {
								LogUtils.i(TAG, "circle failed." + e.getMessage());
								pageNum--;
								mPullToRefreshListView.onRefreshComplete();
							}
						}
					});
			}

		@Override
		public void onClick(View v)
			{
				// TODO Auto-generated method stub
				switch (v.getId())
					{
				
					}
			}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data)
			{
				// TODO Auto-generated method stub
				super.onActivityResult(requestCode, resultCode, data);
				if (resultCode == Activity.RESULT_OK) {
					switch (requestCode)
						{
						case EDIT_USER:
							getCurrentUserInfo();
							pageNum = 0;
							mRefreshType = RefreshType.REFRESH;
							getPublishion();
							break;

						default:
							break;
						}
				}
			}

		/**
		 * 查询当前用户具体信息
		 */
		private void getCurrentUserInfo()
			{
				User user = BmobUser.getCurrentUser(User.class);
				LogUtils.i(TAG,	"sign:" + user.getSignature() + "sex:" + user.getSex());
				Toast.makeText(mContext, "更新信息成功。",Toast.LENGTH_SHORT).show();
			}
	}
