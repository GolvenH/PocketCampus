package com.bzu.yhd.pocketcampus.bottomnav.home.service.api;


import com.bzu.yhd.pocketcampus.bottomnav.home.service.NetLocResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 网络转换定位接口
 * <p/>
 * Created by woxingxiao on 2017-03-01.
 */
public interface NetLocApi {

    @GET("regeocoding?type=010")
    Observable<NetLocResult> getNetLoc(@Query("l") String latLng);
}
