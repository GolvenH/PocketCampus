package com.bzu.yhd.pocketcampus.bottomnav.home.circle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.bottomnav.im.bean.AddFriendMessage;
import com.bzu.yhd.pocketcampus.bottomnav.im.ui.ChatActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;


public class PersonActivity extends FragmentActivity
		implements View.OnClickListener, OnCheckedChangeListener
	{
		private User mUser;
		public static final String TAG = "PersonActivity";
		Button button,button1;
		private ImageView personalIcon;
		private TextView personalName;
		private TextView personalTitle;
		BmobIMUserInfo info;
		private RadioGroup rg;
		private RadioButton rb1, rb2;

		@Override
		public void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_person);
				setUpMenu();
				mUser = BaseApplication.getInstance().getCurrentUshare().getAuthor();
				updatePersonalInfo(mUser);
				info = new BmobIMUserInfo(mUser.getObjectId(),mUser.getUsername(),mUser.getAvatar());

			}

		private void setUpMenu()
			{
				button=(Button)findViewById(R.id.btn_chat);
				button1=(Button)findViewById(R.id.btn_add_friend);
				personalIcon = (ImageView) findViewById(R.id.personal_icon1);
				personalName = (TextView)findViewById(R.id.personl_name1);
				rg = (RadioGroup) findViewById(R.id.rg);
				rb1 = (RadioButton) findViewById(R.id.rb1);
				rb2 = (RadioButton) findViewById(R.id.rb2);
				rg.setOnCheckedChangeListener(this);
				button.setOnClickListener(this);
				button1.setOnClickListener(this);
				rb1.setChecked(true);
			}

		private void changeFragment(Fragment targetFragment)
			{
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.main_fragment, targetFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
			}
		
		
		
		private void updatePersonalInfo(User user)
			{

				if(user.getNickName()!=null)
				{
					personalName.setText(user.getNickName());
				}else {
					personalName.setText(user.getUsername());
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
				
					ImageLoader.getInstance().displayImage(avatarUrl,personalIcon,BaseApplication.getInstance().getOptions(R.mipmap.user_icon_default_main)

							,new SimpleImageLoadingListener()
								{

									@Override
									public void onLoadingComplete(
											String imageUri, View view,
											Bitmap loadedImage)
										{
											// TODO Auto-generated method stub
											super.onLoadingComplete(imageUri,
													view, loadedImage);
											LogUtils.i(TAG,
													"load personal icon completed.");
										}

								});
				
			}

		@Override
		public void onClick(View v)
			{
				switch (v.getId())
				{
					case R.id.btn_add_friend:
						sendAddFriendMessage();
						break;
					case R.id.btn_chat:
						BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info,false,null);
						Bundle bundle = new Bundle();
						bundle.putSerializable("c", c);
						startActivity(ChatActivity.class, bundle, false);

					break;
				}
			}

		public void startActivity(Class<? extends Activity> target, Bundle bundle, boolean finish) {
			Intent intent = new Intent();
			intent.setClass(this, target);
			if (bundle != null)
				intent.putExtra(getPackageName(), bundle);
			startActivity(intent);
			if (finish)
				finish();
		}

		/**
		 * 发送添加好友的请求
		 */
		private void sendAddFriendMessage(){
			//启动一个会话，如果isTransient设置为true,则不会创建在本地会话表中创建记录，
			//设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
			BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true,null);
			//这个obtain方法才是真正创建一个管理消息发送的会话
			BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
			AddFriendMessage msg =new AddFriendMessage();
			User currentUser = BmobUser.getCurrentUser(User.class);
			msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
			Map<String,Object> map =new HashMap<>();
			map.put("name", currentUser.getUsername());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
			map.put("avatar",currentUser.getAvatar());//发送者的头像
			map.put("uid",currentUser.getObjectId());//发送者的uid
			msg.setExtraMap(map);
			conversation.sendMessage(msg, new MessageSendListener() {
				@Override
				public void done(BmobIMMessage msg, BmobException e) {
					if (e == null) {//发送成功
						Toast.makeText(PersonActivity.this,"好友请求发送成功，等待验证",Toast.LENGTH_SHORT).show();
					} else {//发送失败
						Toast.makeText(PersonActivity.this,"发送失败:" + e.getMessage(),Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		public void onCheckedChanged(RadioGroup arg0, int checkedId)
			{

				if (rb1.getId() == checkedId) {
					changeFragment(new PersoninfoFragment());
				} else if (rb2.getId() == checkedId) {
					changeFragment(new PersontalkFragment());
				}
			}
	}