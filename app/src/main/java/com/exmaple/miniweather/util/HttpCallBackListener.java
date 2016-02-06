package com.exmaple.miniweather.util;

/**
 * Created by apple on 16/2/5.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
