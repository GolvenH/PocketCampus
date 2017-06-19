package com.bzu.yhd.pocketcampus.bottomnav.home.circle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.base.BaseFragment;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter.AIContentAdapter;
import com.bzu.yhd.pocketcampus.widget.db.DatabaseUtil;
import com.bzu.yhd.pocketcampus.model.Ushare;
import com.bzu.yhd.pocketcampus.widget.MConstant;
import com.bzu.yhd.pocketcampus.widget.utils.ActivityUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class DongtaiFragment extends BaseFragment
{

		private View contentView;
		private int pageNum;
		private String lastItemTime;// 当前列表结尾的条目的创建时间，
		private ArrayList<Ushare> mListItems;
		private PullToRefreshListView mPullRefreshListView;
		private AIContentAdapter mAdapter;
		private ListView actualListView;
		private TextView networkTips;
		private ProgressBar progressbar;
		private int  Login=0 ;

	public static DongtaiFragment newInstance(String param1, String param2) {
		DongtaiFragment fragment = new DongtaiFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	public enum RefreshType
		{
			REFRESH, LOAD_MORE
		}

		private RefreshType mRefreshType = RefreshType.LOAD_MORE;

		public static BaseFragment newInstance(int index)
		{
			BaseFragment fragment = new DongtaiFragment();
			Bundle args = new Bundle();
			args.putInt("page", index);
			fragment.setArguments(args);
			return fragment;
		}

		private String getCurrentTime()
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String times = formatter.format(new Date(System.currentTimeMillis()));
			return times;
		}

		@Override
		public void onAttach(Activity activity)
		{
			super.onAttach(activity);
		}

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
//			currentIndex = getArguments().getInt("page");
			pageNum = 0;
			lastItemTime = getCurrentTime();
			LogUtils.i("curent time:" + lastItemTime);
			
			BmobUser bmobUser = BmobUser.getCurrentUser();
			if(bmobUser != null){
			    // 允许用户使用应用
				Login=1 ;
			}else{
			    //缓存用户对象为空时， 可打开用户注册界面…
				Login=0 ;
			}
		}

		@Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
            contentView = inflater.inflate(R.layout.fragment_qiangcontent, container, false);
			mPullRefreshListView = (PullToRefreshListView) contentView.findViewById(R.id.pull_refresh_list);
			networkTips = (TextView) contentView.findViewById(R.id.networkTips);
			progressbar = (ProgressBar) contentView.findViewById(R.id.progressBar);

			mPullRefreshListView.setMode(Mode.BOTH);
			mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>()
			{

				@Override
				public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
				{
					String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
							DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
					mPullRefreshListView.setMode(Mode.BOTH);
					mRefreshType = RefreshType.REFRESH;
					pageNum = 0;
					lastItemTime = getCurrentTime();
					fetchData();
				}

				@Override
				public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
				{
					mRefreshType = RefreshType.LOAD_MORE;
					fetchData();
				}
			});
			mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener()
			{

				@Override
				public void onLastItemVisible()
				{
				}
			});

			actualListView = mPullRefreshListView.getRefreshableView();
			mListItems = new ArrayList<Ushare>();
			mAdapter = new AIContentAdapter(mContext, mListItems);
			actualListView.setAdapter(mAdapter);
			if (mListItems.size() == 0)
			{
				fetchData();
			}
			mPullRefreshListView.setState(State.RELEASE_TO_REFRESH, true);
			actualListView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					BaseApplication.getInstance().setCurrentUshare(mListItems.get(position-1));
					Intent intent = new Intent();
					intent.setClass(getActivity(), CommentActivity.class);
					intent.putExtra("intent","layout");
					intent.putExtra("data", mListItems.get(position - 1));
					startActivity(intent);
				}
			});
			return contentView;
		}

		public void fetchData()
		{
			setState(LOADING);
			BmobQuery<Ushare> query = new BmobQuery<Ushare>();
			query.order("-createdAt");
			// query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			query.setLimit(MConstant.NUMBERS_PER_PAGE);
			BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
			query.addWhereLessThan("createdAt", date);
			LogUtils.i("SIZE:" + MConstant.NUMBERS_PER_PAGE * pageNum);
			query.setSkip(MConstant.NUMBERS_PER_PAGE * (pageNum++));
			LogUtils.i( "SIZE:" + MConstant.NUMBERS_PER_PAGE * pageNum);
			query.include("author");
			query.findObjects( new FindListener<Ushare>()
			{

				@Override
				public void done(List<Ushare> list, BmobException e) {
					if(e==null)
					{
						LogUtils.i( "circle success." + list.size());
						if (list.size() != 0 && list.get(list.size() - 1) != null)
						{
							if (mRefreshType == RefreshType.REFRESH)
							{
								mListItems.clear();
							}
							if (list.size() < MConstant.NUMBERS_PER_PAGE)
							{
								LogUtils.i( "已加载完所有数据~");
							}
							if (Login==0)
							{
								list = DatabaseUtil.getInstance(mContext).setFav(list);
							}
							mListItems.addAll(list);
							mAdapter.notifyDataSetChanged();

							setState(LOADING_COMPLETED);
							mPullRefreshListView.onRefreshComplete();
						} else
						{
							ActivityUtil.show(getActivity(), "暂无更多数据~");
							pageNum--;
							setState(LOADING_COMPLETED);
							mPullRefreshListView.onRefreshComplete();
						}
					}else {
						LogUtils.i( "circle failed." + e.getMessage());
						pageNum--;
						setState(LOADING_FAILED);
						mPullRefreshListView.onRefreshComplete();
					}
				}
			});
		}
		private static final int LOADING = 1;
		private static final int LOADING_COMPLETED = 2;
		private static final int LOADING_FAILED = 3;
		private static final int NORMAL = 4;

		public void setState(int state)
		{
			switch (state)
			{
			case LOADING:
				if (mListItems.size() == 0)
				{
					mPullRefreshListView.setVisibility(View.GONE);
					progressbar.setVisibility(View.VISIBLE);
				}
				networkTips.setVisibility(View.GONE);

				break;
			case LOADING_COMPLETED:
				networkTips.setVisibility(View.GONE);
				progressbar.setVisibility(View.GONE);
				mPullRefreshListView.setVisibility(View.VISIBLE);
				mPullRefreshListView.setMode(Mode.BOTH);

				break;
			case LOADING_FAILED:
				if (mListItems.size() == 0)
				{
					mPullRefreshListView.setVisibility(View.VISIBLE);
					mPullRefreshListView.setMode(Mode.PULL_FROM_START);
					networkTips.setVisibility(View.VISIBLE);
				}
				progressbar.setVisibility(View.GONE);
				break;
			case NORMAL:

				break;
			default:
				break;
			}
		}
	}
