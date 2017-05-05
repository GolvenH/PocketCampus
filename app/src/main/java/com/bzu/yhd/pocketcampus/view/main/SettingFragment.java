package com.bzu.yhd.pocketcampus.view.main;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.ColorRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.bottomnav.home.service.AutoUpdateService;
import com.bzu.yhd.pocketcampus.bottomnav.home.weather.domain.ChangeCityEvent;
import com.bzu.yhd.pocketcampus.bottomnav.home.weather.ui.WeatherActivity;
import com.bzu.yhd.pocketcampus.widget.utils.FileSizeUtil;
import com.bzu.yhd.pocketcampus.widget.utils.FileUtil;
import com.bzu.yhd.pocketcampus.widget.utils.PrefUtil;
import com.bzu.yhd.pocketcampus.widget.utils.RxUtils;
import com.bzu.yhd.pocketcampus.widget.utils.SharedPrefHelper;
import com.bzu.yhd.pocketcampus.widget.utils.SharedPreferenceUtil;
import com.bzu.yhd.pocketcampus.widget.utils.SimpleSubscriber;
import com.bzu.yhd.pocketcampus.widget.ImageLoader;
import com.bzu.yhd.pocketcampus.widget.RxBus;
import com.xw.repo.BubbleSeekBar;

import org.polaric.colorful.Colorful;

import java.io.File;
import java.math.BigDecimal;

import rx.Observable;
import rx.functions.Func1;

public class SettingFragment extends PreferenceFragment
    implements Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {
    private static String TAG = SettingFragment.class.getSimpleName();
    //private SettingActivity mActivity;
    private SharedPreferenceUtil mSharedPreferenceUtil;

    private Preference mDayTimePref;
    private Preference mNightTimePref;
    private Preference mChangeIcons;
    private Preference mChangeUpdate;
    private Preference mClearCache;
    private CheckBoxPreference mNotificationType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);


        SwitchPreference switchPref = (SwitchPreference) findPreference(getString(R.string.key_auto_day_night));
        mDayTimePref = findPreference(getString(R.string.key_day_mode_time));
        mNightTimePref = findPreference(getString(R.string.key_night_mode_time));
        if (!switchPref.isChecked()) {
            mDayTimePref.setEnabled(false);
            mNightTimePref.setEnabled(false);
        }

        int[] time = PrefUtil.getDayNightModeStartTime(true);
        int h = time[0];
        int m = time[1];
        mDayTimePref.setSummary(getString(R.string.format_hour_min2, formatTime(h), formatTime(m)));
        time = PrefUtil.getDayNightModeStartTime(false);
        h = time[0];
        m = time[1];
        mNightTimePref.setSummary(getString(R.string.format_hour_min2, formatTime(h), formatTime(m)));

        switchPref.setOnPreferenceChangeListener(this);

        mDayTimePref.setOnPreferenceClickListener(this);
        mNightTimePref.setOnPreferenceClickListener(this);
        mSharedPreferenceUtil = SharedPreferenceUtil.getInstance();

        mChangeIcons = findPreference(SharedPreferenceUtil.CHANGE_ICONS);
        mChangeUpdate = findPreference(SharedPreferenceUtil.AUTO_UPDATE);
        mClearCache = findPreference(SharedPreferenceUtil.CLEAR_CACHE);

        mNotificationType = (CheckBoxPreference) findPreference(SharedPreferenceUtil.NOTIFICATION_MODEL);

        if (SharedPreferenceUtil.getInstance().getNotificationModel() != Notification.FLAG_ONGOING_EVENT) {
            mNotificationType.setChecked(false);
        } else {
            mNotificationType.setChecked(true);
        }


        mChangeIcons.setSummary(getResources().getStringArray(R.array.icons)[mSharedPreferenceUtil.getIconType()]);

        mChangeUpdate.setSummary(
            mSharedPreferenceUtil.getAutoUpdate() == 0 ? "禁止刷新" : "每" + mSharedPreferenceUtil.getAutoUpdate() + "小时更新");
        mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(BaseApplication.getAppCacheDir() + "/NetCache"));

        mChangeIcons.setOnPreferenceClickListener(this);
        mChangeUpdate.setOnPreferenceClickListener(this);
        mClearCache.setOnPreferenceClickListener(this);
        mNotificationType.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (mChangeIcons == preference) {
            showIconDialog();
        } else if (mClearCache == preference) {

            ImageLoader.clear(getActivity());
            Observable.just(FileUtil.delete(new File(BaseApplication.getAppCacheDir() + "/NetCache")))
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean;
                    }
                }).compose(RxUtils.rxSchedulerHelper()).subscribe(new SimpleSubscriber<Boolean>() {
                @Override
                public void onNext(Boolean aBoolean) {
                    mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(BaseApplication.getAppCacheDir() + "/NetCache"));
                    Snackbar.make(getView(), "缓存已清除", Snackbar.LENGTH_SHORT).show();
                }
            });
        } else if (mChangeUpdate == preference) {
            showUpdateDialog();
        }

        switch (preference.getOrder()) {
            case 1:
                showPickTimeDialog(true);
                break;
            case 2:
                showPickTimeDialog(false);
                break;
        }
        return true;
    }

    private void showIconDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.icon_dialog, (ViewGroup) getActivity().findViewById(R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();

        LinearLayout layoutTypeOne = (LinearLayout) dialogLayout.findViewById(R.id.layout_one);
        layoutTypeOne.setClickable(true);
        RadioButton radioTypeOne = (RadioButton) dialogLayout.findViewById(R.id.radio_one);
        LinearLayout layoutTypeTwo = (LinearLayout) dialogLayout.findViewById(R.id.layout_two);
        layoutTypeTwo.setClickable(true);
        RadioButton radioTypeTwo = (RadioButton) dialogLayout.findViewById(R.id.radio_two);
        TextView done = (TextView) dialogLayout.findViewById(R.id.done);

        radioTypeOne.setClickable(false);
        radioTypeTwo.setClickable(false);

        alertDialog.show();

        switch (mSharedPreferenceUtil.getIconType()) {
            case 0:
                radioTypeOne.setChecked(true);
                radioTypeTwo.setChecked(false);
                break;
            case 1:
                radioTypeOne.setChecked(false);
                radioTypeTwo.setChecked(true);
                break;
        }

        layoutTypeOne.setOnClickListener(v -> {
            radioTypeOne.setChecked(true);
            radioTypeTwo.setChecked(false);
        });

        layoutTypeTwo.setOnClickListener(v -> {
            radioTypeOne.setChecked(false);
            radioTypeTwo.setChecked(true);
        });

        done.setOnClickListener(v -> {
            mSharedPreferenceUtil.setIconType(radioTypeOne.isChecked() ? 0 : 1);
            String[] iconsText = getResources().getStringArray(R.array.icons);
            mChangeIcons.setSummary(radioTypeOne.isChecked() ? iconsText[0] :
                iconsText[1]);
            alertDialog.dismiss();
            Snackbar.make(getView(), "切换成功,重启应用生效",
                Snackbar.LENGTH_INDEFINITE).setAction("重启",
                v1 -> {

                    //Intent intent =
                    //    getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    RxBus.getDefault().post(new ChangeCityEvent());
                }).show();
        });
    }

    private void showUpdateDialog() {
        //将 SeekBar 放入 Dialog 的方案 http://stackoverflow.com/questions/7184104/how-do-i-put-a-seek-bar-in-an-alert-dialog
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.update_dialog, (ViewGroup) getActivity().findViewById(
            R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
            .setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();

        final SeekBar mSeekBar = (SeekBar) dialogLayout.findViewById(R.id.time_seekbar);
        final TextView tvShowHour = (TextView) dialogLayout.findViewById(R.id.tv_showhour);
        TextView tvDone = (TextView) dialogLayout.findViewById(R.id.done);

        mSeekBar.setMax(24);
        mSeekBar.setProgress(mSharedPreferenceUtil.getAutoUpdate());
        tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()));
        alertDialog.show();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvDone.setOnClickListener(v -> {
            mSharedPreferenceUtil.setAutoUpdate(mSeekBar.getProgress());
            mChangeUpdate.setSummary(
                mSharedPreferenceUtil.getAutoUpdate() == 0 ? "禁止刷新" : "每" + mSharedPreferenceUtil.getAutoUpdate() + "小时更新");
            //需要再调用一次才能生效设置 不会重复的执行onCreate()， 而是会调用onStart()和onStartCommand()。
            getActivity().startService(new Intent(getActivity(), AutoUpdateService.class));
            alertDialog.dismiss();
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (mNotificationType == preference)
            {
            SharedPreferenceUtil.getInstance().setNotificationModel(
                (boolean) newValue ? Notification.FLAG_ONGOING_EVENT : Notification.FLAG_AUTO_CANCEL);
        }

        switch (preference.getOrder()) {
            case 0:
                if (newValue instanceof Boolean) {
                    mDayTimePref.setEnabled((boolean) newValue);
                    mNightTimePref.setEnabled((boolean) newValue);
                }
                break;
        }

        return true;
    }



    private void showPickTimeDialog(final boolean isDay) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_pick_time_dialog, null);
        final TextView hourMinText = (TextView) view.findViewById(R.id.dialog_hour_min_text);
        final BubbleSeekBar hourBar = (BubbleSeekBar) view.findViewById(R.id.dialog_hour_seek_bar);
        final BubbleSeekBar minBar = (BubbleSeekBar) view.findViewById(R.id.dialog_min_seek_bar);

        int[] time = PrefUtil.getDayNightModeStartTime(isDay);
        int h = time[0];
        int m = time[1];

        hourMinText.setText(getString(R.string.format_hour_min, formatTime(h), formatTime(m)));
        hourBar.getConfigBuilder()
                .progress(h)
                .secondTrackColor(resolveColor(Colorful.getThemeDelegate().getAccentColor().getColorRes()))
                .thumbColor(resolveColor(Colorful.getThemeDelegate().getAccentColor().getColorRes()))
                .bubbleColor(resolveColor(Colorful.getThemeDelegate().getAccentColor().getColorRes()))
                .build();
        minBar.getConfigBuilder()
                .progress(m)
                .secondTrackColor(resolveColor(Colorful.getThemeDelegate().getAccentColor().getColorRes()))
                .thumbColor(resolveColor(Colorful.getThemeDelegate().getAccentColor().getColorRes()))
                .bubbleColor(resolveColor(Colorful.getThemeDelegate().getAccentColor().getColorRes()))
                .build();
        if (Colorful.getThemeDelegate().isNight()) {
            hourBar.getConfigBuilder()
                    .trackColor(resolveColor(android.R.color.darker_gray))
                    .sectionTextColor(resolveColor(android.R.color.white))
                    .build();
            minBar.getConfigBuilder()
                    .trackColor(resolveColor(android.R.color.darker_gray))
                    .sectionTextColor(resolveColor(android.R.color.white))
                    .build();
        }
        hourBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                hourMinText.setText(getString(R.string.format_hour_min, formatTime(progress),
                        formatTime(minBar.getProgress())));
            }
        });
        minBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                hourMinText.setText(getString(R.string.format_hour_min,
                        formatTime(hourBar.getProgress()), formatTime(progress)));
            }
        });
        builder.setView(view);
        builder.setTitle(isDay ? "日间模式开始时间" : "夜间模式开始时间");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String s = formatTime(hourBar.getProgress()) + ":" + formatTime(minBar.getProgress());
                if (isDay) {
                    SharedPrefHelper.putString(getString(R.string.key_day_mode_time), s);
                    mDayTimePref.setSummary(s);
                } else {
                    SharedPrefHelper.putString(getString(R.string.key_night_mode_time), s);
                    mNightTimePref.setSummary(s);
                }
            }
        });
        builder.show();
    }

    private String formatTime(int time) {
        return time < 10 ? "0" + time : "" + time;
    }

    private int resolveColor(@ColorRes int res) {
        return ContextCompat.getColor(getActivity(), res);
    }

    private String getCacheSizeString() {
        long size = getFolderSize(new File(getActivity().getCacheDir().getAbsolutePath()));
        return formatFileSize(size);
    }

    private boolean clearCache() {
        return deleteDir(new File(getActivity().getCacheDir().getAbsolutePath()));
    }

    private long getFolderSize(File file) {
        if (!file.exists()) {
            return 0;
        }

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    private String formatFileSize(double size) {
        double kByte = size / 1024;
        if (kByte < 1) {
            return size + "B";
        }
        double mByte = kByte / 1024;
        if (mByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gByte = mByte / 1024;
        if (gByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(mByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double tByte = gByte / 1024;
        if (tByte < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(tByte);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    private boolean deleteDir(File dir) {
        if (dir == null || !dir.exists())
            return false;

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
