package com.bzu.yhd.pocketcampus.bottomnav.home.banner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;

public class WebViewActivity extends BaseActivity {

    private WebView mWebView;
    private String url="";
    String flag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeToolbar();
        toolbar.setTitle("校园中心");
        mWebView= (WebView) findViewById(R.id.myWebView);


        mWebView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        mWebView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        mWebView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        mWebView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        Intent intent1 = this.getIntent();
        flag = intent1.getStringExtra("flag");


        if(flag.equals("0"))
        {
            url="http://www.bztc.edu.cn/2017/0609/c4995a157038/page.htm";
        }
        if(flag.equals("1"))
        {
            url="http://www.bztc.edu.cn/2017/0608/c4995a157005/page.htm";
        }
        if(flag.equals("2"))
        {
            url="http://www.bztc.edu.cn/2017/0608/c4995a157001/page.htm";
        }
        if(flag.equals("3"))
        {
            url="http://www.bztc.edu.cn/2017/0608/c4995a156968/page.htm";
        }
        if(flag.equals("5"))
        {
            url="http://www.bztc.edu.cn/2017/0607/c4995a156899/page.htm";
        }
        if(flag.equals("11"))
        {
            url="http://www.bztc.edu.cn/";
        }
        if(flag.equals("12"))
        {
            url="http://jwc.bzu.edu.cn/";
        }

        if(flag.equals("3333"))
        {
            url="http://10.3.21.195:8080/PocketCampus/index.jsp";
        }
        inindate(url);
    }

    private void inindate(String myurl)
    {

        //屏蔽系统自带浏览器方法
        WebSettings wSet = mWebView.getSettings();
        wSet.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
        //屏蔽系统自带浏览器方法
        mWebView.loadUrl(myurl);

    }

}
