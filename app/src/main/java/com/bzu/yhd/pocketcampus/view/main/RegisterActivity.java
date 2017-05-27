package com.bzu.yhd.pocketcampus.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.widget.TimeButton;
import com.bzu.yhd.pocketcampus.widget.TransitionView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TransitionView mAnimView;
    private Button mButton;
    private EditText rg_phone, rg_sms, rg_passwd;
    private boolean mboolean = false;
    private TimeButton timeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        mAnimView = (TransitionView) findViewById(R.id.ani_view);
        rg_phone = (EditText) findViewById(R.id.rg_phone);
        rg_sms = (EditText) findViewById(R.id.rg_sms);
        rg_passwd = (EditText) findViewById(R.id.rg_password);

        timeButton = (TimeButton) findViewById(R.id.get_smscode);
        mButton = (Button) findViewById(R.id.bt_sign_up);
        timeButton.setOnClickListener(this);
        mButton.setOnClickListener(this);

        mAnimView.setOnAnimationEndListener(new TransitionView.OnAnimationEndListener() {
            @Override
            public void onEnd() {
                MyOneEnd();
            }
        });
    }

    private void MyOneEnd()
    {
        if (mboolean) {
            gotoHomeActivity();
        } else
            {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAnimView.setVisibility(View.GONE);
                }
            }, 1000);
        }
    }

    private void gotoHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    public void singUp(View view) {
        mAnimView.startErrorAnimation();
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
                            {//验证码发送成功
                                Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情
                                Toast.makeText(RegisterActivity.this, "发送验证码成功。", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "发送验证码失败，请检查网络重试。", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                    {
                    Toast.makeText(RegisterActivity.this,"输入的手机号不合法，请重新输入",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bt_sign_up:
                if (isPhoneNumberValid(username))
                {
                    if(isRightPwd(password))
                    {
                        if(message != "" && message != null)
                        {
                            register(username,password,message);
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,"验证码为空，请先获取验证码",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"密码不合法，请输入6位以上字母与数字组合的密码",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this,"手机号不合法，请输入11数字手机号",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }



    private void register(String username,String passwd,String smscode) {

        User user = new User();
        user.setMobilePhoneNumber(username);//设置手机号码（必填）
        user.setUsername(username);                  //设置用户名，如果没有传用户名，则默认为手机号码
        user.setPassword(passwd);                  //设置用户密码
        user.signOrLogin(smscode, new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null)
                {
                    Log.i("smile", "" + user.getUsername() + "-" + user.getAge() + "-" + user.getObjectId());
                    mboolean = true;
                    mAnimView.startSuccessAnimation();

                } else {
                    Toast.makeText(RegisterActivity.this, "失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    //注册失败的动画
                    mboolean = false;
                    mAnimView.startErrorAnimation();
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
