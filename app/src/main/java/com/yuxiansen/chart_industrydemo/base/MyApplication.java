package com.yuxiansen.chart_industrydemo.base;

import android.app.Application;
import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * author  : Mr.Yu
 * Time    : 2018.1.2
 * Email   : yudehai0204@163.com
 * desc    :
 */
public class MyApplication  extends Application {

    public static Context applicationContext;
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        applicationContext = getApplicationContext();
    }
}
