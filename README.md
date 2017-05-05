# PocketCampus
PocketCampus 毕业设计Android掌上校园APP

参考 https://github.com/woxingxiao/GracefulMovies 实现定时夜间模式 和更换主题的功能

参考 https://github.com/aurelhubert/ahbottomnavigation 实现底部导航的效果

实习上班ing....

未完待续....

权限说明

```xml
	<!--用于进行网络定位-->
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	<!--用于访问GPS定位-->
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
	<!--获取运营商信息，用于支持提供运营商信息相关的接口-->
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	<!--这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
	<!--用于访问网络，网络定位需要上网-->
	<uses-permission android:name="android.permission.INTERNET"/>
	<!--用于读取手机当前的状态-->
	<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
	<!--写入扩展存储，向扩展卡写入数据，用于写入缓存定位数据-->
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

