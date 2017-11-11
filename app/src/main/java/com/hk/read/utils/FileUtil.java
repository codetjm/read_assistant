package com.hk.read.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static com.hk.read.Constant.IMG_TEMP;
import static com.hk.read.Constant.WORDS_DIRECTORY_NAME;

/**
 * Created by changfeng on 2017/11/11.
 */

public class FileUtil {
    private static String TAG = "FileUtil";

    public static File getSaveFile(Context context,int page) {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            Toast.makeText(context, "当前系统不具备SD卡目录", Toast.LENGTH_SHORT).show();
            return null;
        }
        File parentFile = new File(directory, IMG_TEMP);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File file = new File(parentFile, "第"+page + "页.jpg");
        return file;
    }

    public static File saveFile(Context context, String fileName, String txt) {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            Toast.makeText(context, "当前系统不具备SD卡目录", Toast.LENGTH_SHORT).show();
            return null;
        }
        File parentFile = new File(directory, WORDS_DIRECTORY_NAME);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File myFile = new File(parentFile, fileName);
        try {
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            Log.i(TAG, "文件创建完成");
            FileOutputStream fos = new FileOutputStream(myFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(txt);
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            Log.i(TAG, myFile.getAbsolutePath() + "");
            return myFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myFile;
    }

}
