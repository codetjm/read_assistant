package com.hk.read.app;

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
//        bugly初始化，版本神级
        Bugly.init(getApplicationContext(), "c823e70556", false);
        //创建扫描结果保存路径
//        FileUtil.createFile(new File(Constant.WORDS_DIRECTORY_NAME));
//        FileUtil.createFile(new File(Constant.IMG_TEMP));

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }


}
