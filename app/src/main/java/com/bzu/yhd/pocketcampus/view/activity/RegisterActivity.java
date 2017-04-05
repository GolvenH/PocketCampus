package com.bzu.yhd.pocketcampus.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.view.widget.TimeButton;
import com.bzu.yhd.pocketcampus.view.widget.TransitionView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {

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

    private void MyOneEnd() {
        if (mboolean) {
            gotoHomeActivity();
        } else {
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
        String message = rg_sms.getText().toString();
        switch (v.getId()) {
            case R.id.bt_sign_up:

                break;
            case R.id.get_smscode:
                if (isPhoneNumberValid(username)) {

                } else {
                    showToast("输入的手机号不合法，请重新输入");
                }
                break;
        }
    }

    private void register() {
        String username = rg_phone.getText().toString().trim();
        String password = rg_passwd.getText().toString().trim();
        if (isRightPwd(password)) {
            mboolean = true;
            mAnimView.startSuccessAnimation();

            //注册失败的动画
//            mboolean = false;
//            mAnimView.startErrorAnimation();

        } else {
            showToast("密码不合法，请输入6位以上字母与数字组合的密码");
        }
    }

    public static final boolean isRightPwd(String pwd) {
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
