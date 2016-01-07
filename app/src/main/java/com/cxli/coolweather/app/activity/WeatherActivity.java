package com.cxli.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cxli.coolweather.app.R;
import com.cxli.coolweather.app.service.AutoUpdateService;
import com.cxli.coolweather.app.util.HttpCallBackListener;
import com.cxli.coolweather.app.util.HttpUtil;
import com.cxli.coolweather.app.util.LogUtil;
import com.cxli.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by lcx on 2015/12/15.
 */
public class WeatherActivity extends Activity {
    public static final String KEY = "a1213f6e06e84d8286603bd7e6f8e8bd";

    public static final String PREF_WEATHER = "pref_weather";
    private RelativeLayout background;
    private RelativeLayout weatherInfoLayout;
    private LinearLayout cityDesp;
    private LinearLayout detail;
    private TextView cityName;
    private ImageView image;

    private Button home;
    private Button refresh;
    private CompoundButton isAutoButton;
    /**
     * 用于显示发布时间
     */
    private TextView publishText;
    private TextView weatherDesp;
    private TextView tmp;
    private TextView currentDate;
    private ProgressDialog progressDialog;

    private TextView tigan;
    private TextView shidu;
    private TextView kejian;
    private TextView fengxiang;

    private TextView tomorrow;

    private ScrollView scrollView;
    private TextView clothDetail;
    private TextView sportsDetail;
    private TextView travDetail;
    private TextView coldDetail;
    private ArrayList<Fragment> fragments = new ArrayList<>();;

    public static final String TAG = "WeatherActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        //在activity中不显示图标icon
        getActionBar().setDisplayShowHomeEnabled(false);
        weatherInfoLayout = (RelativeLayout) findViewById(R.id.weather_info);
        background = (RelativeLayout) findViewById(R.id.background);
        cityDesp = (LinearLayout) findViewById(R.id.citydesp);
        detail = (LinearLayout) findViewById(R.id.detail);

        cityName = (TextView) findViewById(R.id.city_name);

        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDesp = (TextView) findViewById(R.id.weather_desp);
        tmp = (TextView) findViewById(R.id.tmp);
        image = (ImageView) findViewById(R.id.image);

        currentDate = (TextView) findViewById(R.id.current_date);

        tigan = (TextView) findViewById(R.id.tigan);
        shidu = (TextView) findViewById(R.id.shidu);
        kejian = (TextView) findViewById(R.id.kejian);
        fengxiang = (TextView) findViewById(R.id.fengxiang);

        tomorrow = (TextView) findViewById(R.id.tomorrow);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        clothDetail = (TextView) findViewById(R.id.cloth_detail);
        sportsDetail = (TextView) findViewById(R.id.sport_detail);
        travDetail = (TextView) findViewById(R.id.travel_detail);
        coldDetail = (TextView) findViewById(R.id.cold_detail);

        isAutoButton = (CompoundButton) findViewById(R.id.isauto);

        String cityCode = getIntent().getStringExtra("cityCode");

        if (!TextUtils.isEmpty(cityCode)) {
            //有城市代号时就去查询天气
            publishText.setText("同步中...");
            showProgressDialog();
            scrollView.setVisibility(View.INVISIBLE);
            cityDesp.setVisibility(View.INVISIBLE);
            queryFromServer(cityCode);
            LogUtil.d(TAG, cityCode);
        } else {
            showWeather();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //当menuitem被选中时调用
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshWeather();
                return true;
            case R.id.home:
                goChoose();
                return true;
           /* case R.id.add:
                addCity();
                return true;*/
            case R.id.settings:
                /*Toast.makeText(this, "you cliked setting", Toast.LENGTH_SHORT).show();*/
                goSettings();
                return true;
            case R.id.login:
                Toast.makeText(this, "you cliked login", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addCity() {
        Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
        intent.putExtra("is_from_weather", true);
        WeatherActivity.this.startActivityForResult(intent, 0);

    }

    private void queryFromServer(String cityCode) {

        String address = "https://api.heweather.com/x3/weather?cityid=" + cityCode
                + "&key=" + KEY;

        LogUtil.d(TAG, address);

        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {

                if (!TextUtils.isEmpty(response)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    LogUtil.d(TAG, "HandleWeatherResponse");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            showWeather();
                            LogUtil.d(TAG, "showWeather");
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = getSharedPreferences(PREF_WEATHER, MODE_PRIVATE);
        cityName.setText(prefs.getString("city_name", ""));
        tmp.setText("当前温度  " + prefs.getString("tmp", "") + "℃");
        String weather_desp = prefs.getString("weather_desp", "");
        weatherDesp.setText(weather_desp);
        if (weather_desp.contains("晴")) {
            image.setImageResource(R.drawable.sunny);
        } else if (weather_desp.contains("云")) {
            image.setImageResource(R.drawable.cloudy);
        } else if (weather_desp.contains("雨")) {
            image.setImageResource(R.drawable.rain);
        } else if (weather_desp.contains("雪")) {
            image.setImageResource(R.drawable.snow);
        } else if (weather_desp.contains("阴")) {
            image.setImageResource(R.drawable.overcast);
        } else {
            image.setImageResource(R.drawable.fog);
        }
        publishText.setText(prefs.getString("publish_time", "") + "发布");
        currentDate.setText(prefs.getString("current_date", ""));

        tigan.setText(prefs.getString("tigan", "") + "℃");
        shidu.setText(prefs.getString("shidu", "") + "%");
        kejian.setText(prefs.getString("kejian", "") + "km");
        fengxiang.setText(prefs.getString("fengxiang", ""));

        tomorrow.setText("天气类型 :（" + prefs.getString("tweather", "") + "） 温度区间 :（"
                + prefs.getString("mintmp", "") + "℃ - " + prefs.getString("maxtmp", "")
                + "℃） 降水概率 :（" + prefs.getString("pop", "") + "%）");

        clothDetail.setText(prefs.getString("cloth", ""));
        sportsDetail.setText(prefs.getString("sports", ""));
        travDetail.setText(prefs.getString("trav", ""));
        coldDetail.setText(prefs.getString("flu", ""));

        scrollView.setVisibility(View.VISIBLE);
        cityDesp.setVisibility(View.VISIBLE);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour <= 17) {
            background.setBackgroundResource(R.drawable.day);
        } else {
            background.setBackgroundResource(R.drawable.night);
        }

        SharedPreferences pref = getSharedPreferences(WelcomeActivity.PREF_NAME, MODE_PRIVATE);
        boolean isAuto = pref.getBoolean("isAuto", true);
        if (isAuto) {
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }
    }

    private void goChoose() {
        Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
        intent.putExtra("is_from_weather", true);
        WeatherActivity.this.startActivity(intent);
        WeatherActivity.this.finish();
    }

    private void refreshWeather() {
        publishText.setText("同步中...");
        showProgressDialog();
        SharedPreferences prefs = getSharedPreferences(PREF_WEATHER, MODE_PRIVATE);
        String cityCode = prefs.getString("city_code", "");
        LogUtil.d(TAG, cityCode);
        if (!TextUtils.isEmpty(cityCode)) {
            queryFromServer(cityCode);
        }
    }

    private void goSettings() {
        Intent intent = new Intent(WeatherActivity.this, UserSettings.class);
        LogUtil.d(TAG, "gosetting");
        WeatherActivity.this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
