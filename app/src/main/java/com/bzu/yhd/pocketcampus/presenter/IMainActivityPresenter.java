package com.bzu.yhd.pocketcampus.presenter;

import com.bzu.yhd.pocketcampus.model.MovieModel;

import java.util.List;

/**
 * <p/>
 * Created by woxingxiao on 2017-04-01.
 */
public interface IMainActivityPresenter {

    void onFragmentInitOK(int fragmentId);

    void onFragmentRefreshCheckData(int fragmentId);

    void loadMovieData();

    List<MovieModel> getMovieModels(int fragmentId);

    void unregister();
}
