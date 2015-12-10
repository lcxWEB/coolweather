package com.cxli.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cx.li on 2015/12/10.
 */
public class HttpUtil {

    public static void sendHttpRequest(final String address, final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(address);

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();

                    //对输入流进行读取
                    // Log.d("MainActivity", "InputStream ");
                    BufferedReader buffreader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = buffreader.readLine()) != null) {
                        sb.append(line);
                    }
                    String response = sb.toString();
                    if (listener != null) {
                        //回调onFinish方法
                        listener.onFinish(response);
                    }
                } catch (Exception e) {
                    if (e != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
