package com.bzu.yhd.pocketcampus.widget;

import com.bzu.yhd.pocketcampus.BuildConfig;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.base.Config;
import com.bzu.yhd.pocketcampus.widget.utils.PLog;
import com.bzu.yhd.pocketcampus.widget.utils.RxUtils;
import com.bzu.yhd.pocketcampus.widget.utils.SimpleSubscriber;
import com.bzu.yhd.pocketcampus.bottomnav.home.weather.domain.CityORM;
import com.litesuits.orm.LiteOrm;

import rx.Observable;

/**
 * Created by HugoXie on 16/7/24.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
public class OrmLite {

    static LiteOrm sLiteOrm;

    public static LiteOrm getInstance() {
        getOrmHolder();
        return sLiteOrm;
    }

    private static OrmLite getOrmHolder() {
        return OrmHolder.sInstance;
    }

    private OrmLite() {
        if (sLiteOrm == null) {
            sLiteOrm = LiteOrm.newSingleInstance(BaseApplication.getAppContext(), Config.ORM_NAME);

        }
        sLiteOrm.setDebugged(BuildConfig.DEBUG);
    }

    private static class OrmHolder {
        private static final OrmLite sInstance = new OrmLite();
    }

    public static <T> void OrmTest(Class<T> t) {
        Observable.from(getInstance().query(t))
            .compose(RxUtils.rxSchedulerHelper())
            .subscribe(new SimpleSubscriber<T>() {
                @Override
                public void onNext(T t) {
                    if (t instanceof CityORM) {
                        PLog.w(((CityORM) t).getName());
                    }
                }
            });
    }
}
