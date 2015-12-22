package com.cxli.coolweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.cxli.coolweather.app.activity.WeatherActivity;
import com.cxli.coolweather.app.activity.WelcomeActivity;
import com.cxli.coolweather.app.receiver.AutoUpdateReceiver;
import com.cxli.coolweather.app.util.HttpCallBackListener;
import com.cxli.coolweather.app.util.HttpUtil;
import com.cxli.coolweather.app.util.Utility;

/**
 * Created by cx.li on 2015/12/16.
 */
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();

        //获取设置的频率
        SharedPreferences prefs = getSharedPreferences(WelcomeActivity.PREF_NAME, MODE_PRIVATE);
        long frequency = prefs.getLong("freq", 8);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long freHour = frequency * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + freHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String cityCode = prefs.getString("city_code", "");
        String address = "https://api.heweather.com/x3/weather?cityid=" + cityCode
                + "&key=" + WeatherActivity.KEY;

        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        }
    }
