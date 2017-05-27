package com.bzu.yhd.pocketcampus.bottomnav.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.bottomnav.user.view.BlurBitmapUtil;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.view.main.HomeActivity;
import com.bzu.yhd.pocketcampus.view.main.SettingActivity;
import com.bzu.yhd.pocketcampus.view.main.ThemeActivity;
import com.bzu.yhd.pocketcampus.widget.utils.PrefUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.polaric.colorful.Colorful;

import java.util.Calendar;

import cn.bmob.v3.datatype.BmobFile;

/**
 * @CreateBy YHD
 * </p>
 * @CreateOn 2017/3/14 13:32
 */
public class UserInfoFragment extends Fragment {

    private RelativeLayout rel_userinfo,rel_theme, rel_setting;
    private Intent intent;

    private SwitchCompat mSwitch;
    private TextView mDayNightText;
    private String mAutoSwitchedHint;
    private LinearLayout colorlayout;
    private ImageView ivBackGround;
    private TextView txmytalk;
    private TextView txmycollection ;
    private User user;

    public static UserInfoFragment newInstance(String param1, String param2) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

        View view = inflater.inflate(R.layout.fragment_more, container, false);
        if (savedInstanceState == null)
        {
            checkAutoDayNightMode();
        }
        rel_userinfo = (RelativeLayout) view.findViewById(R.id.user_info);
        rel_theme = (RelativeLayout) view.findViewById(R.id.layout_theme_change);
        rel_setting = (RelativeLayout) view.findViewById(R.id.layout_setting);
        colorlayout = (LinearLayout) view.findViewById(R.id.colorlayout);

        mSwitch = (SwitchCompat) view.findViewById(R.id.day_night_mode_switch);
        mDayNightText = (TextView) view.findViewById(R.id.day_night_mode_text);
        txmytalk = (TextView) view.findViewById(R.id.mytalk);
        txmycollection = (TextView) view.findViewById(R.id.mycollection);

        mSwitch.setChecked(Colorful.getThemeDelegate().isNight());
        mDayNightText.setText(mSwitch.isChecked() ? getString(R.string.night_mode) : getString(R.string.day_mode));

        if(Colorful.getThemeDelegate().isNight())
        {
            colorlayout.setBackgroundColor(Color.parseColor("#303030"));
        }
        else{
            colorlayout.setBackgroundColor(Color.parseColor("#eeeeee"));
        }

        rel_userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent();
                intent.setClass(getActivity(), MeEditActivity.class);
                getActivity().startActivity(intent);
            }
        });
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (PrefUtil.isAutoDayNightMode()) {
                    Toast.makeText(getActivity(), getString(R.string.hint_auto_day_night_disabled),
                            Toast.LENGTH_LONG).show();
                    PrefUtil.setAutoDayNightMode(false);

                }
                switchDayNightModeSmoothly(isChecked, false);
            }
        });

        rel_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.layout_theme_change) {
                    intent = new Intent();
                    intent.setClass(getActivity(), ThemeActivity.class);
                    getActivity().startActivity(intent);

                }
            }
        });
        rel_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.layout_setting) {
                    intent = new Intent();
                    intent.setClass(getActivity(), SettingActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });


        ivBackGround = (ImageView) view.findViewById(R.id.iv_bg);
        Bitmap bitmap = BlurBitmapUtil.blurBitmap(getActivity(), BitmapFactory.decodeResource(getResources(), R.mipmap.avatar), 3f);

        user = BaseApplication.getInstance().getCurrentUser();

        BmobFile file = user.getFile();
        if (null != file) {
            ImageLoader.getInstance().displayImage(
                    file.getFileUrl(), ivBackGround,
                    BaseApplication.getInstance().getOptions(
                            R.mipmap.avatar),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(
                                String imageUri, View view,
                                Bitmap loadedImage) {
                            // TODO Auto-generated method
                            // stub
                            super.onLoadingComplete(
                                    imageUri, view,
                                    loadedImage);
                        }

                    });
/*
            ivBackGround.setImageBitmap(bitmap);
*/
        }
        return view;
    }

    /**
     * 检测是否自动日夜模式，如果是自动将根据时间判断是否切换
     */
    private void checkAutoDayNightMode() {
        boolean firstTime = PrefUtil.checkFirstTime();
        if (firstTime)
            PrefUtil.setNotFirstTime();
        boolean auto = PrefUtil.isAutoDayNightMode();
        if (firstTime || !auto)
            return;

        int[] dayTime = PrefUtil.getDayNightModeStartTime(true);
        int[] nightTime = PrefUtil.getDayNightModeStartTime(false);
        Calendar cal = Calendar.getInstance();
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int m = cal.get(Calendar.MINUTE);
        if (Colorful.getThemeDelegate().isNight()) {
            if ((dayTime[0] < h && h < nightTime[0]) || (dayTime[0] == h && dayTime[1] <= m)) {
                switchDayNightModeSmoothly(false, true);
            }
        } else {
            if ((nightTime[0] < h) || (nightTime[0] == h && nightTime[1] <= m)) {
                switchDayNightModeSmoothly(true, true);
            }
        }
    }

    private void switchDayNightModeSmoothly(final boolean isDark, boolean delay) {
        if (delay) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Colorful.config(getActivity()).night(isDark).apply();
                    ((HomeActivity) getActivity()).getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                    mAutoSwitchedHint =
                            "已自动切换为" + (isDark ? getString(R.string.night_mode) : getString(R.string.day_mode));
                    Toast.makeText(getActivity(), mAutoSwitchedHint, Toast.LENGTH_SHORT).show();
                    ((HomeActivity) getActivity()).recreate();
                }
            }, 1000);
        } else {
            Colorful.config(getActivity()).night(isDark).apply();
            ((HomeActivity) getActivity()).getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
            ((HomeActivity) getActivity()).recreate();
        }
    }
}
