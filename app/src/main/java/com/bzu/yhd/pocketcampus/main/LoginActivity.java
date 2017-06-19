package com.bzu.yhd.pocketcampus.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.widget.utils.HttpDialogUtils;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

;

/**
 * 登录
 * </p>
 * @CreateBy Yhd On 2017/3/22 15:20
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    public static void navigation(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }


    private TextView mBtnforgetPwd,mBtnregeiste;
    EditText etUsername;
    EditText etPassword;
    Button btGo;
    private Intent i;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btGo = (Button) findViewById(R.id.bt_go);
        mBtnforgetPwd=(TextView) findViewById(R.id.tv_forgetpasswd);
        mBtnregeiste=(TextView) findViewById(R.id.tv_register);
        btGo.setOnClickListener(this);
        mBtnforgetPwd.setOnClickListener(this);
        mBtnregeiste.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bt_go:
                login();
                break;
            case R.id.tv_register:
                i=new Intent();
                i.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.tv_forgetpasswd:

                i=new Intent();
                i.setClass(LoginActivity.this,RePasswdActivity.class);
                startActivity(i);
                break;
        }
    }
    private void login() {
        String userName = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "请完整输入用户名和密码", Toast.LENGTH_SHORT).show();
        }else
        {
            HttpDialogUtils.showDialog(this, true, "正在登录");
            new Handler().postDelayed(new Runnable()
            {
                @Override public void run()
                {
                    HttpDialogUtils.dismissDialog();
                }
            }, 1000);

            BmobUser.loginByAccount(userName, password, new LogInListener<User>() {

                @Override
                public void done(User user, BmobException e) {
                    if(user!=null){
                        Log.i("smile","用户登陆成功");
                        Toast.makeText(LoginActivity.this, "登陆成功,欢迎回来", Toast.LENGTH_SHORT).show();

                        BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getNickName()));


                        Intent i=new Intent();
                        i.setClass(LoginActivity.this,HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                            int errorcode=e.getErrorCode();
                            if (errorcode==9001)
                            {
                                Toast.makeText(LoginActivity.this, "用户登陆失败，Application Id为空，请初始化.", Toast.LENGTH_SHORT).show();
                            }
                            if (errorcode==9002)
                            {
                                Toast.makeText(LoginActivity.this, "用户登陆失败，解析返回数据出错.", Toast.LENGTH_SHORT).show();
                            }
                            if (errorcode==9010)
                            {
                                Toast.makeText(LoginActivity.this, "用户登陆失败，网络超时.", Toast.LENGTH_SHORT).show();
                            }
                            if (errorcode==9016)
                            {
                                Toast.makeText(LoginActivity.this, "用户登陆失败，无网络连接，请检查您的手机网络.", Toast.LENGTH_SHORT).show();
                            }
                            if (errorcode==101)
                            {
                                Toast.makeText(LoginActivity.this, "用户登陆失败，用户名密码错误", Toast.LENGTH_SHORT).show();
                            }
                    }
                }
            });
        }
    }

}
