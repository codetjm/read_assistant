package com.hk.read.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static com.hk.read.Constant.IMG_TEMP;
import static com.hk.read.Constant.WORDS_DIRECTORY_NAME;
import static com.igexin.sdk.GTServiceManager.context;

/**
 * Created by changfeng on 2017/11/11.
 */

public class FileUtil {
    private static String TAG = "FileUtil";

    public static File getSaveFile(Context context, int page) {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            Toast.makeText(context, "当前系统不具备SD卡目录", Toast.LENGTH_SHORT).show();
            return null;
        }
        File parentFile = new File(directory, IMG_TEMP);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File file = new File(parentFile, "第" + page + "页.jpg");
        return file;
    }

    public static File saveFile(String fileName, String txt,OnFileOprateListener listener) {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            listener.sendMesage(0,"当前系统不具备SD卡目录");
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
            listener.sendMesage(0,"\n文件创建完成");
            FileOutputStream fos = new FileOutputStream(myFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "gb2312");
            osw.write(txt);
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            Log.i(TAG, myFile.getAbsolutePath() + "");
            listener.sendMesage(0,"\n写入成功，"+myFile.getAbsolutePath());
            return myFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myFile;
    }

    public static boolean claarStore() {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            return true;
        }
        File parentFile = new File(directory, WORDS_DIRECTORY_NAME);
        if (!parentFile.exists()) {
            return true;
        }

        boolean delete = parentFile.delete();

        return delete;
    }

    public static boolean mergeFile(int start, int end,OnFileOprateListener listener) {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            listener.sendMesage(0,"\n当前系统不具备SD卡目录");
            Toast.makeText(context, "当前系统不具备SD卡目录", Toast.LENGTH_SHORT).show();
            return false;
        }
        File parentFile = new File(directory, WORDS_DIRECTORY_NAME);
        if (!parentFile.exists()) {
            listener.sendMesage(0,"\nword_scan文件夹不存在");
            return false;
        }

        File[] files = parentFile.listFiles();
        StringBuilder builder = new StringBuilder();
        for (int i = start; i <= end; i++) {
            boolean isHavePage  = false;
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    String replace = name.replace("第", "");
                    String replace1 = replace.replace("页", "");
                    String replace2 = replace1.replace(".txt", "");
                    int page = 0;
                    try {
                     page = Integer.parseInt(replace2);
                    }catch (Exception e){

                    }
                    if (page == i) {
                        isHavePage = true;
                        Log.i(TAG, "开始合并第" + i + "页");
                        listener.sendMesage(0,"\n开始合并第" + i + "页");
                        String string = getTxtFromFile(file);
                        if (android.text.TextUtils.isEmpty(string)){
                            listener.sendMesage(0,"\n读取第"+i+"页失败");
                            Log.i(TAG,"读取第"+i+"页失败");
                            return false;
                        }
                        builder.append(string);
                    }

                }
            }
            if (!isHavePage){
                listener.sendMesage(0,"\n没有第"+i+"页，请重新扫描并解析该页。");
                return false;
            }

        }
        saveFile("第"+start+"—"+end+"页.txt",builder.toString(),listener);

        return true;
    }

    private static String getTxtFromFile(File file) {
        try {

            FileInputStream in = new FileInputStream(file);

            // size  为字串的长度 ，这里一次性读完

            int size = in.available();

            byte[] buffer = new byte[size];

            in.read(buffer);

            in.close();

            return new String(buffer, "utf-8");

        } catch (IOException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();
            return "";


        }
    }
}
