package com.cxli.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cxli.coolweather.app.activity.WeatherActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

            String tigan = now.getString("fl");
            String shidu = now.getString("hum");
            String kejian = now.getString("vis");
            JSONObject wind = now.getJSONObject("wind");
            String fengxiang = wind.getString("dir");

            String tmp = now.getString("tmp");
            LogUtil.d("Main", tmp);

            JSONArray dailyforecast = weatherInfo.getJSONArray("daily_forecast");
            JSONObject tomorrow = dailyforecast.getJSONObject(1);
            JSONObject tcond = tomorrow.getJSONObject("cond");
            String tweather = tcond.getString("txt_d");
            String pop = tomorrow.getString("pop");
            JSONObject ttmp = tomorrow.getJSONObject("tmp");
            String maxtmp = ttmp.getString("max");
            String mintmp = ttmp.getString("min");

            saveWeatherInfo(context, cityName, cityCode, tmp,
                    weatherDesp, code, publishTime, tigan, shidu, kejian, fengxiang,
                    tweather, pop, maxtmp, mintmp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String cityCode, String tmp, String weatherDesp,
                                       String code, String publishTime, String tigan,
                                       String shidu, String kejian, String fengxiang,
                                       String tweather, String pop, String maxtmp, String mintmp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

        SharedPreferences.Editor editor = context.getSharedPreferences(WeatherActivity.PREF_WEATHER,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("city_code", cityCode);
        editor.putString("tmp", tmp);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("code", code);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.putString("tigan", tigan);
        editor.putString("shidu", shidu);
        editor.putString("kejian", kejian);
        editor.putString("fengxiang", fengxiang);
        editor.putString("tweather", tweather);
        editor.putString("pop", pop);
        editor.putString("maxtmp", maxtmp);
        editor.putString("mintmp", mintmp);

        editor.commit();
    }
}
