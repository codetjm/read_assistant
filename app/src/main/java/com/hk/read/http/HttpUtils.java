package com.hk.read.http;

import android.text.TextUtils;
import android.util.Log;

import com.hk.read.app.MyApplication;
import com.hk.read.utils.FileUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Publisher;

import java.io.IOException;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/******************************************
 * author: changfeng (changfeng@51huxin.com)
 * createDate: 2017/6/21
 * company: (C) Copyright 阳光互信 2017
 * since: JDK 1.8
 * Description: 注释写这里
 ******************************************/

public class HttpUtils {
    public static final String TRANSLATE_URL = "http://fanyi.youdao.com/openapi.do?keyfrom=SeeYouLater&key=12615936&type=data&doctype=json&version=1.1&q=";
    public static final String GET_REQUEST_ID = "https://aip.baidubce.com/rest/2.0/solution/v1/form_ocr/request?access_token=";
    public static final String GET__FORM_RESULT = "https://aip.baidubce.com/rest/2.0/solution/v1/form_ocr/get_request_result?access_token=";

    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    public static void traslate(String text, final OnResultListener listener) {
        Request.Builder requestBuilder = new Request.Builder().url(TRANSLATE_URL + text);
        //可以省略，默认是GET请求
        Request request = requestBuilder.build();
        Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFial(-1,e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onSuccess(response.body().string());
            }
        });
    }

    /**
     * 表格识别 网络请求
     * @param filePath
     * @return
     */
    public static Flowable<String> getFormResult(String filePath) {


        return Flowable.just(filePath)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        //请求request_id
                        String token = MyApplication.getApp().getToken();
                        if (TextUtils.isEmpty(token)) {
                            Log.e("====","表格扫描初始化失败");
                            return "";
                        }
                        String str = FileUtil.getImageStr(s);
                        FormBody body = new FormBody.Builder()
                                .add("image",str)
                                .build();
                        Request request = new Request.Builder().url(GET_REQUEST_ID + token)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .post(body)
                                .build();

                        Response response = mOkHttpClient.newCall(request).execute();
                        return response.body().string();
                    }
                })

                .flatMap(new Function<String, Publisher<String>>() {
                    @Override
                    public Publisher<String> apply(@NonNull String s) throws Exception {
                        String token = MyApplication.getApp().getToken();
                        if (TextUtils.isEmpty(token)) {
                            Log.e("====","表格扫描初始化失败");
                            return Flowable.just("");
                        }

//                        {
//                            "result" : [
//                            {
//                                "request_id" : "1234_6789"
//                            }
//                            ],
//                            "log_id":149689853984104
//                        }
                        JSONObject obj = new JSONObject(s);
                        boolean result1 = obj.has("result");
                        if (!result1) {
                            Log.e("====", "获取request_id失败，错误码：" + obj.getString("error_code")+"\n错误消息："+obj.getString("error_msg"));
                            return Flowable.just("");
                        }
                        JSONArray result = obj.getJSONArray("result");
                        JSONObject object = result.getJSONObject(0);
                        String request_id = object.getString("request_id");

                        FormBody body = new FormBody.Builder()
                                .add("request_id", request_id)
                                .add("result_type","excel")//默认返回的是excel，也可返回“json”
                                .build();
                        Request request = new Request.Builder().url(GET__FORM_RESULT + token)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .post(body)
                                .build();

                        Response response = mOkHttpClient.newCall(request).execute();
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.i("====","识别结果："+jsonObject.toString());
//                        {
//                            "result" : {
//                            "result_data" : "",
//                                    "persent":100,
//                                    "request_id": "149691317905102",
//                                    "ret_code": 3
//                            "ret_msg": "已完成",
//                        },
//                            "log_id":149689853984104
//                        }
                        boolean result2 = jsonObject.has("result");
                        if (!result2) {
                            Log.e("====", "识别失败，错误码：" + jsonObject.getString("error_code"));
                            return Flowable.just("");
                        }
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        return Flowable.just(jsonObject1.toString());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static Flowable pollResult(final String request_id,final OnResultListener listener) {
        return Flowable.just(request_id)

                .flatMap(new Function<String, Publisher<String>>() {
                    @Override
                    public Publisher<String> apply(@NonNull String s) throws Exception {
                        String token = MyApplication.getApp().getToken();
                        if (TextUtils.isEmpty(token)) {
                            Log.e("====","表格扫描初始化失败");
                            return Flowable.just("");
                        }

//                        {
//                            "result" : [
//                            {
//                                "request_id" : "1234_6789"
//                            }
//                            ],
//                            "log_id":149689853984104
//                        }


                        FormBody body = new FormBody.Builder()
                                .add("request_id", s)
                                .add("result_type","excel")//默认返回的是excel，也可返回“json”
                                .build();
                        Request request = new Request.Builder().url(GET__FORM_RESULT + token)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .post(body)
                                .build();

                        Response response = mOkHttpClient.newCall(request).execute();
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.i("====","\n轮询："+jsonObject.toString());
//                        {
//                            "result" : {
//                            "result_data" : "",
//                                    "persent":100,
//                                    "request_id": "149691317905102",
//                                    "ret_code": 3
//                            "ret_msg": "已完成",
//                        },
//                            "log_id":149689853984104
//                        }
                        boolean result2 = jsonObject.has("result");
                        if (!result2) {
                            Log.e("====", "识别失败，错误码：" + jsonObject.getString("error_code"));
                            return Flowable.just("");
                        }
                        String result_data = jsonObject.optJSONObject("result").optString("result_data");
                        return Flowable.just(result_data);
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String s) throws Exception {
                        boolean b = TextUtils.isEmpty(s) ? true : false;//是否继续请求
                        if (b){
                            listener.onFial(-2,"\n未完成");
                            pollResult(request_id,listener).subscribe();
                        }else {
                            listener.onSuccess(s);
                        }
                        Log.i("====","表格结果："+s);
                        return b;
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}
