package com.bzu.yhd.pocketcampus.bottomnav.home.service;


import com.bzu.yhd.pocketcampus.bottomnav.home.service.api.NetLocApi;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Api操作助手类
 * <p/>
 * Created by woxingxiao on 2017-02-10.
 */
public class ApiHelper {

    private static String API_KEY_JH;
    private static String APP_ID_YY;
    private static String API_KEY_YY;

    private static NetLocApi netLocApi;

    public static void init(String apiKeyJH, String appId, String apiKeyYY) {
        API_KEY_JH = apiKeyJH;
        APP_ID_YY = appId;
        API_KEY_YY = apiKeyYY;
    }


    private static NetLocApi getNetLocApi() {
        if (netLocApi == null) {
            netLocApi = new ApiClient().createApi("http://gc.ditu.aliyun.com/", NetLocApi.class);
        }

        return netLocApi;
    }

    /**
     * 经纬度转地址
     */
    public static Observable<NetLocResult> loadNetLoc(String latLng) {
        return getNetLocApi().getNetLoc(latLng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



    public static void releaseNetLocApi() {
        netLocApi = null;
    }

}
