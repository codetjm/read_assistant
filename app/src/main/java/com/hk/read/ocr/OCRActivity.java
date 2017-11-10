package com.hk.read.ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hk.read.R;
import com.hk.read.base.BaseActivity;
import com.hk.read.ocr.imp.IOcrPresenter;
import com.hk.read.ocr.imp.IOcrView;


public class OCRActivity extends BaseActivity implements IOcrView, View.OnClickListener {

    private View startCan;
    private View wordExplain;
    private View send;
    private TextView logView;

    private IOcrPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        initView();
        presenter = new OcrPresenter(this);
    }

    private void initView() {
        startCan = findViewById(R.id.start_scan);
        wordExplain = findViewById(R.id.word_explain);
        send = findViewById(R.id.send);
        logView = (TextView) findViewById(R.id.out_log);
        startCan.setOnClickListener(this);
        wordExplain.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void updateLog(String msg) {
        logView.setText(msg);
    }

    @Override
    public void updateBtnStatu(int statu) {

    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_scan:
                //启动扫描
                presenter.startWordRecgnize();
                break;
            case R.id.word_explain:
                //解析文字
//                presenter.handleResult();
                break;
            case R.id.send:
                //发送文字


                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OcrPresenter.START_CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //处理成功

                updateLog("临时图片存放路径：" +data.getStringExtra("filePath"));

            }

//                presenter.handleResult();
        } else {
            updateLog("拍照失败，请重试");
        }
    }
}

