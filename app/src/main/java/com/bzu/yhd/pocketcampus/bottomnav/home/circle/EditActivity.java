package com.bzu.yhd.pocketcampus.bottomnav.home.circle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BasePageActivity;
import com.bzu.yhd.pocketcampus.model.Ushare;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.widget.utils.ActivityUtil;
import com.bzu.yhd.pocketcampus.widget.utils.CacheUtils;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.yuyh.library.imgsel.SImageLoader;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class EditActivity extends BasePageActivity implements OnClickListener
{
	private static final int REQUEST_CODE = 0;
	private static final int REQUEST_CODE_ALBUM = 1;
	private static final int REQUEST_CODE_CAMERA = 2;
	EditText content;

	LinearLayout openLayout;
	LinearLayout takeLayout;

	ImageView albumPic;
	ImageView takePic;
	String dateTime;

	@Override
	protected void setLayoutView()
	{
			setContentView(R.layout.activity_edit);
	}

	@Override
	protected void findViews()
	{
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		initializeToolbar();
		toolbar.setTitle("发表作品");
		content = (EditText) findViewById(R.id.edit_content);
		openLayout = (LinearLayout) findViewById(R.id.open_layout);
		takeLayout = (LinearLayout) findViewById(R.id.take_layout);
		albumPic = (ImageView) findViewById(R.id.open_pic);
		takePic = (ImageView) findViewById(R.id.take_pic);
	}

	@Override
	protected void setupViews(Bundle bundle)
	{
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_publish, menu);
		final MenuItem item = menu.findItem(R.id.action_publish);

		item.getActionView().setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				onOptionsItemSelected(item);
			}
		});

		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_publish)
		{
			String commitContent = content.getText().toString().trim();
			if (TextUtils.isEmpty(commitContent))
			{
				Toast.makeText(EditActivity.this, "内容不能为空",Toast.LENGTH_SHORT).show();
			}else if (targeturl == null)
			{
				publishWithoutFigure(commitContent, null);
			} else
			{
				publish(commitContent);
			}
			return true;
		}
		if (item.getItemId() == android.R.id.home)
		{
			this.finish();return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void setListener()
	{
		openLayout.setOnClickListener(this);
		takeLayout.setOnClickListener(this);
		albumPic.setOnClickListener(this);
		takePic.setOnClickListener(this);
	}

	@Override
	protected void fetchData()
	{

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.open_layout:
			/*Date date1 = new Date(System.currentTimeMillis());
			dateTime = date1.getTime() + "";
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image*//*");
			startActivityForResult(intent, REQUEST_CODE_ALBUM);*/
			showAlbum();

			break;
		case R.id.take_layout:
			Date date = new Date(System.currentTimeMillis());
			dateTime = date.getTime() + "";
			File f = new File(CacheUtils.getCacheDirectory(EditActivity.this, true, "pic") + dateTime);
			if (f.exists())
			{
				f.delete();
			}
			try
			{
				f.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			Uri uri = Uri.fromFile(f);
			Log.e("uri", uri + "");

			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(camera, REQUEST_CODE_CAMERA);
			break;
			case R.id.open_pic:
				showAlbum();

			default:
			break;
		}
	}

	/*
	 * 发表带图片
	 */
	private void publish(final String commitContent)
	{
		final BmobFile figureFile = new BmobFile(new File(targeturl));
		figureFile.upload(new UploadFileListener()
		{
			@Override
			public void done(BmobException e) {
				if(e==null) {
					LogUtils.i( "上传文件成功。" + figureFile.getFileUrl());
					publishWithoutFigure(commitContent, figureFile);
				}
				else {
					LogUtils.i( "上传文件失败。" + e.getMessage());
				}
			}
		});
	}

	private void publishWithoutFigure(final String commitContent, final BmobFile figureFile)
	{
		User user = BmobUser.getCurrentUser(User.class);

		final Ushare ushare = new Ushare();
		ushare.setAuthor(user);
		ushare.setContent(commitContent);
		if (figureFile != null)
		{
			ushare.setContentfigureurl(figureFile);
		}
		ushare.setLove(0);
		ushare.setHate(0);
		ushare.setShare(0);
		ushare.setComment(0);
		ushare.setPass(true);
		ushare.save(new SaveListener()
		{
			@Override
			public void done(Object o, BmobException e) {
				if(e==null)
				{
					ActivityUtil.show(EditActivity.this, "发表成功！");
					LogUtils.i( "创建成功。");
					setResult(RESULT_OK);
					finish();
				}else {
					ActivityUtil.show(EditActivity.this, "发表失败！yg" + e.getMessage());
					LogUtils.i( "创建失败。" + e.getMessage());
				}
			}
		});
	}

	String targeturl = null;

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null)
		{
			List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);

			for (String path : pathList)
			{
				targeturl=path;

				albumPic.setBackgroundDrawable(new BitmapDrawable(path));
				takeLayout.setVisibility(View.GONE);
			}
		}

		LogUtils.i( "get album:");
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case REQUEST_CODE_CAMERA:
				String files = CacheUtils.getCacheDirectory(EditActivity.this, true, "pic") + dateTime;
				File file = new File(files);
				if (file.exists())
				{
					Bitmap bitmap = compressImageFromFile(files);
					targeturl = saveToSdCard(bitmap);
					takePic.setBackgroundDrawable(new BitmapDrawable(bitmap));
					openLayout.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
		}
	}


	private SImageLoader loader = new SImageLoader() {
		@Override
		public void displayImage(Context context, String path, ImageView imageView) {
			Glide.with(context).load(path).into(imageView);
		}
	};
	private void showAlbum()
	{
		ImgSelConfig config = new ImgSelConfig.Builder(this, loader)
				// 是否多选
				.multiSelect(false)
				.btnText("Confirm")
				// 确定按钮背景色
				// 确定按钮文字颜色
				.btnTextColor(Color.WHITE)
				// 使用沉浸式状态栏
				.statusBarColor(Color.parseColor("#3F51B5"))
				// 返回图标ResId
				.backResId(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material)
				.title("选择图片")
				.titleColor(Color.WHITE)
				.titleBgColor(Color.parseColor("#3F51B5"))
				.allImagesText("图库")
				.needCrop(false)
				.cropSize(1, 1, 200, 200)
				// 第一个是否显示相机
				.needCamera(false)
				// 最大选择图片数量
				.maxNum(9)
				.build();
		ImgSelActivity.startActivity(this, config, REQUEST_CODE);
	}
	private Bitmap compressImageFromFile(String srcPath)
	{
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww)
		{
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh)
		{
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}

	public String saveToSdCard(Bitmap bitmap)
	{
		String files = CacheUtils.getCacheDirectory(EditActivity.this, true, "pic") + dateTime + "_11.jpg";
		File file = new File(files);
		try
		{
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out))
			{
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtils.i(file.getAbsolutePath());
		return file.getAbsolutePath();
	}

}
