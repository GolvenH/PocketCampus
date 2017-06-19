package com.bzu.yhd.pocketcampus.base;

/**
 * Created by HugoXie on 16/5/23.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info: 常量类
 */
public class Config {

    public static final String WEATHER_CACHE = "weatherData";
/**/
    public static final String KEY = "db952db2823c40608555af938025c71c";// 和风天气 key

    public static final String ORM_NAME = "cities.db";


    public static String Bmob_APPID = "1fbf365963748879b31dcef9fdbe38f8";// bmob sdk的APPID

    public static final int REQUESTCODE_ADD = 1;//添加失物/招领

    /**
     * Bmob应用key
     */
    public static final String DEFAULT_APPKEY="1fbf365963748879b31dcef9fdbe38f8";
    //是否是debug模式
    public static final boolean DEBUG=true;
    //好友请求：未读-未添加->接收到别人发给我的好友添加请求，初始状态
    public static final int STATUS_VERIFY_NONE=0;
    //好友请求：已读-未添加->点击查看了新朋友，则都变成已读状态
    public static final int STATUS_VERIFY_READED=2;
    //好友请求：已添加
    public static final int STATUS_VERIFIED=1;
    //好友请求：拒绝
    public static final int STATUS_VERIFY_REFUSE=3;
    //好友请求：我发出的好友请求-暂未存储到本地数据库中
    public static final int STATUS_VERIFY_ME_SEND=4;

}
