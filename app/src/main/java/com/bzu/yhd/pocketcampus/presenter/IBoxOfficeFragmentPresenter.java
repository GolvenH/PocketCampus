package com.bzu.yhd.pocketcampus.presenter;

import com.bzu.yhd.pocketcampus.view.iview.IBoxOfficeListFragment;

/**
 * <p/>
 * Created by woxingxiao on 2017-03-07.
 */
public interface IBoxOfficeFragmentPresenter {

    void register(IBoxOfficeListFragment fragment);

    void loadData(int dataType);

    void unregister();
}
