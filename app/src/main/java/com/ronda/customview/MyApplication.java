package com.ronda.customview;

import android.app.Application;

import com.socks.library.KLog;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/17
 * Version: v1.0
 */

public class MyApplication extends Application {

    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;

        init();
    }

    private void init() {
        KLog.init(true, "Liu");
    }

    public static MyApplication getInstance(){
        return myApplication;
    }
}
