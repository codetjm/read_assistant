package com.hk.read.base;

import android.app.Activity;
import android.os.Bundle;

import com.hk.read.service.GeTuiPushService;
import com.igexin.sdk.PushManager;

/**
 * Created by changfeng on 2017/11/5.
 */

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushManager.getInstance().initialize(this.getApplicationContext(), GeTuiPushService.class);
    }


}
