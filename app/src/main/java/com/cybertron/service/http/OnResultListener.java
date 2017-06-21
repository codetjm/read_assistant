package com.cybertron.service.http;

/******************************************
 * author: changfeng (changfeng@51huxin.com)
 * createDate: 2017/6/21
 * company: (C) Copyright 阳光互信 2017
 * since: JDK 1.8
 * Description: 注释写这里
 ******************************************/

public interface OnResultListener {
    void onSuccess(String json);
    void onFial();
}
