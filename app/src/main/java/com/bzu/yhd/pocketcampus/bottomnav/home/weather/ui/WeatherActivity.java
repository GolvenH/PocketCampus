package com.bzu.yhd.pocketcampus.bottomnav.home.weather.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;
import com.bzu.yhd.pocketcampus.bottomnav.home.service.AutoUpdateService;
import com.bzu.yhd.pocketcampus.bottomnav.home.service.LocationService;
import com.bzu.yhd.pocketcampus.widget.utils.PLog;
import com.bzu.yhd.pocketcampus.widget.utils.PrefUtil;
import com.bzu.yhd.pocketcampus.widget.utils.SharedPreferenceUtil;

import org.polaric.colorful.Colorful;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WeatherActivity extends BaseActivity {

    private TextView mCityText;
    private MyReceiver mReceiver;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private Unbinder mUnbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initializeToolbar();
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, new IntentFilter(getString(R.string.action_locate_succeed)));

        mUnbinder = ButterKnife.bind(this);

        PLog.i("onCreate");
        initView();
        initIcon();
        startService(new Intent(this, AutoUpdateService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        PLog.i("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PLog.i("onRestart");
        //为了实现 Intent 重启使图标生效
        initIcon();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PLog.i("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        PLog.i("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PLog.i("onStop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        final MenuItem item = menu.findItem(R.id.action_location);

        mCityText = (TextView) item.getActionView().findViewById(R.id.menu_location_text);
        mCityText.setText(PrefUtil.getCityShort());

        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onOptionsItemSelected(item);
            }
        });

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_location) {
            showLocatedCityDialog(false, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化基础View
     */
    protected void initView() {

        MainFragment fragment=new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.weather_content, fragment).commit();

    }

    /**
     * 初始化Icon
     */
    private void initIcon() {
        if (SharedPreferenceUtil.getInstance().getIconType() == 0) {
            SharedPreferenceUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferenceUtil.getInstance().putInt("晴", R.mipmap.type_one_sunny);
            SharedPreferenceUtil.getInstance().putInt("阴", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("多云", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("少云", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            SharedPreferenceUtil.getInstance().putInt("小雨", R.mipmap.type_one_light_rain);
            SharedPreferenceUtil.getInstance().putInt("中雨", R.mipmap.type_one_light_rain);
            SharedPreferenceUtil.getInstance().putInt("大雨", R.mipmap.type_one_heavy_rain);
            SharedPreferenceUtil.getInstance().putInt("阵雨", R.mipmap.type_one_thunderstorm);
            SharedPreferenceUtil.getInstance().putInt("雷阵雨", R.mipmap.type_one_thunder_rain);
            SharedPreferenceUtil.getInstance().putInt("霾", R.mipmap.type_one_fog);
            SharedPreferenceUtil.getInstance().putInt("雾", R.mipmap.type_one_fog);
        } else {
            SharedPreferenceUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferenceUtil.getInstance().putInt("晴", R.mipmap.type_two_sunny);
            SharedPreferenceUtil.getInstance().putInt("阴", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("多云", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("少云", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("晴间多云", R.mipmap.type_two_cloudytosunny);
            SharedPreferenceUtil.getInstance().putInt("小雨", R.mipmap.type_two_light_rain);
            SharedPreferenceUtil.getInstance().putInt("中雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("大雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("阵雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("雷阵雨", R.mipmap.type_two_thunderstorm);
            SharedPreferenceUtil.getInstance().putInt("霾", R.mipmap.type_two_haze);
            SharedPreferenceUtil.getInstance().putInt("雾", R.mipmap.type_two_fog);
            SharedPreferenceUtil.getInstance().putInt("雨夹雪", R.mipmap.type_two_snowrain);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();

/*
        OrmLite.getInstance().close();
*/
    }

    /**
     * 监听定位完成广播
     */
    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (getString(R.string.action_locate_succeed).equals(intent.getAction())) {
                mCityText.setText(PrefUtil.getCityShort());
                showLocatedCityDialog(true, intent.getBooleanExtra(getString(R.string.param_is_upper_city), false));
            }
        }
    }

    private void showLocatedCityDialog(final boolean refresh, final boolean upperCity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
        builder.setTitle(getString(R.string.location_default));
        final View view = getLayoutInflater().inflate(R.layout.layout_location_dialog, null);
        final TextView msgText = (TextView) view.findViewById(R.id.dialog_loc_msg_text);
        final TextView manualText = (TextView) view.findViewById(R.id.dialog_loc_input_manually_text);
        final View inputLayout = view.findViewById(R.id.dialog_loc_input_layout);
        final EditText inputEdit = (EditText) view.findViewById(R.id.dialog_loc_edit);

        if (upperCity) {
            msgText.setText(getString(R.string.hint_query_by_upper_city, PrefUtil.getCity(),
                    PrefUtil.getUpperCity()));
        } else {
            msgText.setText(getString(R.string.hint_located_city, PrefUtil.getCity()));
        }
        String hint = getString(R.string.hint_input_city_manually);
        SpannableStringBuilder span = new SpannableStringBuilder(hint);
        span.setSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(this, Colorful.getThemeDelegate().getAccentColor().getColorRes())),
                hint.length() - 4, hint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        manualText.setText(span);
        manualText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputLayout.getVisibility() != View.VISIBLE)
                    inputLayout.setVisibility(View.VISIBLE);
            }
        });

        builder.setView(view);
        builder.setNegativeButton(getString(R.string.locate_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PrefUtil.clearCity();
                LocationService.start(WeatherActivity.this);
            }
        });
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (inputLayout.getVisibility() == View.VISIBLE) {
                    if (inputEdit.getText().toString().trim().isEmpty())
                    {
                        Toast.makeText(WeatherActivity.this, getString(R.string.hint_distract_input_empty), Toast.LENGTH_SHORT).show();

                    } else {
                        PrefUtil.setInputtedCity(true);
                        PrefUtil.setCity(inputEdit.getText().toString().trim());
                        mCityText.setText(PrefUtil.getCityShort());
                    }
                    return;
                }

                if (upperCity) {
                    PrefUtil.setCity(PrefUtil.getUpperCity());
                    mCityText.setText(PrefUtil.getCityShort());
                }
            }
        });
        builder.show();
    }

}
