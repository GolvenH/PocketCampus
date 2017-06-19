package com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.CommentActivity;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.ImagePagerActivity;
import com.bzu.yhd.pocketcampus.bottomnav.home.circle.PersonActivity;
import com.bzu.yhd.pocketcampus.bottomnav.user.MeEditActivity;
import com.bzu.yhd.pocketcampus.main.LoginActivity;
import com.bzu.yhd.pocketcampus.widget.MConstant;
import com.bzu.yhd.pocketcampus.widget.NoScrollGridView;
import com.bzu.yhd.pocketcampus.widget.db.DatabaseUtil;
import com.bzu.yhd.pocketcampus.widget.utils.ActivityUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yuyh.library.imgsel.utils.LogUtils;

import org.polaric.colorful.Colorful;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

public class AIContentAdapter extends BaseContentAdapter<Ushare>
	{
		public static final String TAG = "AIContentAdapter";

		public static final int SAVE_FAVOURITE = 2;
		private String nowTime;
		private Date date1;
		private Date date2;


		public AIContentAdapter(Context context, ArrayList<Ushare> list)
			{

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

				ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));

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
					avatarUrl="http://bmob-cdn-11858.b0.upaiyun.com/2017/05/31/df39920e4ce947cfb4f1f32f53e15874.jpg";
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
									Intent intent = new Intent();
									intent.putExtra("extra", "me");
									intent.setClass(mContext,MeEditActivity.class);
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

				if(entity.getAuthor().getNickName()==null)
				{
					viewHolder.userName.setText(entity.getAuthor().getUsername());
				}else {
					viewHolder.userName.setText(entity.getAuthor().getNickName());
				}
				
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
						viewHolder.gridView.setVisibility(View.GONE);
					}
				else 
				{
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

				if (entity.getMyLove()) 
					{
						viewHolder.love.setTextColor(Color.parseColor("#D95555"));
					}
				else 
					{
						if (Colorful.getThemeDelegate().isNight())
						{
							viewHolder.love.setTextColor(Color.parseColor("#ffffff"));
						}else {
							viewHolder.love.setTextColor(Color.parseColor("#000000"));
						}

					}
				viewHolder.hate.setText(entity.getHate() + "");
				viewHolder.love.setOnClickListener(new OnClickListener()
					{
						boolean oldFav = entity.getMyFav();
						@Override
						public void onClick(View v)
							{
								// TODO Auto-generated method stub
								if (BaseApplication.getInstance()
										.getCurrentUser() == null)
								{
									ActivityUtil.show(mContext, "请先登录。");
									Intent intent = new Intent();
									intent.setClass(mContext,LoginActivity.class);
									BaseApplication.getInstance().getTopActivity().startActivity(intent);
									return;
								}
								if (entity.getMyLove())
									{
										ActivityUtil.show(mContext, "您已赞过啦");
										return;
									}
								if (DatabaseUtil.getInstance(mContext).isLoved(entity))
								{
									ActivityUtil.show(mContext, "您已赞过啦");
									entity.setMyLove(true);
									entity.setLove(entity.getLove() + 1);
									viewHolder.love.setTextColor(Color.parseColor("#D95555"));
									viewHolder.love	.setText(entity.getLove() + "");
									return;
								}

								entity.setLove(entity.getLove() + 1);
								viewHolder.love.setTextColor(Color.parseColor("#D95555"));
								viewHolder.love.setText(entity.getLove() + "");
								entity.increment("love", 1);
								if (entity.getMyFav())
									{
										entity.setMyFav(false);
									}
								entity.update(new UpdateListener()
									{
										@Override
										public void done(BmobException e) {
											if(e==null)
											{
												entity.setMyLove(true);
												entity.setMyFav(oldFav);
												DatabaseUtil.getInstance(mContext).insertFav(entity);
												DatabaseUtil.getInstance(mContext).queryFav();
												LogUtils.i(TAG, "点赞成功~");
											}else {
												entity.setMyLove(true);
												entity.setMyFav(oldFav);
											}
										}
									});
							}
					});

				viewHolder.comment.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
							{
								// 评论
								 BaseApplication.getInstance().setCurrentUshare(entity);
								if (BaseApplication.getInstance().getCurrentUser() == null)
								{
									ActivityUtil.show(mContext, "请先登录。");
									Intent intent = new Intent();
									intent.setClass(mContext,LoginActivity.class);
									BaseApplication.getInstance().getTopActivity().startActivity(intent);
									return;
								}
								Intent intent = new Intent();
								intent.setClass(BaseApplication.getInstance().getTopActivity(),CommentActivity.class);
								intent.putExtra("data", entity);

								mContext.startActivity(intent);
							}
					});

				if (entity.getMyFav()) 
					{
						viewHolder.favMark.setImageResource(R.mipmap.ic_action_fav_choose);
					}
				else 
					{
						viewHolder.favMark.setImageResource(R.mipmap.ic_action_fav_normal);
					}
				viewHolder.favMark.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
							{
								// 收藏
								ActivityUtil.show(mContext, "收藏");
								onClickFav(v, entity);
							}
					});
				return convertView;
			}
	    
		
		
		public Bitmap returnBitMap(String url){ 
		        URL myFileUrl = null;   
		        Bitmap bitmap = null;  
		        try {   
		            myFileUrl = new URL(url);   
		        } catch (MalformedURLException e) {   
		            e.printStackTrace();   
		        }   
		        try {   
		            HttpURLConnection conn = (HttpURLConnection) myFileUrl   
		              .openConnection();   
		            conn.setDoInput(true);   
		            conn.connect();   
		            InputStream is = conn.getInputStream();   
		            bitmap = BitmapFactory.decodeStream(is);   
		            is.close();   
		        } catch (IOException e) {   
		              e.printStackTrace();   
		        }   
		              return bitmap;   
		    }   
		
		private void imageBrower(int position, String[] urls) {
				Intent intent = new Intent(mContext, ImagePagerActivity.class);
				// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
				mContext.startActivity(intent);
				Toast.makeText(mContext, "666", Toast.LENGTH_SHORT).show();
			}

		public static class ViewHolder
			{
				public ImageView userLogo;
				public TextView userName;
				public TextView talkTime;
				public TextView contentText;
				public ImageView favMark;
				public TextView love;
				public TextView hate;
				public TextView comment;
				public NoScrollGridView gridView;
			}

		private void onClickFav(View v, final Ushare ushare)
			{
				User user = BmobUser.getCurrentUser(User.class);
				if (user != null && user.getSessionToken() != null) {
					BmobRelation favRelaton = new BmobRelation();
					ushare.setMyFav(!ushare.getMyFav());
					if (ushare.getMyFav())
						{
							((ImageView) v).setImageResource(R.mipmap.ic_action_fav_choose);
							favRelaton.add(ushare);
							user.setFavorite(favRelaton);
							ActivityUtil.show(mContext, "收藏成功。");
							user.update(new UpdateListener()
								{
									@Override
									public void done(BmobException e) {
										if(e==null){
											DatabaseUtil.getInstance(mContext).insertFav(ushare);
											LogUtils.i(TAG, "收藏成功。");
										}else {
											LogUtils.i(TAG, "收藏失败。请检查网络~");
											ActivityUtil.show(mContext,	"收藏失败。请检查网络~" + e.getMessage());
										}
									}
								});
						} 
					else 
						{
							((ImageView) v).setImageResource(R.mipmap.ic_action_fav_normal);
							favRelaton.remove(ushare);
							user.setFavorite(favRelaton);
							ActivityUtil.show(mContext, "取消收藏。");
							user.update(new UpdateListener()
							{
								@Override
								public void done(BmobException e) {
									if(e==null){
										DatabaseUtil.getInstance(mContext).deleteFav(ushare);
										LogUtils.i(TAG, "取消收藏。");
									}else {
										LogUtils.i(TAG, "取消收藏失败。请检查网络~");
										ActivityUtil.show(mContext,	"取消收藏失败。请检查网络~" + e.getMessage());
									}
								}
								});
						}
				}
				else 
					{
						// 前往登录注册界面
						ActivityUtil.show(mContext, "收藏前请先登录。");
						Intent intent = new Intent();
						intent.setClass(mContext, LoginActivity.class);
						BaseApplication.getInstance().getTopActivity().startActivityForResult(intent, SAVE_FAVOURITE);
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
						query.findObjects(new FindListener<Ushare>()
							{
								@Override
								public void done(List<Ushare> list, BmobException e)
								{
									if(e==null)
									{
										LogUtils.i(TAG,	"get fav success!" + list.size());
										ActivityUtil.show(mContext,	"fav size:" + list.size());
									}else {
										ActivityUtil.show(mContext,	"获取收藏失败。请检查网络~");

									}
								}
							});
					} 
				else
					{
						// 前往登录注册界面
						ActivityUtil.show(mContext, "获取收藏前请先登录。");
						Intent intent = new Intent();
						intent.setClass(mContext, LoginActivity.class);
						BaseApplication.getInstance().getTopActivity().startActivityForResult(intent,	MConstant.GET_FAVOURITE);
					}
			}
	}