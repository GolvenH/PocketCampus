package com.bzu.yhd.pocketcampus.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.widget.TimeButton;
import com.bzu.yhd.pocketcampus.widget.TransitionView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class RePasswdActivity extends BaseActivity implements View.OnClickListener{

    private TransitionView mAnimView;
    private Button mButton;
    private EditText rg_phone, rg_sms, rg_passwd;
    private boolean mboolean = false;
    private TimeButton timeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_passwd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeToolbar();

        BaseApplication.getInstance().addActivity(this);
        rg_phone = (EditText) findViewById(R.id.rg_phone);
        rg_sms = (EditText) findViewById(R.id.rg_sms);
        rg_passwd = (EditText) findViewById(R.id.rg_password);

        timeButton = (TimeButton) findViewById(R.id.get_smscode);
        mButton = (Button) findViewById(R.id.bt_sign_up);
        timeButton.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String username = rg_phone.getText().toString().trim();
        String message = rg_sms.getText().toString().trim();
        String password = rg_passwd.getText().toString().trim();

        switch (v.getId()) {

            case R.id.get_smscode:
                if (isPhoneNumberValid(username))
                {
                    BmobSMS.requestSMSCode(username, "reg", new QueryListener<Integer>() {

                        @Override
                        public void done(Integer smsId,BmobException ex) {
                            if(ex==null)
                            {   //验证码发送成功
                                Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情showToast("发送验证码成功");
                            }
                            else
                            {
                                showToast("发送验证码失败，请检查网络重试");
                            }
                        }
                    });
                }
                else
                {
                    showToast("输入的手机号不合法，请重新输入");
                }
                break;

            case R.id.bt_sign_up:
                if (isPhoneNumberValid(username))
                {
                    if(isRightPwd(password))
                    {
                        if(message != "" && message != null)
                        {
                            register(password,message);
                        }
                        else
                        {
                            showToast("验证码为空，请先获取验证码");
                        }
                    }
                    else {
                        showToast("密码不合法，请输入6位以上字母与数字组合的密码");
                    }
                } else {
                   showToast("手机号不合法，请输入11数字手机号");
                }
                break;
        }
    }


    private void register(String passwd,String smscode) {

        BmobUser.resetPasswordBySMSCode(smscode, passwd,new UpdateListener() {

            @Override
            public void done(BmobException ex) {
                if(ex==null){
                    Log.i("smile", "密码重置成功");

                    BmobUser.logOut();   //清除缓存用户对象
                    BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
                    if (currentUser==null) {
                        showToast("密码重置成功,请返回重新登陆。");
                        Intent i = new Intent();
                        i.setClass(RePasswdActivity.this, LoginActivity.class);
                        startActivity(i);
                        BaseApplication.getInstance().exit();
                    }

                }else{
                    showToast("密码重置失败");
                    Log.i("smile", "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                }
            }
        });

    }

    public  boolean isRightPwd(String pwd) {
        Pattern p = Pattern.compile("^(?![^a-zA-Z]+$)(?!\\D+$)[0-9a-zA-Z]{8,16}$");
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    public boolean isPhoneNumberValid(String phoneNumber) {

        Pattern p = Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }


}
