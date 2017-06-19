package com.bzu.yhd.pocketcampus.bottomnav.home.circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.model.Comment;
import com.bzu.yhd.pocketcampus.model.Defaultcontent;
import com.bzu.yhd.pocketcampus.model.Ushare;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter.CommentAdapter;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter.MyGridAdapter;
import com.bzu.yhd.pocketcampus.bottomnav.im.chat.ImageBrowserActivity;
import com.bzu.yhd.pocketcampus.main.LoginActivity;
import com.bzu.yhd.pocketcampus.widget.MConstant;
import com.bzu.yhd.pocketcampus.widget.NoScrollGridView;
import com.bzu.yhd.pocketcampus.widget.db.DatabaseUtil;
import com.bzu.yhd.pocketcampus.widget.utils.ActivityUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class CommentActivity extends BaseActivity implements OnClickListener
{

	protected static final String TAG = "CommentActivity";
	private ListView commentList;
	private TextView footer;
	private EditText commentContent;
	private Button commentCommit;
	private TextView userName;
	private TextView talktime;
	private TextView commentItemContent;
	private NoScrollGridView gridView;
	private String nowTime;
	private ImageView userLogo;
	private ImageView myFav;
	private TextView comment;
	private TextView love;
	private TextView hate;
	private Ushare ushare;
	private String commentEdit = "";
	private CommentAdapter mAdapter;
	private ArrayList<Comment> comments = new ArrayList<Comment>();
	private Date date1;
	private Date date2;
	private int pageNum;
	private String imgurl;
	private String avatarUrl = null;
	private UMShareListener mShareListener;
	private ShareAction mShareAction;

		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_comment);
				findViews();
				setupViews();
				 setListener();
				 fetchData();

			        // 设置分享的内容
			        setShareContent();
				mShareListener = new CustomShareListener(this);

			}

	protected void findViews()
	{
		// TODO Auto-generated method stub
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		initializeToolbar();
		toolbar.setTitle("发表评论");


		commentList = (ListView) findViewById(R.id.comment_list);
		footer = (TextView) findViewById(R.id.loadmore);

		commentContent = (EditText) findViewById(R.id.comment_content);
		commentCommit = (Button) findViewById(R.id.comment_commit);

		userName = (TextView) findViewById(R.id.user_name);
		talktime = (TextView) findViewById(R.id.talk_time);
		commentItemContent = (TextView) findViewById(R.id.content_text);
//		commentItemImage = (ImageView) findViewById(R.id.content_image);
		 gridView= (NoScrollGridView) findViewById(R.id.content_images);

		userLogo = (ImageView) findViewById(R.id.user_logo);
		myFav = (ImageView) findViewById(R.id.item_action_fav);
		comment = (TextView) findViewById(R.id.item_action_comment);
		love = (TextView) findViewById(R.id.item_action_love);
		hate = (TextView) findViewById(R.id.item_action_hate);
	}



	protected void setupViews( )
	{

		// TODO Auto-generated method stub
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
				| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		ushare = (Ushare) getIntent().getSerializableExtra("data");// MyApplication.getInstance().getCurrentUshare();
		pageNum = 0;
		mAdapter = new CommentAdapter(CommentActivity.this, comments);
		commentList.setAdapter(mAdapter);
		setListViewHeightBasedOnChildren(commentList);
		commentList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// TODO Auto-generated method stub
				ActivityUtil.show(CommentActivity.this, "po" + position);
			}
		});
		commentList.setCacheColorHint(0);
		commentList.setScrollingCacheEnabled(false);
		commentList.setScrollContainer(false);
		commentList.setFastScrollEnabled(true);
		commentList.setSmoothScrollbarEnabled(true);

		initMoodView(ushare);
	}

	public String twoDateDistance(Date startDate, Date endDate)
	{

		if (startDate == null || endDate == null)
		{
			return null;
		}

		long timeLong = endDate.getTime() - startDate.getTime();
		if (timeLong < 60 * 1000)
			return timeLong / 1000 + "秒前";
		else if (timeLong < 60 * 60 * 1000)
		{
			timeLong = timeLong / 1000 / 60;
			return timeLong + "分钟前";
		} else if (timeLong < 60 * 60 * 24 * 1000)
		{
			timeLong = timeLong / 60 / 60 / 1000;
			return timeLong + "小时前";
		} else if (timeLong < 60 * 60 * 24 * 1000 * 7)
		{
			timeLong = timeLong / 1000 / 60 / 60 / 24;
			return timeLong + "天前";
		} else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			return sdf.format(startDate);
		}
	}

	private String getCurrentTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String times = formatter.format(new Date(System.currentTimeMillis()));
		return times;
	}
	private void initMoodView(Ushare mood2)
	{
		// TODO Auto-generated method stub
		if (mood2 == null)
		{
			return;
		}


		userName.setText(ushare.getAuthor().getNickName());
		nowTime = getCurrentTime();
		String startTime = ushare.getCreatedAt();

		SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			date1 = sDate.parse(startTime);
			date2 = sDate.parse(nowTime);


		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String time =twoDateDistance(date1, date2);

		talktime.setText(time);

		commentItemContent.setText(ushare.getContent());
		if (ushare.getContentfigureurl() == null)
			{
				gridView.setVisibility(View.GONE);

			}
		else
		{
			gridView.setVisibility(View.VISIBLE);
			final String[] ss={ ushare.getContentfigureurl().getFileUrl()};

			gridView.setAdapter(new MyGridAdapter(ss, this));

			gridView.setOnItemClickListener(new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
						{
							imageBrower(position,ss);
						}
				});
		}
		love.setText(ushare.getLove() + "");
		if (ushare.getMyLove())
		{
			love.setTextColor(Color.parseColor("#D95555"));
		} else
		{
			love.setTextColor(Color.parseColor("#000000"));
		}
		hate.setText(ushare.getHate() + "");
		if (ushare.getMyFav())
		{
			myFav.setImageResource(R.mipmap.ic_action_fav_choose);
		} else
		{
			myFav.setImageResource(R.mipmap.ic_action_fav_normal);
		}

		User user = ushare.getAuthor();
		BmobFile file = user.getFile();

		if (null != file)
		{
			imgurl=file.getFileUrl();
			ImageLoader.getInstance().displayImage(file.getFileUrl(), userLogo,
					BaseApplication.getInstance().getOptions(R.mipmap.avatar),
					new SimpleImageLoadingListener()
					{

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
						{
							// TODO Auto-generated method stub
							super.onLoadingComplete(imageUri, view, loadedImage);
							LogUtils.i(TAG, "load personal icon completed.");
						}

					});
		}
	}

	protected void setListener()
	{
		// TODO Auto-generated method stub
		footer.setOnClickListener(this);
		commentCommit.setOnClickListener(this);
		userLogo.setOnClickListener(this);
		myFav.setOnClickListener(this);
		love.setOnClickListener(this);
		hate.setOnClickListener(this);
		comment.setOnClickListener(this);
	}


	private void imageBrower(int position, String[] urls) {
			Intent intent = new Intent(getBaseContext(), ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
			startActivity(intent);

			Toast.makeText(getBaseContext(), "666", Toast.LENGTH_SHORT).show();
		}

	protected void fetchData()
	{
		// TODO Auto-generated method stub
		fetchComment();
	}

	private void fetchComment()
	{
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("relation", new BmobPointer(ushare));
		query.include("user");
		query.order("createdAt");
		query.setLimit(MConstant.NUMBERS_PER_PAGE);
		query.setSkip(MConstant.NUMBERS_PER_PAGE * (pageNum++));
		query.findObjects(new FindListener<Comment>()
		{

			@Override
			public void done(List<Comment> list, BmobException e) {
				if(e==null)
				{
					LogUtils.i(TAG, "get comment success!" + list.size());
					if (list.size() != 0 && list.get(list.size() - 1) != null)
					{

						if (list.size() < MConstant.NUMBERS_PER_PAGE)
						{
							Toast.makeText(getBaseContext(), "已加载完所有评论~",Toast.LENGTH_SHORT).show();
							footer.setText("暂无更多评论~");
						}

						mAdapter.getDataList().addAll(list);
						mAdapter.notifyDataSetChanged();
						setListViewHeightBasedOnChildren(commentList);
						LogUtils.i(TAG, "refresh");
					} else
					{
						Toast.makeText(getBaseContext(), "暂无更多评论~",Toast.LENGTH_SHORT).show();
						footer.setText("暂无更多评论~");
						pageNum--;
					}
				}else{
					ActivityUtil.show(CommentActivity.this, "获取评论失败。请检查网络~");
					pageNum--;
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
		case R.id.user_logo:
//			onClickUserLogo();
			break;
		case R.id.content_images:

			Intent intent =new Intent(this,ImageBrowserActivity.class);
			ArrayList<String> photos = new ArrayList<String>();
			photos.add(avatarUrl);
			intent.putStringArrayListExtra("photos", photos);
			intent.putExtra("position", 0);
			startActivity(intent);

			break;

		case R.id.loadmore:
			onClickLoadMore();
			break;
		case R.id.comment_commit:
			onClickCommit();
			break;
		case R.id.item_action_fav:
			onClickFav(v);
			break;
		case R.id.item_action_love:
			onClickLove();
			break;
		case R.id.item_action_hate:
			onClickHate();
			break;
		/*case R.id.item_action_share:
			onClickShare();
			break;*/
		case R.id.item_action_comment:
			onClickComment();
			break;
		default:
			break;
		}
	}

	private void onClickLoadMore()
	{
		// TODO Auto-generated method stub
		fetchData();
	}

	private void onClickCommit()
	{
		// TODO Auto-generated method stub
		User currentUser = BmobUser.getCurrentUser(User.class);
		if (currentUser != null)
		{// 已登录
			commentEdit = commentContent.getText().toString().trim();
			if (TextUtils.isEmpty(commentEdit))
			{
				ActivityUtil.show(this, "评论内容不能为空。");
				return;
			}
			// comment now
			publishComment(currentUser, commentEdit);
		} else
		{// 未登录
			ActivityUtil.show(this, "发表评论前请先登录。");
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			startActivityForResult(intent, MConstant.PUBLISH_COMMENT);
		}

	}

	private void publishComment(User user, String content)
	{

		final Comment comment = new Comment();
		comment.setUser(user);
		comment.setCommentContent(content);
		comment.save(new SaveListener()
		{

			@Override
			public void done(Object o, BmobException e)
			{
				if(e==null)
				{
					ActivityUtil.show(CommentActivity.this, "评论成功。");
					if (mAdapter.getDataList().size() < MConstant.NUMBERS_PER_PAGE)
					{
						mAdapter.getDataList().add(comment);
						mAdapter.notifyDataSetChanged();
						setListViewHeightBasedOnChildren(commentList);
					}
					commentContent.setText("");
					hideSoftInput();

					// 将该评论与强语绑定到一起
					BmobRelation relation = new BmobRelation();
					relation.add(comment);
					ushare.setRelation(relation);
					ushare.update(new UpdateListener()
					{

						@Override
						public void done(BmobException e) {
							if(e==null)
							{
								LogUtils.i(TAG, "更新评论成功。");
							}
							else {
								LogUtils.i(TAG, "更新评论失败。" + e.getMessage());
							}
						}

					});
				}else {
					ActivityUtil.show(CommentActivity.this, "评论失败。请检查网络~");
				}
			}
		});
	}

	private void onClickFav(View v)
	{
		// TODO Auto-generated method stub

		User user = BmobUser.getCurrentUser(User.class);
		if (user != null && user.getSessionToken() != null)
		{
			BmobRelation favRelaton = new BmobRelation();
			ushare.setMyFav(!ushare.getMyFav());
			if (ushare.getMyFav())
			{
				((ImageView) v).setImageResource(R.mipmap.ic_action_fav_choose);
				favRelaton.add(ushare);
				Toast.makeText(getBaseContext(), "收藏成功。",Toast.LENGTH_SHORT).show();
			} else
			{
				((ImageView) v).setImageResource(R.mipmap.ic_action_fav_normal);
				favRelaton.remove(ushare);
				Toast.makeText(getBaseContext(), "取消收藏。",Toast.LENGTH_SHORT).show();
			}

			user.setFavorite(favRelaton);
			user.update( new UpdateListener()
			{

				@Override
				public void done(BmobException e) {
					if(e==null)
					{
						LogUtils.i(TAG, "收藏成功。");
						ActivityUtil.show(CommentActivity.this, "收藏成功。");
					}
					else {
						LogUtils.i(TAG, "收藏失败。请检查网络~");
						ActivityUtil.show(CommentActivity.this, "收藏失败。请检查网络~" + e.getMessage());
					}
				}

			});
		} else
		{
			// 前往登录注册界面
			ActivityUtil.show(this, "收藏前请先登录。");
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			startActivityForResult(intent, MConstant.SAVE_FAVOURITE);
		}

	}

	private void getMyFavourite()
	{
		User user = BmobUser.getCurrentUser(User.class);
		if (user != null)
		{
			BmobQuery<Ushare> query = new BmobQuery<Ushare>();
			query.addWhereRelatedTo("favorite", new BmobPointer(user));
			query.include("user");
			query.order("createdAt");
			query.setLimit(MConstant.NUMBERS_PER_PAGE);
			query.findObjects( new FindListener<Ushare>()
			{

				@Override
				public void done(List<Ushare> list, BmobException e) {
					if(e==null){
						LogUtils.i(TAG, "get fav success!" + list.size());
						ActivityUtil.show(CommentActivity.this, "fav size:" + list.size());
					}else {
						ActivityUtil.show(CommentActivity.this, "获取收藏失败。请检查网络~");
					}
				}
			});
		} else
		{
			// 前往登录注册界面
			ActivityUtil.show(this, "获取收藏前请先登录。");
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			startActivityForResult(intent, MConstant.GET_FAVOURITE);
		}
	}

	boolean isFav = false;

	private void onClickLove()
	{
		// TODO Auto-generated method stub
		User user = BmobUser.getCurrentUser(User.class);
		if (user == null)
		{
			// 前往登录注册界面
			ActivityUtil.show(this, "请先登录。");
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
			return;
		}
		if (ushare.getMyLove())
		{
			ActivityUtil.show(CommentActivity.this, "您已经赞过啦");
			return;
		}
		isFav = ushare.getMyFav();
		if (isFav)
		{
			ushare.setMyFav(false);
		}
		ushare.setLove(ushare.getLove() + 1);
		love.setTextColor(Color.parseColor("#D95555"));
		love.setText(ushare.getLove() + "");
		ushare.increment("love", 1);
		ushare.update(new UpdateListener()
		{

			@Override
			public void done(BmobException e) {
				if(e==null)
				{
					ushare.setMyLove(true);
					ushare.setMyFav(isFav);
					DatabaseUtil.getInstance(getBaseContext()).insertFav(ushare);
					Toast.makeText(getBaseContext(), "点赞成功~",Toast.LENGTH_SHORT).show();
				}else {

				}
			}

		});
	}

	private void onClickHate()
	{
		// TODO Auto-generated method stub
		ushare.setHate(ushare.getHate() + 1);
		hate.setText(ushare.getHate() + "");
		ushare.increment("hate", 1);
		ushare.update(new UpdateListener()
		{

			@Override
			public void done(BmobException e) {
				if(e==null)
				{
					Toast.makeText(getBaseContext(), "点赞成功~",Toast.LENGTH_SHORT).show();
				}else {

				}
			}

		});
	}


	private void onClickShare()
	{
		// TODO Auto-generated method stub
		ActivityUtil.show(CommentActivity.this, "分享到 ...");                             //分享
//			postShare();
		ShareBoardConfig config = new ShareBoardConfig();
		config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);
		config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR); // 圆角背景

//                config.setTitleVisibility(false); // 隐藏title
//                config.setCancelButtonVisibility(false); // 隐藏取消按钮
		mShareAction.open(config);
	}

	private void onClickComment()
	{
		// TODO Auto-generated method stub
		commentContent.requestFocus();

		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(commentContent, 0);
	}

	private void hideSoftInput()
	{
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case MConstant.PUBLISH_COMMENT:
				// 登录完成
				commentCommit.performClick();
				break;
			case MConstant.SAVE_FAVOURITE:
				myFav.performClick();
				break;
			case MConstant.GET_FAVOURITE:

				break;
			case MConstant.GO_SETTINGS:
				userLogo.performClick();
				break;
			default:
				break;
			}
		}

	}

	private static class CustomShareListener implements UMShareListener {

		private WeakReference<CommentActivity> mActivity;

		private CustomShareListener(CommentActivity activity) {
			mActivity = new WeakReference(activity);
		}

		@Override
		public void onStart(SHARE_MEDIA platform) {

		}

		@Override
		public void onResult(SHARE_MEDIA platform) {

			if (platform.name().equals("WEIXIN_FAVORITE")) {
				Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
			} else {
				if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
						&& platform != SHARE_MEDIA.EMAIL
						&& platform != SHARE_MEDIA.FLICKR
						&& platform != SHARE_MEDIA.FOURSQUARE
						&& platform != SHARE_MEDIA.TUMBLR
						&& platform != SHARE_MEDIA.POCKET
						&& platform != SHARE_MEDIA.PINTEREST

						&& platform != SHARE_MEDIA.INSTAGRAM
						&& platform != SHARE_MEDIA.GOOGLEPLUS
						&& platform != SHARE_MEDIA.YNOTE
						&& platform != SHARE_MEDIA.EVERNOTE) {
					Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
				}

			}
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
					&& platform != SHARE_MEDIA.EMAIL
					&& platform != SHARE_MEDIA.FLICKR
					&& platform != SHARE_MEDIA.FOURSQUARE
					&& platform != SHARE_MEDIA.TUMBLR
					&& platform != SHARE_MEDIA.POCKET
					&& platform != SHARE_MEDIA.PINTEREST

					&& platform != SHARE_MEDIA.INSTAGRAM
					&& platform != SHARE_MEDIA.GOOGLEPLUS
					&& platform != SHARE_MEDIA.YNOTE
					&& platform != SHARE_MEDIA.EVERNOTE) {
				Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
				if (t != null) {
					Log.d("throw", "throw:" + t.getMessage());
				}
			}

		}
		@Override
		public void onCancel(SHARE_MEDIA platform) {

			Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
		}
	}


    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent() {

		mShareAction = new ShareAction(CommentActivity.this).setDisplayList(
				SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
				SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
				SHARE_MEDIA.ALIPAY, SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN,
				SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL,SHARE_MEDIA.MORE)
				.addButton("umeng_sharebutton_copy", "umeng_sharebutton_copy", "umeng_socialize_copy", "umeng_socialize_copy")
				.addButton("umeng_sharebutton_copyurl", "umeng_sharebutton_copyurl", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
				.setShareboardclickCallback(new ShareBoardlistener() {
					@Override
					public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
						if (snsPlatform.mShowWord.equals("umeng_sharebutton_copy")) {
							Toast.makeText(CommentActivity.this, "复制文本按钮", Toast.LENGTH_LONG).show();
						} else if (snsPlatform.mShowWord.equals("umeng_sharebutton_copyurl")) {
							Toast.makeText(CommentActivity.this, "复制链接按钮", Toast.LENGTH_LONG).show();

						} else {
							UMWeb web = new UMWeb(Defaultcontent.url);
							web.setTitle("来自分享面板标题");
							web.setDescription("来自分享面板内容");
							web.setThumb(new UMImage(CommentActivity.this, R.mipmap.ic_launcher));
							new ShareAction(CommentActivity.this).withMedia(web)
									.setPlatform(share_media)
									.setCallback(mShareListener)
									.share();
						}
					}
				});
    }

	
	/***
	 * 动态设置listview的高度 item 总布局必须是linearLayout
	 * 
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView)
	{
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
		{
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 15;
		listView.setLayoutParams(params);
	}
}
