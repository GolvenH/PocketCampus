package com.bzu.yhd.pocketcampus.bottomnav.home.circle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.base.BaseFragment;
import com.bzu.yhd.pocketcampus.model.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;

public class PersoninfoFragment extends BaseFragment
	{
		private Intent intent;
		private User mUser;
		public static final int EDIT_USER = 1;
		private TextView age,star,sex,sign,area;
		private String nowTime;
		public static String setTime = null;
		private String 	s22 ;
		private String 	s23;
		private	 String s12 ;
		private String 	s13;
		
		public static PersoninfoFragment newInstance()
			{
				PersoninfoFragment fragment = new PersoninfoFragment();
				return fragment;
			}


		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

		}

		@Override
		public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.fragment_personal_info, container, false);
			age=(TextView) view.findViewById(R.id.user_birth_text);
			star=(TextView) view.findViewById(R.id.user_star_text);
			sex=(TextView) view.findViewById(R.id.user_sex__text);
			sign=(TextView) view.findViewById(R.id.user_sign_text);
			area=(TextView) view.findViewById(R.id.user_local_text);
			initDate();
			return view;
		}
		protected void initDate()
			{
				mUser = BaseApplication.getInstance().getCurrentUshare()
						.getAuthor();

				//年龄
				if (mUser.getBirthday() == null) {
					age.setText("对方还没有设置年龄~");

				} else {
					String s=mUser.getBirthday();
					nowTime = getCurrentTime();
					String time = twoDateDistance(s, nowTime);
					age.setText(time+"岁");
				}
				//地区
				if (mUser.getArea() == null) {
					star.setText("对方还没有设置地区所在地");

				} else {
					String s=mUser.getArea();
				
					area.setText(s);
				}
				//星座
				star.setText(mUser.getStar());
				//性别
				if(mUser.getSex()!=null)
				{
				if(mUser.getSex().equals("男"))
				{
					sex.setText("男");
				}
				else
				{
					sex.setText("女");
				}
				}else {
					sex.setText("未设置");
				}

				sign.setText(mUser.getSignature());

			}

		//获取当前时间 
		private String getCurrentTime()
			{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String times = formatter
						.format(new Date(System.currentTimeMillis()));
				return times;
			}
		//	两个时间相减 求取年龄
		public String twoDateDistance(String startDate, String endDate)
			{

				if (startDate == null) 
				{
					startDate=endDate;	
				}
				if (endDate == null) {
					return "0";
				}
				
				if (endDate != null && startDate != null) 
				{
					String 	s11 = startDate.substring(0,4);
					String 	s21 = endDate.substring(0,4);
					String 	ssss = startDate.substring(6,7);
						s12 = startDate.substring(5,7);
					 	s13 = startDate.substring(8,10); 
					 	s22 = endDate.substring(5,7);
						s23 = endDate.substring(8,10);
						int a = Integer.valueOf(s21).intValue()-Integer.valueOf(s11).intValue();
						int b = Integer.valueOf(s22).intValue()-Integer.valueOf(s12).intValue();
						int c = Integer.valueOf(s23).intValue()-Integer.valueOf(s13).intValue();
						if(b<=0)
						{
								a=a-1;
						}
						if(b==0)
						{
							if(c<0)
							{
								a=a-1;
							}
						}
						String s = String.valueOf(a);
					return s;
				}
				return "0";
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

		//
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
				// mIProgressControllor.showActionBarProgress();
				User user = BmobUser.getCurrentUser(User.class);
				
				
				LogUtils.i(TAG,
						"sign:" + user.getSignature() + "sex:" + user.getSex());
				Toast.makeText(mContext, "更新信息成功。",Toast.LENGTH_SHORT).show();
				// mIProgressControllor.hideActionBarProgress();
			}

	}
