package com.hk.read.ocr;

import android.content.Intent;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.hk.read.ocr.imp.IOcrPresenter;
import com.hk.read.ocr.imp.IOcrView;

import java.io.File;

/**
 * Created by changfeng on 2017/11/10.
 */

public class OcrPresenter implements IOcrPresenter {
    public static final int START_CAMERA_REQUEST_CODE = 10001;

    private IOcrView mOcrView;

    public OcrPresenter(IOcrView ocrView) {
        mOcrView = ocrView;
    }

    @Override
    public void startWordRecgnize() {
        // 生成intent对象
        Intent intent = new Intent(mOcrView.getContext(), CameraActivity.class);
        try {
            intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, com.hk.read.FileUtil.getSaveFile(mOcrView.getContext()).getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOcrView.getContext().startActivityForResult(intent, START_CAMERA_REQUEST_CODE);
    }

    @Override
    public void handleResult(String filePath) {
        GeneralParams param = new GeneralParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(filePath));
// 调用通用文字识别服务
        OCR.getInstance().recognizeGeneral(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                // 调用成功，返回GeneralResult对象
                StringBuilder sb = new StringBuilder();
                for (WordSimple wordSimple : result.getWordList()) {
                    // wordSimple不包含位置信息
                    sb.append(wordSimple.getWords());
                    sb.append("\n");
                }
                mOcrView.updateLog("\n扫描到的文字如下：\n" + sb.toString());
                long l = System.currentTimeMillis();
//                FileUtil.saveFile(mOcrView.getContext(),l+"",sb.toString());
                Log.i("====",sb.toString());
                mOcrView.updateLog("解析后的文字写入成功：\n" );
                mOcrView.updateLog("写入路径：\n" + l+".txt");
                mOcrView.updateBtnStatu(0);
            }

            @Override
            public void onError(final OCRError error) {
                // 调用失败，返回OCRError对象
                mOcrView.updateLog(error.getMessage() + "\n");
                mOcrView.updateBtnStatu(0);
            }
        });
    }
}
