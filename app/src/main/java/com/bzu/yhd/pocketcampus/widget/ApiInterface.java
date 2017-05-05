package com.bzu.yhd.pocketcampus.widget;

import com.bzu.yhd.pocketcampus.bottomnav.home.weather.domain.WeatherAPI;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiInterface {

    String HOST = "https://api.heweather.com/x3/";

    @GET("weather")
    Observable<WeatherAPI> mWeatherAPI(@Query("city") String city, @Query("key") String key);

}
