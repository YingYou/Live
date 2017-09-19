package com.xiaomeijr.mhdxh.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.apkfuns.logutils.LogUtils;

import cn.finalteam.okhttpfinal.HttpCycleContext;

public abstract class BaseActivity extends FragmentActivity implements HttpCycleContext{

    protected final String HTTP_TASK_KEY = "HttpTaskKey_" + hashCode();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去标题栏
        Window window = this.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(getLayoutResID());

        //打印打开的activity的类名和所在的包
        LogUtils.i(getClass().getName());
        initUI();
        initData();
    }

    /**
     * getContentView ID
     *
     * @return
     */
    protected abstract int getLayoutResID();
    /**
     * 初始化UI
     */
    protected abstract void initUI();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    @Override
    public String getHttpTaskKey() {
        return HTTP_TASK_KEY;
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        super.onStart();
    }
}
