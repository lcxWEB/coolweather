package com.cxli.review.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cx.li on 2015/12/10.
 */
public class Utility {

    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");

            JSONObject weatherInfo = jsonArray.getJSONObject(0);
            LogUtil.d("Main", weatherInfo.toString());

            JSONObject basic = weatherInfo.getJSONObject("basic");
            LogUtil.d("Main", basic.toString());
            String cityName = basic.getString("city");
            LogUtil.d("Main",cityName);
            String cityCode = basic.getString("id");
            LogUtil.d("Main",cityCode);
            JSONObject update = basic.getJSONObject("update");
            LogUtil.d("Main", update.toString());
            String publishTime = update.getString("loc");
            LogUtil.d("Main",publishTime);
            JSONObject now = weatherInfo.getJSONObject("now");
            LogUtil.d("Main",now.toString());
            JSONObject cond = now.getJSONObject("cond");
            LogUtil.d("Main",cond.toString());

            String weatherDesp = cond.getString("txt");
            LogUtil.d("Main",weatherDesp);
            String code = cond.getString("code");

            String tmp = now.getString("tmp");
            LogUtil.d("Main",tmp);

            saveWeatherInfo(context, cityName, cityCode, tmp,
                    weatherDesp, code, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String cityCode, String tmp, String weatherDesp,
                                       String code, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("city_code", cityCode);
        editor.putString("tmp", tmp);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("code", code);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }

    public static byte[] readInputStream(InputStream in) throws Exception{
        int len= 0;
        byte buf[] = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((len=in.read(buf))!=-1){
            out.write(buf, 0, len);  //把数据写入内存
        }
        out.close();  //关闭内存输出流
        return out.toByteArray(); //把内存输出流转换成byte数组
    }

    public static byte[] getImage(String path){
        URL url;
        byte[] b=null;
        HttpURLConnection con = null;
        try {
            url = new URL(path);   //设置URL
            con = (HttpURLConnection)url.openConnection();  //打开连接
            LogUtil.d("ImageTest", con.toString());
            con.setRequestMethod("GET"); //设置请求方法
            con.setConnectTimeout(8000);
            con.setReadTimeout(8000);
            con.setDoInput(true);
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();
            LogUtil.d("ImageTest", responseCode+"");
            InputStream in=con.getInputStream();  //取得字节输入流
            b=readInputStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return b;  //返回byte数组
    }
}
