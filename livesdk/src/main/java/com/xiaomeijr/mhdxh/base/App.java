package com.xiaomeijr.mhdxh.base;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.apkfuns.logutils.LogLevel;
import com.apkfuns.logutils.LogUtils;
import com.umeng.socialize.Config;
import com.xiaomeijr.mhdxh.fakeserver.FakeServer;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import io.rong.imkit.RongIM;


public class App extends MultiDexApplication {
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
        RongIM.init(this);
        UMShareAPI.get(this);
        LiveKit.init(context, FakeServer.getAppKey());
        StreamingEnv.init(getApplicationContext());
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
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