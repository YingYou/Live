package com.mer.live.base;

import android.app.Application;
import android.content.Context;

import com.mer.live.fakeserver.FakeServer;
import com.qiniu.pili.droid.streaming.StreamingEnv;


public class App extends Application {
    private static App instance;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        instance = this;
        LiveKit.init(context, FakeServer.getAppKey());
        StreamingEnv.init(getApplicationContext());
    }

    public static Context getContext() {
        return context;
    }
    public static App getInstance() {
        return instance;
    }
}