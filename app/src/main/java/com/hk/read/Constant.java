package com.hk.read;

import android.os.Environment;

/**
 * Created by changfeng on 2017/11/11.
 */

public class Constant {
    //扫描结果存放地
    public static final String WORDS_DIRECTORY_NAME = "/word_scan";
    //图片临时存放地
    public static final String IMG_TEMP = Environment.getDataDirectory().getAbsolutePath()+"/word_scan/temp";
}
