package com.bzu.yhd.pocketcampus.bottomnav.home.weather.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseFragment;
import com.bzu.yhd.pocketcampus.bottomnav.home.weather.adapter.WeatherAdapter;
import com.bzu.yhd.pocketcampus.bottomnav.home.weather.domain.ChangeCityEvent;
import com.bzu.yhd.pocketcampus.bottomnav.home.weather.domain.Weather;
import com.bzu.yhd.pocketcampus.widget.RetrofitSingleton;
import com.bzu.yhd.pocketcampus.widget.RxBus;
import com.bzu.yhd.pocketcampus.widget.utils.PLog;
import com.bzu.yhd.pocketcampus.widget.utils.PrefUtil;
import com.bzu.yhd.pocketcampus.widget.utils.SharedPreferenceUtil;
import com.bzu.yhd.pocketcampus.widget.utils.SimpleSubscriber;
import com.bzu.yhd.pocketcampus.widget.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by HugoXie on 16/7/9.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
public class MainFragment extends BaseFragment  {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiprefresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.iv_erro)
    ImageView mIvError;

    private static Weather mWeather = new Weather();
    private WeatherAdapter mAdapter;
    private View view;
    private Unbinder mUnbinder;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_weather, container, false);
            mUnbinder = ButterKnife.bind(this, view);
        }
        mIsCreateView = true;
        PLog.d("onCreateView");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        load();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PLog.d("onCreate");
        RxBus.getDefault().toObservable(ChangeCityEvent.class).observeOn(AndroidSchedulers.mainThread()).subscribe(
            new SimpleSubscriber<ChangeCityEvent>() {
                @Override
                public void onNext(ChangeCityEvent changeCityEvent) {
                    if (mRefreshLayout != null) {
                        mRefreshLayout.setRefreshing(true);
                    }
                    load();
                    PLog.d("MainRxBus");
                }
            });
    }

    private void initView() {
        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
            mRefreshLayout.setOnRefreshListener(
                () -> mRefreshLayout.postDelayed(this::load, 1000));
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new WeatherAdapter(mWeather);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void load() {
        fetchDataByNetWork()
            .doOnRequest(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    mRefreshLayout.setRefreshing(true);
                }
            })
            .doOnError(throwable -> {
                mIvError.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                SharedPreferenceUtil.getInstance().setCityName("滨州");
                safeSetTitle("找不到城市啦");
            })
            .doOnNext(weather -> {
                mIvError.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            })
            .doOnTerminate(() -> {
                mRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
            }).subscribe(new Subscriber<Weather>() {
            @Override
            public void onCompleted() {
                ToastUtil.showShort(getString(R.string.complete));
            }

            @Override
            public void onError(Throwable e) {
                PLog.e(e.toString());
                RetrofitSingleton.disposeFailureInfo(e);
            }

            @Override
            public void onNext(Weather weather) {
                mWeather.status = weather.status;
                mWeather.aqi = weather.aqi;
                mWeather.basic = weather.basic;
                mWeather.suggestion = weather.suggestion;
                mWeather.now = weather.now;
                mWeather.dailyForecast = weather.dailyForecast;
                mWeather.hourlyForecast = weather.hourlyForecast;
                safeSetTitle(weather.basic.city);
                mAdapter.notifyDataSetChanged();
                normalStyleNotification(weather);
            }
        });
    }

    /**
     * 从网络获取
     */
    private Observable<Weather> fetchDataByNetWork() {
        String cityName = PrefUtil.getCityShort();
//        String cityName = SharedPreferenceUtil.getInstance().getCityName();
        return RetrofitSingleton.getInstance()
            .fetchWeather(cityName)
            .compose(this.bindToLifecycle());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 通知栏天气
     */
    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
            PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(getActivity());
        Notification notification = builder.setContentIntent(pendingIntent)
            .setContentTitle(weather.basic.city)
            .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
            // 这里部分 ROM 无法成功
            .setSmallIcon(SharedPreferenceUtil.getInstance().getInt(weather.now.cond.txt, R.mipmap.none))
            .build();
        notification.flags = SharedPreferenceUtil.getInstance().getNotificationModel();
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}
