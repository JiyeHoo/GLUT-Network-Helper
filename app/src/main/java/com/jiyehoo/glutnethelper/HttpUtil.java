package com.jiyehoo.glutnethelper;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author JiyeHoo
 * @description:
 * @date :2021/5/12 上午12:35
 */
public class HttpUtil {
    public static void sendGetRequest(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
