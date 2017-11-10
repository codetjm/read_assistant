package com.hk.read.ocr.imp;

/**
 * Created by changfeng on 2017/11/10.
 */

public interface IOcrPresenter {
    void startWordRecgnize();
    void handleResult(String filePath);
}
