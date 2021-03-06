package com.bzu.yhd.pocketcampus.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;
import com.bzu.yhd.pocketcampus.base.BaseApplication;

import org.polaric.colorful.Colorful;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 关于
 * <p/>
 * Created by Yhd on 2017-02-10.
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.about_header_img)
    ImageView mHeaderImg;
    @BindView(R.id.version_name_text)
    TextView mVersionNameText;
    @BindView(R.id.about_github_img)
    ImageView mGitHubImg;
    @BindView(R.id.about_license_text)
    TextView mLicenseText;
    private Unbinder mUnbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mUnbinder = ButterKnife.bind(this);
        initializeToolbar();
        BaseApplication.getInstance().addActivity(this);

        Glide.with(this).load(R.mipmap.flowbloom).into(mHeaderImg);
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String s = String.format(Locale.CHINA, "版本：%s（Build %d）", info.versionName, info.versionCode);
            mVersionNameText.setText(s);
        } catch (Exception e) {
            e.printStackTrace();
            mVersionNameText.setText("");
        }
        if (Colorful.getThemeDelegate().isNight()) {
            Drawable drawable = mGitHubImg.getDrawable();
            if (drawable != null) {
                DrawableCompat.setTint(drawable, Color.WHITE);
                mGitHubImg.setImageDrawable(drawable);
            }
        }
        mLicenseText.setText(Html.fromHtml(getString(R.string.license)));
    }

    @OnClick({R.id.about_github_img, R.id.about_license_text,R.id.feed_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_github_img:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(getString(R.string.github_page)));
                startActivity(Intent.createChooser(intent, "请选择浏览器"));
                break;
            case R.id.about_license_text:
                navigateTo(OpenLicenseActivity.class);
                break;
            case R.id.feed_back:
                showToast("意见反馈");
                break;
        }
    }

}
