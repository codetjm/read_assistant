package com.hk.read.ocr.imp;

import android.app.Activity;

/**
 * Created by changfeng on 2017/11/10.
 */

public interface IOcrView {
    void updateLog(String msg);
    void updateBtnStatu(int statu);
    Activity getContext();
}
