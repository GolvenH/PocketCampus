package com.bzu.yhd.pocketcampus.bottomnav.home.circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.model.Ushare;
import com.bzu.yhd.pocketcampus.bottomnav.im.ui.UserInfoActivity;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter.BaseContentAdapter;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter.MyGridAdapter;
import com.bzu.yhd.pocketcampus.main.LoginActivity;
import com.bzu.yhd.pocketcampus.widget.MConstant;
import com.bzu.yhd.pocketcampus.widget.NoScrollGridView;
import com.bzu.yhd.pocketcampus.widget.utils.ActivityUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author kingofglory email: kingofglory@yeah.net blog: http:www.google.com
 * @date 2014-2-24 TODO
 */

public class PersonCenterContentAdapter extends BaseContentAdapter<Ushare> {
		
		
		private String nowTime;
		private Date date1;
		private Date date2;
	public static final String TAG = "AIContentAdapter";
	public static final int SAVE_FAVOURITE = 2;

	public PersonCenterContentAdapter(Context context, ArrayList<Ushare> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}


	private String getCurrentTime()
		{
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String times = formatter
					.format(new Date(System.currentTimeMillis()));
			return times;
		}

	public String twoDateDistance(Date startDate, Date endDate)
		{

			if (startDate == null || endDate == null) {
				return null;
			}

			long timeLong = endDate.getTime() - startDate.getTime();
			if (timeLong < 60 * 1000)
				return timeLong / 1000 + "秒前";
			else if (timeLong < 60 * 60 * 1000) {
				timeLong = timeLong / 1000 / 60;
				return timeLong + "分钟前";
			} else if (timeLong < 60 * 60 * 24 * 1000) {
				timeLong = timeLong / 60 / 60 / 1000;
				return timeLong + "小时前";
			} else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
				timeLong = timeLong / 1000 / 60 / 60 / 24;
				return timeLong + "天前";
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss ");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
				return sdf.format(startDate);
			}
		}

	
	@Override
	public View getConvertView(int position, View convertView,
			ViewGroup parent)
		{
			// TODO Auto-generated method stub
			final ViewHolder viewHolder;
			if (convertView == null) 
			{
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.ai_item, null);
				viewHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
				viewHolder.talkTime = (TextView) convertView.findViewById(R.id.talk_time);
				viewHolder.userLogo = (ImageView) convertView.findViewById(R.id.user_logo);
				viewHolder.favMark = (ImageView) convertView.findViewById(R.id.item_action_fav);
				viewHolder.contentText = (TextView) convertView.findViewById(R.id.content_text);
//				viewHolder.contentImage = (ImageView) convertView.findViewById(R.id.content_image);

				viewHolder.gridView = (NoScrollGridView) convertView.findViewById(R.id.content_images);

				
				viewHolder.love = (TextView) convertView.findViewById(R.id.item_action_love);
				viewHolder.hate = (TextView) convertView.findViewById(R.id.item_action_hate);
				viewHolder.comment = (TextView) convertView.findViewById(R.id.item_action_comment);
				convertView.setTag(viewHolder);
			} 
			else 
				{
					viewHolder = (ViewHolder) convertView.getTag();
				}
			
			final Ushare entity = dataList.get(position);
			LogUtils.i("user", entity.toString());
			User user = entity.getAuthor();
			if (user == null)
				{
					LogUtils.i("user", "USER IS NULL");
				}
			if (user.getFile() == null) 
				{
					LogUtils.i("user", "USER avatar IS NULL");
				}
			String avatarUrl = null;
			if (user.getFile()!= null) 
				{
					avatarUrl = user.getFile().getFileUrl();
				}
			else
				{
				avatarUrl="http://file.bmob.cn/M01/EC/94/oYYBAFXL_t6AR5hHAAA41P2ipPo244.jpg";
				}
	
			ImageLoader.getInstance().displayImage(avatarUrl,viewHolder.userLogo, BaseApplication.getInstance().getOptions(R.mipmap.user_icon_default_main)
					,new SimpleImageLoadingListener()
						{
							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage)
								{
									super.onLoadingComplete(imageUri, view,	loadedImage);
								}
						});
			viewHolder.userLogo.setOnClickListener(new OnClickListener()
				{
					String a1=BaseApplication.getInstance().getCurrentUser().getUsername();
					String a2=entity.getAuthor().getUsername();
					@Override
					public void onClick(View v)
						{
							if (BaseApplication.getInstance().getCurrentUser() == null)
								{
									ActivityUtil.show(mContext, "请先登录。");
									Intent intent = new Intent();
									intent.setClass(mContext,LoginActivity.class);
									BaseApplication.getInstance().getTopActivity().startActivity(intent);
									return;
								}
							
							else 
							// 判断点击头像得到的用户 与当前登陆用户 是否相同
							if(a1.equals(a2))
							{
								Toast.makeText(BaseApplication.getInstance().getTopActivity(), "11", Toast.LENGTH_SHORT).show();
								Intent intent = new Intent();
								intent.putExtra("extra", "me");
								intent.setClass(mContext,UserInfoActivity.class);
								BaseApplication.getInstance().getTopActivity().startActivity(intent);
							}
						 else
						 {
							BaseApplication.getInstance().setCurrentUshare(entity);
							Intent intent = new Intent();
							intent.setClass(BaseApplication.getInstance().getTopActivity(),PersonActivity.class);
							mContext.startActivity(intent);
						 }
						}
				});
			
			viewHolder.userName.setText(entity.getAuthor().getNickName());
			
			
			
			nowTime = getCurrentTime();
			String startTime = entity.getCreatedAt();
			SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try
				{
					date1 = sDate.parse(startTime);
					date2 = sDate.parse(nowTime);

				} 
			catch (ParseException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			String time = twoDateDistance(date1, date2);

			viewHolder.talkTime.setText(time);
			
			
		viewHolder.contentText.setText(entity.getContent());
		if (entity.getContentfigureurl() == null)
		{
//			viewHolder.contentImage.setVisibility(View.GONE);
			viewHolder.gridView.setVisibility(View.GONE);

		}
	else 
	{
//		viewHolder.contentImage.setVisibility(View.VISIBLE);
		viewHolder.gridView.setVisibility(View.VISIBLE);
		
		final String[] ss={ entity.getContentfigureurl().getFileUrl()};
		
		viewHolder.gridView.setAdapter(new MyGridAdapter(ss, mContext));
		
		viewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
			{

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
					{
						imageBrower(position,ss);
					}
			});
		
		}
		viewHolder.love.setText(entity.getLove() + "");
		LogUtils.i("love", entity.getMyLove() + "..");
		if (entity.getMyLove()) {
			viewHolder.love.setTextColor(Color.parseColor("#D95555"));
		} else {
			viewHolder.love.setTextColor(Color.parseColor("#000000"));
		}
		viewHolder.hate.setText(entity.getHate() + "");
		viewHolder.love.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (entity.getMyLove()) {
					return;
				}
				entity.setLove(entity.getLove() + 1);
				viewHolder.love.setTextColor(Color.parseColor("#D95555"));
				viewHolder.love.setText(entity.getLove() + "");
				entity.setMyLove(true);
				entity.increment("love", 1);
				entity.update(new UpdateListener() {

					@Override
					public void done(BmobException e) {
						if(e==null)
						{
							LogUtils.i(TAG, "点赞成功~");
						}else {

						}
					}

				});
			}
		});
		viewHolder.hate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				entity.setHate(entity.getHate() + 1);
				viewHolder.hate.setText(entity.getHate() + "");
				entity.increment("hate", 1);
				entity.update(new UpdateListener() {

					@Override
					public void done(BmobException e) {
						if(e==null)
							{
								ActivityUtil.show(mContext, "点踩成功~");
							}else {

						}
					}
				});
			}
		});
		viewHolder.comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 评论
				BaseApplication.getInstance().setCurrentUshare(entity);
				Intent intent = new Intent();
				intent.setClass(BaseApplication.getInstance().getTopActivity(),
						CommentActivity.class);
				mContext.startActivity(intent);
			}
		});

		if (entity.getMyFav()) {
			viewHolder.favMark
					.setImageResource(R.mipmap.ic_action_fav_choose);
		} else {
			viewHolder.favMark
					.setImageResource(R.mipmap.ic_action_fav_normal);
		}
		viewHolder.favMark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 收藏
				ActivityUtil.show(mContext, "收藏");
				onClickFav(v, entity);

			}
		});
		return convertView;
	}
	
	private void imageBrower(int position, String[] urls) {
			Intent intent = new Intent(mContext, ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
			mContext.startActivity(intent);
			
			Toast.makeText(mContext, "666", Toast.LENGTH_SHORT).show();
		}

	public static class ViewHolder {
		public ImageView userLogo;
		public TextView userName;
		public TextView contentText;
//		public ImageView contentImage;
		public TextView talkTime;

		public ImageView favMark;
		public TextView love;
		public TextView hate;
		public TextView comment;
		
		public NoScrollGridView gridView;

	}

	private void onClickFav(View v, Ushare ushare) {
		// TODO Auto-generated method stub
		User user = BmobUser.getCurrentUser( User.class);
		if (user != null && user.getSessionToken() != null) {
			BmobRelation favRelaton = new BmobRelation();

			ushare.setMyFav(!ushare.getMyFav());
			if (ushare.getMyFav()) {
				((ImageView) v)
						.setImageResource(R.mipmap.ic_action_fav_choose);
				favRelaton.add(ushare);
				ActivityUtil.show(mContext, "收藏成功。");
			} else {
				((ImageView) v)
						.setImageResource(R.mipmap.ic_action_fav_normal);
				favRelaton.remove(ushare);
				ActivityUtil.show(mContext, "取消收藏。");
			}

			user.setFavorite(favRelaton);
			user.update(new UpdateListener() {

				@Override
				public void done(BmobException e) {
					if(e==null)
					{
						LogUtils.i(TAG, "收藏成功。");
					}else {
						LogUtils.i(TAG, "收藏失败。请检查网络~");
						ActivityUtil.show(mContext, "收藏失败。请检查网络~" + e.getMessage());
					}
				}

			});
		} else {
			// 前往登录注册界面
			ActivityUtil.show(mContext, "收藏前请先登录。");
			Intent intent = new Intent();
			intent.setClass(mContext, LoginActivity.class);
			BaseApplication.getInstance().getTopActivity()
					.startActivityForResult(intent, SAVE_FAVOURITE);
		}
	}

	private void getMyFavourite() {
		User user = BmobUser.getCurrentUser( User.class);
		if (user != null) {
			BmobQuery<Ushare> query = new BmobQuery<Ushare>();
			query.addWhereRelatedTo("favorite", new BmobPointer(user));
			query.include("user");
			query.order("createdAt");
			query.setLimit(MConstant.NUMBERS_PER_PAGE);
			query.findObjects(new FindListener<Ushare>() {

				@Override
				public void done(List<Ushare> list, BmobException e) {
					if(e==null)
					{
						LogUtils.i(TAG, "get fav success!" + list.size());
						ActivityUtil.show(mContext, "fav size:" + list.size());
					}else {
						LogUtils.i(TAG, "收藏失败。请检查网络~");
						ActivityUtil.show(mContext, "收藏失败。请检查网络~" + e.getMessage());
					}
				}
			});
		} else {
			// 前往登录注册界面
			ActivityUtil.show(mContext, "获取收藏前请先登录。");
			Intent intent = new Intent();
			intent.setClass(mContext, LoginActivity.class);
			BaseApplication.getInstance().getTopActivity()
					.startActivityForResult(intent, MConstant.GET_FAVOURITE);
		}
	}
}