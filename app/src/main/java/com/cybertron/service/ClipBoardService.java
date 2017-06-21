package com.cybertron.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cybertron.service.http.HttpUtils;
import com.cybertron.service.http.OnResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Administrator
 */
public class ClipBoardService extends Service {

    private MyBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        final Intent mIntent = new Intent();
        mIntent.setAction("com.cybertron.dict.ClipBoardReceiver");
        cm.addPrimaryClipChangedListener(new OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData data = cm.getPrimaryClip();
                Item item = data.getItemAt(0);
                Log.e("====", "\n复制文字:" + item.getText());
                if (!TextUtils.isEmpty(item.getText())) {
                    HttpUtils.traslate((String) item.getText(), new OnResultListener() {
                        @Override
                        public void onSuccess(String json) {
                            StringBuffer buffer = new StringBuffer();
                            try {
                                JSONObject obj = new JSONObject(json);
                                JSONArray translation = obj.getJSONArray("translation");
                                String trans = translation.getString(0);
                                buffer.append("参考释义："+trans + "\n");
                                JSONObject basic = obj.getJSONObject("basic");
                                JSONArray explains = basic.getJSONArray("explains");
                                buffer.append("详细释义：\n");
                                for (int i = 0; i < explains.length(); i++) {
                                    buffer.append(explains.getString(i) + "\n");
                                }
                                Log.i("====", buffer.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mIntent.putExtra("clipboardvalue", buffer.toString());
                            sendBroadcast(mIntent);
                        }

                        @Override
                        public void onFial() {
                            Toast.makeText(ClipBoardService.this, "翻译失败", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });
    }

    @Override
    public void onStart(Intent intent, int startId) {
    }

    @Override
    public void onDestroy() {
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        ClipBoardService getService() {
            return ClipBoardService.this;
        }
    }

}
