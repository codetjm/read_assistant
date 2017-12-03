package com.hk.read;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.hk.read.app.MyApplication;
import com.hk.read.base.BaseActivity;
import com.hk.read.ocr.OCRActivity;
import com.hk.read.translate.ClipBoardActivity;
import com.hk.read.utils.PemissionUitls;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private View scan;
    private View translate;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        scan = findViewById(R.id.scan);
        translate = findViewById(R.id.translate);
        mTextView = (TextView) findViewById(R.id.version);
        mTextView.append(getAppVersionName(this));
        scan.setOnClickListener(this);
        translate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v == translate) {
            intent = new Intent(this, ClipBoardActivity.class);
        } else if (v == scan) {
            intent = new Intent(this, OCRActivity.class);
        }

        startActivity(intent);

    }

    private void init() {
        PemissionUitls.verifyStoragePermissions(this);


        //        文字识别初始化
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
                    @Override
                    public void onResult(AccessToken result) {
                        // 调用成功，返回AccessToken对象
                        String baiduToken = result.getAccessToken();
                        ((MyApplication) getApplication()).setToken(baiduToken);
                        Log.e("====", "baiduToken:" + baiduToken);
//                        Toast.makeText(getApplicationContext(), "文字识别授权成功:" + token, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(OCRError error) {
                        // 调用失败，返回OCRError子类SDKError对象
                        Log.e("====", "请求token失败：" + error.getMessage());
//                        Toast.makeText(getApplicationContext(), "文字识别过期", Toast.LENGTH_LONG).show();
                    }
                }, getApplicationContext(), "wYVcBLA67kZ1AytTZVGsSfa4", "vwdhRzo9DBuPlrqIyAYjHAtBLuSlvDZ7");
            }
        });

        thread.start();

    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versioncode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        Log.i("====","versionName:"+versionName);
        return versionName;
    }
}
