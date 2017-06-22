package com.mer.live.base;

import android.app.Application;
import android.content.Context;

import com.apkfuns.logutils.LogLevel;
import com.apkfuns.logutils.LogUtils;
import com.mer.live.fakeserver.FakeServer;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;


public class App extends Application {
    private static App instance;
    private static Context context;
    {

        PlatformConfig.setWeixin("wxfb9eddc0e08f59be", "ee7cab9c84e6389bc8a40a0213528cdd");
        PlatformConfig.setQQZone("1105711808", "9yhZvoaduBx7a1v5");
        PlatformConfig.setSinaWeibo("255918506", "7692277c47630283302d0b5b0af7d1f0", "http://sns.whalecloud.com");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        instance = this;
        initLogs();
        UMShareAPI.get(this);
        LiveKit.init(context, FakeServer.getAppKey());
        StreamingEnv.init(getApplicationContext());
    }

    public static Context getContext() {
        return context;
    }
    public static App getInstance() {
        return instance;
    }

    private void initLogs() {
        LogUtils.getLogConfig()
                .configAllowLog(true)
                .configTagPrefix("MyApp")
                .configShowBorders(true)
                .configLevel(LogLevel.TYPE_VERBOSE);
    }
}