package com.cxli.review.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.cxli.review.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by cx.li on 2015/12/17.
 */
public class ImageTest extends Activity  {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image);
        //     imageView.setImageResource(R.mipmap.ic_launcher);
        String imageAddress = "http://www.heweather.com/weather/images/logo.jpg";
        byte[] data = Utility.getImage(imageAddress);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                String imageAddress = "http://files.heweather.com/cond_icon/202.png";
                byte[] data = Utility.getImage(imageAddress);
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogUtil.d("ImageTest", data.length +"");
                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();*/
    }

    private Bitmap getBitmapFromUrl(String imgUrl) {
        URL url;
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(imgUrl);
            connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    private byte[] queryForImage(String imageAddress) throws Exception {

        URL url = new URL(imageAddress);
        byte[] response = null;

        HttpURLConnection connection = null;
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = 0;
            byte[] buff = new byte[1024];
            while ((len = in.read()) != -1) {
                out.write(buff, 0, len);
            }
            response = out.toByteArray();
            LogUtil.d("ImageTest", "长度" + response.length);
        }
        else {
            LogUtil.d("ImageTest", responseCode +"");
        }
        return response;
    }
}
