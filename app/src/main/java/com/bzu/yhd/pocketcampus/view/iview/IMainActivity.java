package com.bzu.yhd.pocketcampus.view.iview;

/**
 * <p/>
 * Created by woxingxiao on 2017-04-01.
 */

public interface IMainActivity {

    void onFragmentInitOK(int fragmentId);

    void onFragmentRefreshRequest(int fragmentId);

    void onFragmentRefreshDataReady(int fragmentId);

    void onDataError(int code, String msg);
}
