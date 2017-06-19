package com.bzu.yhd.pocketcampus.bottomnav.find;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.model.Ushare;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.CommentActivity;
import com.bzu.yhd.pocketcampus.main.LoginActivity;
import com.bzu.yhd.pocketcampus.widget.MConstant;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MyTopicFragment extends BaseHomeFragment
		implements OnClickListener
		{
		;
		private PullToRefreshListView mPullToRefreshListView;
		private ListView mListView;

		private ArrayList<Ushare> mUshares;
		private PersonCenterContentAdapter mAdapter;

		private User mUser;

		private int pageNum;
		private String  deleteId;
		public static final int EDIT_USER = 1;

		public enum RefreshType
			{
				REFRESH, LOAD_MORE
			}

		private RefreshType mRefreshType = RefreshType.LOAD_MORE;

		public static MyTopicFragment newInstance()
			{
				MyTopicFragment fragment = new MyTopicFragment();
				return fragment;
			}

		@Override
		protected int getLayoutId()
			{
				// TODO Auto-generated method stub
				return R.layout.activity_my_topic;
			}

		@Override
		protected void findViews(View view)
			{
				// TODO Auto-generated method stub
				mPullToRefreshListView = (PullToRefreshListView) view
						.findViewById(R.id.pull_refresh_list_personal);
			}

		@Override
		protected void setupViews(Bundle bundle)
			{
				// TODO Auto-generated method stub
				mUser = BaseApplication.getInstance().getCurrentUser();
				if(mUser==null)
				{
					Toast.makeText(getActivity(), "请先登录..", Toast.LENGTH_SHORT)
					.show();
					Intent i=new Intent();
					i.setClass(getActivity(), LoginActivity.class);
					getActivity().startActivity(i);
				}
				initMyPublish();

			}

		private void initMyPublish()
			{

				mPullToRefreshListView.setMode(Mode.BOTH);
				mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>()
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
								BaseApplication.getInstance().setCurrentUshare(
										mUshares.get(position - 1));
								Intent intent = new Intent();
								intent.setClass(getActivity(),
										CommentActivity.class);
								intent.putExtra("data",
										mUshares.get(position - 1));
								startActivity(intent);
							}
					});
				mListView.setOnItemLongClickListener(new OnItemLongClickListener() {  
				            @Override  
				            public boolean onItemLongClick(AdapterView<?> parent, View view,  
				                    int position, long id) {  
				            		Ushare q= mUshares.get(position - 1);
				            		
				            		deleteId=q.getObjectId();
				            	
				            		AlertDialog ad = new AlertDialog.Builder(getActivity())
				            				.setMessage("确认删除说说信息？")
				            				.setPositiveButton("确定",new DialogInterface.OnClickListener() 
				            					{
						            				public void onClick(DialogInterface dialog,int which) 
						            					{
						            						//这里写上按确认键你要做的事情
						            						deleteTalk(deleteId);
							            				}
				            					})
				            				.setNegativeButton("取消",new DialogInterface.OnClickListener() 
				            					{
						            				public void onClick(DialogInterface dialog,
						            				int which)
						            					{
							            				}
				            					})
				            				.create();
				            		
				            				ad.show();
				            		return true;  
				            }  
				        });  
				
			}

		private void deleteTalk(String id)
			{
				String did=id;
				Ushare de = new Ushare();
				de.setObjectId(did);
				de.delete(new UpdateListener() {
					@Override
					public void done(BmobException e) {
						if(e==null){
							Toast.makeText(getActivity(),"删除成功~", Toast.LENGTH_SHORT).show();
						}else {
							Toast.makeText(getActivity(),"删除失败。"+ e.getMessage(), Toast.LENGTH_SHORT).show();

						}
					}
				});
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
				getPublishion();
			}

		private void getPublishion()
			{
				mIProgressControllor.showActionBarProgress();
				BmobQuery<Ushare> query = new BmobQuery<Ushare>();
				query.setLimit(MConstant.NUMBERS_PER_PAGE);
				query.setSkip(MConstant.NUMBERS_PER_PAGE * (pageNum++));
				query.order("-createdAt");
				query.include("author");
				query.addWhereEqualTo("author", mUser);
				query.findObjects(new FindListener<Ushare>()
					{

						@Override
						public void done(List<Ushare> data, BmobException e) {
							if(e==null){

								mIProgressControllor.hideActionBarProgress();
								if (data.size() != 0
										&& data.get(data.size() - 1) != null)
								{
									if (mRefreshType == RefreshType.REFRESH) {
										mUshares.clear();
									}

									if (data.size() < MConstant.NUMBERS_PER_PAGE) {
										Toast.makeText(getActivity(),
												"已加载完所有数据~", Toast.LENGTH_SHORT).show();
									}

									mUshares.addAll(data);
									mAdapter.notifyDataSetChanged();
									mPullToRefreshListView.onRefreshComplete();
								} else {
									Toast.makeText(getActivity(), "暂无更多数据~", Toast.LENGTH_SHORT)
											.show();

									pageNum--;
									mPullToRefreshListView.onRefreshComplete();
								}
							}else {
								mIProgressControllor.hideActionBarProgress();
								LogUtils.i(TAG, "find failed." + e.getMessage());
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

					default:
						break;
					}
			}

		@Override
		public void onAttach(Activity activity)
			{
				// TODO Auto-generated method stub
				super.onAttach(activity);
				try {
					mIProgressControllor = (IProgressControllor) activity;
				} catch (ClassCastException e) {
					e.printStackTrace();
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
				mIProgressControllor.showActionBarProgress();
				mIProgressControllor.hideActionBarProgress();
			}

		public interface IProgressControllor
			{
				void showActionBarProgress();

				void hideActionBarProgress();
			}

		private IProgressControllor mIProgressControllor;

	}
