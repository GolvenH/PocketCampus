package com.bzu.yhd.pocketcampus.presenter.impl;

import com.bzu.yhd.pocketcampus.presenter.IBoxOfficeFragmentPresenter;
import com.bzu.yhd.pocketcampus.server.ApiSubscriber;
import com.bzu.yhd.pocketcampus.view.iview.IBoxOfficeListFragment;
import com.bzu.yhd.pocketcampus.model.BoxOfficeModel;
import com.bzu.yhd.pocketcampus.server.ApiException;
import com.bzu.yhd.pocketcampus.server.ApiHelper;

import java.util.List;

/**
 * <p/>
 * Created by woxingxiao on 2017-03-07.
 */

public class BoxOfficeFragmentPresenterImpl implements IBoxOfficeFragmentPresenter {

    private IBoxOfficeListFragment mFragment;
    private ApiSubscriber<List<BoxOfficeModel>> mSubscriber;
    private int mErrCode = -1;

    @Override
    public void register(IBoxOfficeListFragment fragment) {
        this.mFragment = fragment;
    }

    @Override
    public void loadData(int dataType) {
        mSubscriber = new ApiSubscriber<List<BoxOfficeModel>>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onNext(List<BoxOfficeModel> movieModels) {
                if (mFragment != null)
                    mFragment.onDataReady(movieModels);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException)
                    mErrCode = ((ApiException) e).getCode();
                super.onError(e);
            }

            @Override
            protected void onError(String msg) {
                if (mFragment != null)
                    mFragment.onDataError(mErrCode, msg);
            }

            @Override
            public void onFinally() {
                super.onFinally();
            }
        };

        ApiHelper.loadBoxOffice(dataType)
                .subscribe(mSubscriber);
    }

    @Override
    public void unregister() {
        if (mSubscriber != null && mSubscriber.isUnsubscribed())
            mSubscriber.unsubscribe();
        mFragment = null;
    }
}
