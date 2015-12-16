package com.cxli.coolweather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxli.coolweather.app.R;
import com.cxli.coolweather.app.util.HttpCallBackListener;
import com.cxli.coolweather.app.util.HttpUtil;
import com.cxli.coolweather.app.util.LogUtil;
import com.cxli.coolweather.app.util.Utility;

/**
 * Created by lcx on 2015/12/15.
 */
public class WeatherActivity extends Activity {

    public static final String key = "a1213f6e06e84d8286603bd7e6f8e8bd";
    private LinearLayout weatherInfoLayout;
    private TextView cityName;
    /**
     * 用于显示发布时间
     */
    private TextView publishText;
    private TextView weatherDesp;
    private TextView tmp;
    private TextView currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info);
        cityName = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDesp = (TextView) findViewById(R.id.weather_desp);
        tmp = (TextView) findViewById(R.id.tmp);
        currentDate = (TextView) findViewById(R.id.current_date);

        String cityCode = getIntent().getStringExtra("cityCode");

        if (!TextUtils.isEmpty(cityCode)) {
            //有城市代号时就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityName.setVisibility(View.INVISIBLE);
            queryFromServer(cityCode);
            LogUtil.d("MainA", cityCode);
        } else {
            showWeather();
        }
    }

    private void queryFromServer(String cityCode) {

        String address = "https://api.heweather.com/x3/weather?cityid=" + cityCode
                + "&key=" + key;

        LogUtil.d("Main", address);

        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {

                if (!TextUtils.isEmpty(response)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    LogUtil.d("Main", "HandleWeatherResponse");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                            LogUtil.d("Main", "showWeather");
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });



    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityName.setText(prefs.getString("city_name", ""));
        tmp.setText(prefs.getString("tmp", ""));
        weatherDesp.setText(prefs.getString("weather_desp", ""));
        publishText.setText(prefs.getString("publish_time", ""));
        currentDate.setText(prefs.getString("current_date", "") + "发布");
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);
    }
}
