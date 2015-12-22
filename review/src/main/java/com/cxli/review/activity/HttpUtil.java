package com.cxli.review.activity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
                    BufferedReader buffreader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = buffreader.readLine()) != null) {
                        sb.append(line);
                    }
                    String response = sb.toString();

                    LogUtil.d("MainAc", response);

                    if (listener != null) {
                        //回调onFinish方法
                        listener.onFinish(response);
                        LogUtil.d("MainA", "onFinish");
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

    public static void getImage(final String imageAddress, final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(imageAddress);

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int len = 0;
                    byte[] buff = new byte[1024];
                    while (( len= in.read()) != -1) {
                        out.write(buff, 0, len);
                    }
                    byte[] response = out.toByteArray();
                    LogUtil.d("ImageTest", "长度" +response.length);
                    if (listener != null) {
                        //回调onFinish方法
                        listener.onFinish(response);
                        LogUtil.d("ImageTest", "onFinish");
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
