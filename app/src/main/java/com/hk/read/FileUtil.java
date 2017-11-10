package com.hk.read;

import android.content.Context;

import java.io.File;

/**
 * Created by changfeng on 2017/11/11.
 */

public class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), System.currentTimeMillis()+".jpg");
        return file;
    }
}
