package com.hk.read.ocr.imp;

import com.hk.read.ocr.imp.entity.OcrImg;

/**
 * Created by changfeng on 2017/11/10.
 */

public interface IOcrPresenter {
    //扫描第几页
    void startWordRecgnize(int page);
    void handleResult(OcrImg img);
}
