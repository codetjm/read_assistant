package com.cybertron.service.http;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
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
    private static OkHttpClient mOkHttpClient=new OkHttpClient();
    public static void traslate(String text, final OnResultListener listener) {
        Request.Builder requestBuilder = new Request.Builder().url(TRANSLATE_URL+text);
        //可以省略，默认是GET请求
        Request request = requestBuilder.build();
        Call mcall= mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFial();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onSuccess(response.body().string());
            }
        });
    }
}
