package com.hk.read;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.Bugly;

/**
 * Created by changfeng on 2017/11/5.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "c823e70556", false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
}
