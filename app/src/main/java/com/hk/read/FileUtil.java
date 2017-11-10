package com.hk.read;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static com.hk.read.Constant.WORDS_DIRECTORY_NAME;

/**
 * Created by changfeng on 2017/11/11.
 */

public class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), System.currentTimeMillis()+".jpg");
        return file;
    }
    public static void saveFile(Context context,String fileName,String txt) {
        File directory = Environment.getRootDirectory();
        File myFile = new File(directory, WORDS_DIRECTORY_NAME+"/"+fileName+".txt");
        if (!directory.exists()) {
            Toast.makeText(context, "当前系统不具备SD卡目录", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            myFile.createNewFile();
            Toast.makeText(context, "文件创建完成", Toast.LENGTH_SHORT).show();
            FileOutputStream fos = new FileOutputStream(myFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(txt);
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            Toast.makeText(context, "文件已经写入完成", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
