package com.hk.read.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public static File getSaveTxtFile(Context context, int page) {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            Toast.makeText(context, "当前系统不具备SD卡目录", Toast.LENGTH_SHORT).show();
            return null;
        }
        File parentFile = new File(directory, WORDS_DIRECTORY_NAME);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File file = new File(parentFile, "第" + page + "页.txt");
        return file;
    }
    public static List<String> getAllSaveTxtFile(Context context) {
        ArrayList<String> filePahts = new ArrayList<>();
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            Toast.makeText(context, "当前系统不具备SD卡目录", Toast.LENGTH_SHORT).show();
            return filePahts;
        }
        File parentFile = new File(directory, WORDS_DIRECTORY_NAME);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
            return filePahts;
        }
        for (File file : parentFile.listFiles()) {
            if (file.isFile()){
                filePahts.add(file.getAbsolutePath());
            }
        }
        return filePahts;
    }
    public static File saveFile(String fileName, String txt, OnFileOprateListener listener) {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            listener.sendMesage(0, "当前系统不具备SD卡目录");
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
            listener.sendMesage(0, "\n文件创建完成");
            FileOutputStream fos = new FileOutputStream(myFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "gb2312");
            osw.write(TextUtils.replacePunctuation(txt));
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            Log.i(TAG, myFile.getAbsolutePath() + "");
            listener.sendMesage(0, "\n写入成功，" + myFile.getAbsolutePath());
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
        File[] files = parentFile.listFiles();
        for (File file : files) {
            boolean delete = file.delete();
            if (!delete) {
                return delete;
            }
        }

        return true;
    }

    public static boolean mergeFile(int start, int end, OnFileOprateListener listener) {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            listener.sendMesage(0, "\n当前系统不具备SD卡目录");
            Toast.makeText(context, "当前系统不具备SD卡目录", Toast.LENGTH_SHORT).show();
            return false;
        }
        File parentFile = new File(directory, WORDS_DIRECTORY_NAME);
        if (!parentFile.exists()) {
            listener.sendMesage(0, "\nword_scan文件夹不存在");
            return false;
        }

        File[] files = parentFile.listFiles();
        StringBuilder builder = new StringBuilder();
        for (int i = start; i <= end; i++) {
            boolean isHavePage = false;
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    String replace = name.replace("第", "");
                    String replace1 = replace.replace("页", "");
                    String replace2 = replace1.replace(".txt", "");
                    int page = 0;
                    try {
                        page = Integer.parseInt(replace2);
                    } catch (Exception e) {

                    }
                    if (page == i) {
                        isHavePage = true;
                        Log.i(TAG, "开始合并第" + i + "页");
                        listener.sendMesage(0, "\n开始合并第" + i + "页");
                        String string = getTxtFromFile(file);
                        if (android.text.TextUtils.isEmpty(string)) {
                            listener.sendMesage(0, "\n读取第" + i + "页失败");
                            Log.i(TAG, "读取第" + i + "页失败");
                            return false;
                        }
                        builder.append(string);
                    }

                }
            }
            if (!isHavePage) {
                listener.sendMesage(0, "\n没有第" + i + "页，请重新扫描并解析该页。");
                return false;
            }

        }
        saveFile("第" + start + "—" + end + "页.txt", builder.toString(), listener);

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

            return new String(buffer, "gb2312");

        } catch (IOException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();
            return "";


        }
    }

    public static Uri getFileUri(Context context, File file) {
        Uri uri;
        // 低版本直接用 Uri.fromFile
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            //  使用 FileProvider 会在某些 app 下不支持（在使用FileProvider 方式情况下QQ不能支持图片、视频分享，微信不支持视频分享）
            uri = FileProvider.getUriForFile(context,
                    "com.hk.read.fileprovider",
                    file);

            ContentResolver cR = context.getContentResolver();
            if (uri != null && !android.text.TextUtils.isEmpty(uri.toString())) {
                String fileType = cR.getType(uri);
// 使用 MediaStore 的 content:// 而不是自己 FileProvider 提供的uri，不然有些app无法适配
                if (!android.text.TextUtils.isEmpty(fileType)) {
                    if (fileType.contains("video/")) {
                        uri = getVideoContentUri(context, file);
                    } else if (fileType.contains("image/")) {
                        uri = getImageContentUri(context, file);
                    } else if (fileType.contains("audio/")) {
                        uri = getAudioContentUri(context, file);
                    }
                }
            }
        }
        return uri;
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param videoFile
     * @return content Uri
     */
    public static Uri getVideoContentUri(Context context, File videoFile) {
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (videoFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param audioFile
     * @return content Uri
     */
    public static Uri getAudioContentUri(Context context, File audioFile) {
        String filePath = audioFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID}, MediaStore.Audio.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/audio/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (audioFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static void shareFile(Context context, String filePath, OnFileOprateListener listener) {
        if (context == null || android.text.TextUtils.isEmpty(filePath)) {
            listener.sendMesage(0, "\nshareFile context is null or filePath is empty.");
            return;
        }

        File file = new File(filePath);
        if (file != null && file.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory("android.intent.category.DEFAULT");

// 如果需要指定分享到某个app，配置 componentName 即可
//            if (!TextUtils.isEmpty(componentName) && "com.tencent.mm".equals(componentName)){
//                // 分享精确到微信的页面，朋友圈页面，或者选择好友分享页面
//                ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
//                intent.setComponent(comp);
//            }

            Uri fileUri = getFileUri(context, file);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            // 授予目录临时共享权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            String fileType = "";
            if (fileUri != null && !android.text.TextUtils.isEmpty(fileUri.toString())) {
                ContentResolver contentResolver = context.getContentResolver();
                fileType = contentResolver.getType(fileUri);
            }

            if (android.text.TextUtils.isEmpty(fileType)) {
                fileType = getFileType(filePath); // 使用上面的根据文件头信息获取文件类型的方法
            }

            if (android.text.TextUtils.isEmpty(fileType)) {
                fileType = "*/*";
            }

            listener.sendMesage(0, "shareFile fileType:" + fileType);
            listener.sendMesage(0, "shareFile uri: " + fileUri);

            intent.setDataAndType(fileUri, fileType);

            try {
                context.startActivity(Intent.createChooser(intent, file.getName()));
                listener.sendMesage(0,"\n发送成功");
            } catch (Exception e) {
                listener.sendMesage(0,"\n发送失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件类型
     *
     * @param filePath
     * @return
     */
    public static String getFileType(String filePath) {
        return mFileTypes.get(getFileHeader(filePath));
    }

    private static final HashMap<String, String> mFileTypes = new HashMap<String, String>();

    // judge file type by file header content
    static {
        mFileTypes.put("ffd8ffe000104a464946", "jpg"); //JPEG (jpg)
        mFileTypes.put("89504e470d0a1a0a0000", "png"); //PNG (png)
        mFileTypes.put("47494638396126026f01", "gif"); //GIF (gif)
        mFileTypes.put("49492a00227105008037", "tif"); //TIFF (tif)
        mFileTypes.put("424d228c010000000000", "bmp"); //16色位图(bmp)
        mFileTypes.put("424d8240090000000000", "bmp"); //24位位图(bmp)
        mFileTypes.put("424d8e1b030000000000", "bmp"); //256色位图(bmp)
        mFileTypes.put("41433130313500000000", "dwg"); //CAD (dwg)
        mFileTypes.put("3c21444f435459504520", "html"); //HTML (html)
        mFileTypes.put("3c21646f637479706520", "htm"); //HTM (htm)
        mFileTypes.put("48544d4c207b0d0a0942", "css"); //css
        mFileTypes.put("696b2e71623d696b2e71", "js"); //js
        mFileTypes.put("7b5c727466315c616e73", "rtf"); //Rich Text Format (rtf)
        mFileTypes.put("38425053000100000000", "psd"); //Photoshop (psd)
        mFileTypes.put("46726f6d3a203d3f6762", "eml"); //Email [Outlook Express 6] (eml)
        mFileTypes.put("d0cf11e0a1b11ae10000", "doc"); //MS Excel 注意：word、msi 和 excel的文件头一样
        mFileTypes.put("d0cf11e0a1b11ae10000", "vsd"); //Visio 绘图
        mFileTypes.put("5374616E64617264204A", "mdb"); //MS Access (mdb)
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462d312e350d0a", "pdf"); //Adobe Acrobat (pdf)
        mFileTypes.put("2e524d46000000120001", "rmvb"); //rmvb/rm相同
        mFileTypes.put("464c5601050000000900", "flv"); //flv与f4v相同
        mFileTypes.put("00000020667479706d70", "mp4");
        mFileTypes.put("49443303000000002176", "mp3");
        mFileTypes.put("000001ba210001000180", "mpg"); //
        mFileTypes.put("3026b2758e66cf11a6d9", "wmv"); //wmv与asf相同
        mFileTypes.put("52494646e27807005741", "wav"); //Wave (wav)
        mFileTypes.put("52494646d07d60074156", "avi");
        mFileTypes.put("4d546864000000060001", "mid"); //MIDI (mid)
        mFileTypes.put("504b0304140000000800", "zip");
        mFileTypes.put("526172211a0700cf9073", "rar");
        mFileTypes.put("235468697320636f6e66", "ini");
        mFileTypes.put("504b03040a0000000000", "jar");
        mFileTypes.put("4d5a9000030000000400", "exe");//可执行文件
        mFileTypes.put("3c25402070616765206c", "jsp");//jsp文件
        mFileTypes.put("4d616e69666573742d56", "mf");//MF文件
        mFileTypes.put("3c3f786d6c2076657273", "xml");//xml文件
        mFileTypes.put("494e5345525420494e54", "sql");//xml文件
        mFileTypes.put("7061636b616765207765", "java");//java文件
        mFileTypes.put("406563686f206f66660d", "bat");//bat文件
        mFileTypes.put("1f8b0800000000000000", "gz");//gz文件
        mFileTypes.put("6c6f67346a2e726f6f74", "properties");//bat文件
        mFileTypes.put("cafebabe0000002e0041", "class");//bat文件
        mFileTypes.put("49545346030000006000", "chm");//bat文件
        mFileTypes.put("04000000010000001300", "mxp");//bat文件
        mFileTypes.put("504b0304140006000800", "docx");//docx文件
        mFileTypes.put("d0cf11e0a1b11ae10000", "wps");//WPS文字wps、表格et、演示dps都是一样的
        mFileTypes.put("6431303a637265617465", "torrent");


        mFileTypes.put("6D6F6F76", "mov"); //Quicktime (mov)
        mFileTypes.put("FF575043", "wpd"); //WordPerfect (wpd)
        mFileTypes.put("CFAD12FEC5FD746F", "dbx"); //Outlook Express (dbx)
        mFileTypes.put("2142444E", "pst"); //Outlook (pst)
        mFileTypes.put("AC9EBD8F", "qdf"); //Quicken (qdf)
        mFileTypes.put("E3828596", "pwl"); //Windows Password (pwl)
        mFileTypes.put("2E7261FD", "ram"); //Real Audio (ram)
        mFileTypes.put("null", null); //null
    }

    /**
     * 获取文件头信息
     *
     * @param filePath
     * @return
     */
    public static String getFileHeader(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.length() < 11) {
            return "null";
        }
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(file);
            byte[] b = new byte[10];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    /**
     * 将byte字节转换为十六进制字符串
     *
     * @param src
     * @return
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

}
