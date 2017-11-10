package com.hk.read.ocr.imp;

import android.app.Activity;

/**
 * Created by changfeng on 2017/11/10.
 */

public interface IOcrView {
    //未解析，解析中，解析后，为发送，发送前，发送后
    void updateLog(String msg);
    void updateBtnStatu(int statu);
    Activity getContext();
}
