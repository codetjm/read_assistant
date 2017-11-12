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
import com.hk.read.ocr.entity.OcrImg;
import com.hk.read.ocr.imp.IOcrPresenter;
import com.hk.read.ocr.imp.IOcrView;
import com.hk.read.utils.FileUtil;
import com.hk.read.utils.OnFileOprateListener;
import com.hk.read.utils.TextUtils;

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
    public void startWordRecgnize(int page) {
        // 生成intent对象
        Intent intent = new Intent(mOcrView.getContext(), CameraActivity.class);
        try {
            intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(mOcrView.getContext(), page).getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOcrView.getContext().startActivityForResult(intent, START_CAMERA_REQUEST_CODE);
    }

    @Override
    public void handleResult(final OcrImg ocrImg) {
        final GeneralParams param = new GeneralParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(ocrImg.filePath));
// 调用通用文字识别服务
        OCR.getInstance().recognizeGeneral(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                // 调用成功，返回GeneralResult对象
                handlerText(result, ocrImg);
            }


            @Override
            public void onError(final OCRError error) {
                // 调用失败，返回OCRError对象
                mOcrView.updateLog(error.getMessage() + "\n");
                mOcrView.updateBtnStatu(0);
            }
        });
    }

    @Override
    public void sendTxt(String filePath) {
      FileUtil.shareFile(mOcrView.getContext(), filePath, new OnFileOprateListener() {
          @Override
          public void sendMesage(int code, String msg) {
              mOcrView.updateLog(msg);
          }
      });
    }

    @Override
    public boolean mergePage(int start, int end) {
        if (start >= end && start != 0) {
            mOcrView.updateLog("输入的起始页码应小于结束页码");
            return false;
        }

        boolean b = FileUtil.mergeFile(start, end, new OnFileOprateListener() {
            @Override
            public void sendMesage(int code, String msg) {
                mOcrView.updateLog(msg);
            }
        });
        return b;
    }

    @Override
    public boolean clearWord() {
        return FileUtil.claarStore();
    }


    private void handlerText(GeneralResult result, OcrImg ocrImg) {
        StringBuilder sb = new StringBuilder();
        String preTemp = "";
        for (WordSimple wordSimple : result.getWordList()) {
            // wordSimple不包含位置信息
            boolean isParagraphLastLine = TextUtils.isParagraphLastLine(preTemp);
            if (isParagraphLastLine) {
                sb.append("\n");
            }
            String words = wordSimple.getWords();
//            boolean isLast = TextUtils.isParagraphLastLine(words);
//            if (isLast) {
//                if (words.length() != 0) {
//                    char c = words.charAt(words.length() - 1);
//                    if (!(".".equals(c) || "。".equals(c) || "！".equals(c) || "!".equals(c))) {
//                        if (!TextUtils.isParagraphLastLine(preTemp)) {
//                            words += "。";
//                        }
//                    }
//                }
//            }
            String formatLine = TextUtils.formatLine(preTemp, words);
            sb.append(formatLine);

            preTemp = formatLine;
        }

        mOcrView.updateLog("\n扫描到的文字如下：\n" + sb.toString());
        String path = ocrImg.filePath;
        String substring = path.substring(path.lastIndexOf("/") + 1);
        String fileName = substring.substring(0, substring.lastIndexOf("."));
        //替换标点符号
        File file = FileUtil.saveFile(fileName + ".txt", sb.toString(), new OnFileOprateListener() {
            @Override
            public void sendMesage(int code, String msg) {
                mOcrView.updateLog(msg);
            }
        });
        Log.i("====", sb.toString());
        mOcrView.updateBtnStatu(0);
    }


}
