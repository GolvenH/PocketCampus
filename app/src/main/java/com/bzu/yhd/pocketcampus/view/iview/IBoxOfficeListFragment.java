package com.bzu.yhd.pocketcampus.view.iview;

import android.content.Context;

import com.bzu.yhd.pocketcampus.model.BoxOfficeModel;

import java.util.List;

/**
 * <p/>
 * Created by woxingxiao on 2017-03-07.
 */
public interface IBoxOfficeListFragment {

    Context getContext();

    void onDataReady(List<BoxOfficeModel> modelList);

    void onDataError(int code, String msg);
}
