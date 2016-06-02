package com.test.huanxin.bannertest.app;

import android.app.Application;

import com.test.huanxin.bannertest.BuildConfig;

import org.xutils.x;

/**
 * Created by Administrator on 2016/6/1 0001.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
