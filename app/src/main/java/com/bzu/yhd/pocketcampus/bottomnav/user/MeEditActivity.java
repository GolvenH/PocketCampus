package com.bzu.yhd.pocketcampus.bottomnav.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.bottomnav.user.view.AddressPickTask;
import com.bzu.yhd.pocketcampus.main.RePasswdActivity;
import com.bzu.yhd.pocketcampus.widget.utils.CacheUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.victor.loading.rotate.RotateLoading;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.yuyh.library.imgsel.SImageLoader;

import java.io.File;
import java.util.List;

import cn.addapp.pickers.entity.City;
import cn.addapp.pickers.entity.County;
import cn.addapp.pickers.entity.Province;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MeEditActivity extends BaseActivity implements OnClickListener{
    private final static String TAG = MeEditActivity.class.getName();

    private static final int REQUEST_CODE = 0;

    static final int UPDATE_SEX = 11;
    static final int UPDATE_ICON = 12;
    static final int GO_LOGIN = 13;
    static final int UPDATE_SIGN = 14;
    static final int EDIT_SIGN = 15;
    static final int BIRTH_SIGN = 16;

    private Toolbar toolbar;
    private ImageView ivAvatar;
    private ImageView ivAvatarNew;
    private ImageView ivAvatarAction;
    private TextView txDisplayName;
    private TextView tvUsername;
    private ImageView ivUsernameValidation;
    private TextView txPhone;
    private ImageView ivEmailValidation;
    private TextView txGender;
    private TextView txCity;
    private TextView txSignature;
    private TextView txrepasswd;

    private FloatingActionButton fab;
    private RotateLoading loading;

    private User user;

    private static Bitmap bitmap;


    private boolean isLogined()
    {
        BmobUser user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            return true;
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_edit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("编辑资料");
        BaseApplication.getInstance().addActivity(this);

        Fresco.initialize(this);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initView();
        initData();
    }



    protected void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        loading = (RotateLoading) findViewById(R.id.loading);
        ivAvatar = (ImageView) findViewById(R.id.iv_me_edit_avatar);
        ivAvatarNew = (ImageView) findViewById(R.id.iv_me_edit_avatar_new);
        ivAvatarAction = (ImageView) findViewById(R.id.iv_me_edit_avatar_action);
        txDisplayName = (TextView) findViewById(R.id.tv_me_edit_displayname_value);
        tvUsername = (TextView) findViewById(R.id.tv_me_edit_username_value);
        ivUsernameValidation = (ImageView) findViewById(R.id.iv_me_edit_username_validation);
        txPhone = (TextView) findViewById(R.id.tv_me_edit_phone_value);
        ivEmailValidation = (ImageView) findViewById(R.id.iv_me_edit_phone_validation);
        txGender = (TextView) findViewById(R.id.tv_me_edit_gender_value);
        txCity = (TextView) findViewById(R.id.tv_me_edit_city_value);
        txSignature = (TextView) findViewById(R.id.tv_me_edit_signature_value);
        txrepasswd = (TextView) findViewById(R.id.tv_me_edit_reset_password);
        ivAvatar.setOnClickListener(this);
        ivAvatarNew.setOnClickListener(this);
        ivAvatarAction.setOnClickListener(this);
        txDisplayName.setOnClickListener(this);
        tvUsername.setOnClickListener(this);
        ivUsernameValidation.setOnClickListener(this);
        txPhone.setOnClickListener(this);
        ivEmailValidation.setOnClickListener(this);
        txGender.setOnClickListener(this);
        txCity.setOnClickListener(this);
        txSignature.setOnClickListener(this);
        txrepasswd.setOnClickListener(this);
    }


    private void initData() {
        user = BaseApplication.getInstance().getCurrentUser();
            if (user != null) {
                //昵称
                if (user.getNickName() == null) {
                    txDisplayName.setText("快来设置昵称吧~");

                } else {
                    txDisplayName.setText(user.getNickName());
                }

                //地区
                if (user.getMobilePhoneNumber() == null) {
                    txCity.setText("绑定手机号");

                } else {
                   txPhone.setText(user.getMobilePhoneNumber());
                }

                //地区
                if (user.getArea() == null) {
                    txCity.setText("快来设置，寻找同乡吧~");

                } else {
                    String s=user.getArea();
                    txCity.setText(s);
                }

                //签名
                txSignature.setText(user.getSignature());
                //性别
                txGender.setText(user.getSex());

                BmobFile file = user.getFile();
                if (null != file) {
                    ImageLoader.getInstance().displayImage(
                            file.getFileUrl(), ivAvatar,
                            BaseApplication.getInstance().getOptions(
                                    R.mipmap.avatar),
                            new SimpleImageLoadingListener()
                            {
                                @Override
                                public void onLoadingComplete(
                                        String imageUri, View view,
                                        Bitmap loadedImage)
                                {
                                    // TODO Auto-generated method
                                    // stub
                                    super.onLoadingComplete(
                                            imageUri, view,
                                            loadedImage);
                                }

                            });

            }
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

    }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_me_edit_avatar_action:
                if (isLogined()) {
                    showAlbum();
                }
                break;
            case R.id.tv_me_edit_displayname_value:
                // 无需设置
                change_nickname("" + txDisplayName.getText().toString().trim());
                break;
            case R.id.tv_me_edit_signature_value:
                if (isLogined()) {
                    change_sign("" + txSignature.getText().toString().trim());
                }
                break;
            case R.id.tv_me_edit_reset_password:
                Intent i=new Intent();
                i.setClass(this,RePasswdActivity.class);
                startActivity(i);
                break;
            case R.id.tv_me_edit_city_value:
                // 地区
                AddressPickTask task = new AddressPickTask(this);
                task.setHideProvince(false);
                task.setHideCounty(false);
                task.setCallback(new AddressPickTask.Callback() {
                    @Override
                    public void onAddressInitFailed() {
                        showToast("数据初始化失败");
                    }

                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        if (county == null) {
                            String mycity=province.getAreaName()+"-"+ city.getAreaName();
                            showToast(mycity);
                            txCity.setText(mycity);
                            updateCity(mycity);
                        } else {
                            String mycity=province.getAreaName() +"-"+ city.getAreaName() +"-"+ county.getAreaName();
                            showToast(mycity);
                            txCity.setText(mycity);
                            updateCity(mycity);
                        }
                    }
                });
                task.execute("山东", "滨州", "滨城");

                break;
            case R.id.tv_me_edit_gender_value:
                // 性别
                showSexDialog();
                break;
            default:
                break;
        }    }

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
                //.btnBgColor(Color.parseColor(""))
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
                .needCrop(true)
                .cropSize(1, 1, 200, 200)
                // 第一个是否显示相机
                .needCamera(false)
                // 最大选择图片数量
                .maxNum(9)
                .build();

        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }


    /**
     * 更改昵称
     */
    public void change_nickname(String nickname){

        final EditText texta = new EditText(this);
        texta.setText(nickname);
        new AlertDialog.Builder(this)
                .setTitle("请输入您的昵称")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(texta)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = texta.getEditableText().toString();
                        updateNick(nickname);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateNick(String sign){
        User user = BmobUser.getCurrentUser(User.class);
        if(user != null){
            user.setNickName(sign);
            user.update(new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        showToast("更改信息成功。");
                        initData();
                        Log.i("bmob","更新成功");
                    }else{
                        showToast("更改信息成功。");
                        Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    /**
     * 更改城市
     */
    private void updateCity(String city){
        User user = BmobUser.getCurrentUser(User.class);
        if(user != null){
            user.setArea(city);
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        showToast("更改信息成功。");
                        initData();
                        Log.i("bmob","更新成功");
                    }else{
                        showToast("更改信息成功。");
                        Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    /**
     * 更改签名
     */
    public void change_sign(String nickname){

        final EditText texta = new EditText(this);
        texta.setText(nickname);
        new AlertDialog.Builder(this)
                .setTitle("请输入您的昵称")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(texta)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = texta.getEditableText().toString();
                        updateSign(nickname);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateSign(String sign){
        User user = BmobUser.getCurrentUser(User.class);
        if(user != null){
            user.setSignature(sign);
            user.update( new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        showToast("更改信息成功。");
                        initData();
                        Log.i("bmob","更新成功");
                    }else{
                        showToast("更改信息成功。");
                        Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }
    private void showSexDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        LinearLayout ll_title = (LinearLayout) window
                .findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("性别");
        // 为确认按钮添加事件,执行退出应用操作
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("男");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                if (!txGender.equals("1")) {
                    txGender.setText("男");
                    updateSex(1);
                }
                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("女");
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!txGender.equals("2")) {

                    txGender.setText("女");
                    updateSex(2);
                }
                dlg.cancel();
            }
        });
    }

    private void updateSex(int sex)
    {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            if (sex == 1) {
                user.setSex("男");
            } else {
                user.setSex("女");
            }
            user.update(new UpdateListener()
            {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        showToast("更改信息成功。");
                        initData();
                        Log.i("bmob","更新成功");
                    }else{
                        showToast("更改信息成功。");
                        Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    private void updateAvatar()
    {
        User user = BmobUser.getCurrentUser(User.class);
        BmobFile file = user.getFile();
        if (null != file) {
            String savata= file.getFileUrl();
            user.setAvatar(savata);
            user.update(new UpdateListener()
            {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        showToast("更改信息成功。");
                        initData();
                        Log.i("bmob","更新成功");
                    }else{
                        showToast("更改信息成功。");
                        Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    String dateTime;


    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);

            for (String path : pathList)
            {
                updateIcon(path);
                tvUsername.append(path + "\n");
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode)
            {
                case UPDATE_SEX:
                    initData();
                    break;
                case UPDATE_ICON:
                    initData();
                    ivAvatar.performClick();
                    break;
                case UPDATE_SIGN:
                    initData();
                    txSignature.performClick();
                    break;
                case EDIT_SIGN:
                    initData();                   //bug
                    break;
                case 1:
                    String files = CacheUtils.getCacheDirectory(this,
                            true, "icon") + dateTime;
                    File file = new File(files);
                    if (file.exists() && file.length() > 0) {
                        Uri uri = Uri.fromFile(file);
                        startPhotoZoom(uri);
                    } else {

                    }
                    break;
                case 2:
                    if (data == null) {
                        return;
                    }
                    startPhotoZoom(data.getData());
                    break;
                case 3:
                    if (data != null) {
                        Bundle extras = data.getExtras();

                    }
                    break;
                case GO_LOGIN:
                    initData();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateIcon(String avataPath)
    {
        if (avataPath != null) {
            final BmobFile file = new BmobFile(new File(avataPath));
            file.uploadblock( new UploadFileListener()
            {

                @Override
                public void done(BmobException e) {
                    if(e==null)
                    {
                        User currentUser = BmobUser.getCurrentUser(User.class);
                        currentUser.setFile(file);
                        currentUser.update(new UpdateListener()
                                {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            showToast("上传头像成功。");
                                            initData();
                                            updateAvatar();
                                            Log.i("bmob","更新成功");
                                        }else{
                                            showToast("上传头像失败。");
                                            Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                                        }
                                    }
                                });

                    }else{
                        showToast("上传文件失败：" + e.getMessage());
                    }
                }
                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }
            });
        }
    }

    public void startPhotoZoom(Uri uri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 120);
        intent.putExtra("outputY", 120);
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去锟斤拷锟节憋拷
        intent.putExtra("scaleUpIfNeeded", true);// 去锟斤拷锟节憋拷
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);

    }
}
