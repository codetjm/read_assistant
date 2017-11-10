package com.hk.read.ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hk.read.R;
import com.hk.read.base.BaseActivity;
import com.hk.read.ocr.imp.IOcrPresenter;
import com.hk.read.ocr.imp.IOcrView;


public class OCRActivity extends BaseActivity implements IOcrView, View.OnClickListener {

    private Button startCan;
    private Button wordExplain;
    private Button send;
    private TextView logView;

    private IOcrPresenter presenter;
    private String mPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        initView();
        presenter = new OcrPresenter(this);
    }

    private void initView() {
        startCan = (Button) findViewById(R.id.start_scan);
        wordExplain = (Button) findViewById(R.id.word_explain);
        send = (Button) findViewById(R.id.send);
        logView = (TextView) findViewById(R.id.out_log);
//        logView.setText("", TextView.BufferType.EDITABLE);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        startCan.setOnClickListener(this);
        wordExplain.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void updateLog(String msg) {
//        Editable text = (Editable) logView.getText();
//        text.append(msg);
//        logView.setText(text);

        logView.append(msg);
        int offset = logView.getLineCount() * logView.getLineHeight();
        if (offset > logView.getHeight()) {
            logView.scrollTo(0, offset - logView.getHeight());
        }
    }

    @Override
    public void updateBtnStatu(int statu) {
        wordExplain.setText("文字解析");
        wordExplain.setEnabled(true);
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
                if (TextUtils.isEmpty(mPath)) {
                    updateLog("还未拍照，请拍照后重试");
                    return;
                }
                wordExplain.setText("正在解析中...");
                wordExplain.setEnabled(false);
                presenter.handleResult(mPath);

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
                mPath = data.getStringExtra("filePath");
                updateLog("临时图片存放路径：" + mPath);

            }

//                presenter.handleResult();
        } else {
            updateLog("拍照失败，请重试");
        }
    }
}

