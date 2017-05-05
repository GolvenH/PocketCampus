package com.bzu.yhd.pocketcampus.widget.utils;


import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseApplication;

import static com.bzu.yhd.pocketcampus.widget.utils.SharedPrefHelper.getString;


/**
 * 方便app常用SharedPreferences直接操作类
 * <p/>
 * Created by woxingxiao on 2017-02-16.
 */
public final class PrefUtil {

    public static boolean checkFirstTime() {
        return SharedPrefHelper.getBoolean(BaseApplication.sBaseApplication.getString(R.string.key_first_time), true);
    }

    public static void setNotFirstTime() {
        SharedPrefHelper.putBoolean(BaseApplication.sBaseApplication.getString(R.string.key_first_time), false);
    }

    public static int[] getDayNightModeStartTime(boolean isDay) {
        int[] time = new int[2];
        String timeStr;
        if (isDay) {
            timeStr = getString(BaseApplication.sBaseApplication.getString(R.string.key_day_mode_time), "8:00");
        } else {
            timeStr = getString(BaseApplication.sBaseApplication.getString(R.string.key_night_mode_time), "18:00");
        }
        String[] str = timeStr.split(":");
        time[0] = Integer.valueOf(str[0]);
        time[1] = Integer.valueOf(str[1]);
        return time;
    }

    public static boolean isAutoDayNightMode() {
        return SharedPrefHelper.getBoolean(BaseApplication.sBaseApplication.getString(R.string.key_auto_day_night), true);
    }

    public static void setAutoDayNightMode(boolean auto) {
        SharedPrefHelper.putBoolean(BaseApplication.sBaseApplication.getString(R.string.key_auto_day_night), auto);
    }

    public static String getCity() {
        return getString(BaseApplication.sBaseApplication.getString(R.string.key_city), "滨州市");
    }

    public static String getCityShort() {
        return Util.trimCity(getCity());
    }

    public static void setCity(String city) {
        SharedPrefHelper.putString(BaseApplication.sBaseApplication.getString(R.string.key_city), city);
    }

    public static void clearCity() {
        SharedPrefHelper.putString(BaseApplication.sBaseApplication.getString(R.string.key_city), "");
        SharedPrefHelper.putString(BaseApplication.sBaseApplication.getString(R.string.key_upper_city), "");
        SharedPrefHelper.putBoolean(BaseApplication.sBaseApplication.getString(R.string.key_inputted_city), false);
    }

    public static String getUpperCity() {
        return getString(BaseApplication.sBaseApplication.getString(R.string.key_upper_city), "");
    }

    public static void setUpperCity(String city) {
        SharedPrefHelper.putString(BaseApplication.sBaseApplication.getString(R.string.key_upper_city), city);
    }

    public static boolean isInputtedCity() {
        return SharedPrefHelper.getBoolean(BaseApplication.sBaseApplication.getString(R.string.key_inputted_city), false);
    }

    public static void setInputtedCity(boolean inputted) {
        SharedPrefHelper.putBoolean(BaseApplication.sBaseApplication.getString(R.string.key_inputted_city), inputted);
    }
}