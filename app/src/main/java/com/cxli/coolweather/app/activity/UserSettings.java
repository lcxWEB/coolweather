package com.cxli.coolweather.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.cxli.coolweather.app.R;
import com.cxli.coolweather.app.service.AutoUpdateService;
import com.cxli.coolweather.app.util.LogUtil;

/**
 * Created by cx.li on 2015/12/21.
 */
public class UserSettings extends Activity implements CompoundButton.OnCheckedChangeListener {

    private boolean isAuto;
    private Switch isAutoButton;

    private LinearLayout setFreq;

    private EditText freq;
    private long frequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);

        //读取SharedPreferences中需要的数据
        //使用SharedPreferences来记录程序按钮的状态
        SharedPreferences prefs = getSharedPreferences(WelcomeActivity.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editors = prefs.edit();
        isAuto = prefs.getBoolean("isAuto", true);
        isAutoButton = (Switch) findViewById(R.id.isauto);
        setFreq = (LinearLayout) findViewById(R.id.setfreq);

        //获取设置的更新频率，默认8小时
        frequency = prefs.getLong("freq", 8);
        freq = (EditText) findViewById(R.id.freq);
        freq.setText(String.valueOf(frequency));
        Intent intent = new Intent(this, AutoUpdateService.class);
        if (isAuto) {
            isAutoButton.setChecked(true);
            setFreq.setVisibility(View.VISIBLE);
            startService(intent);
            //UserSettings.this.finish();
        } else {
            isAutoButton.setChecked(false);
            setFreq.setVisibility(View.GONE);
            stopService(intent);
        }
        isAutoButton.setOnCheckedChangeListener(this);
        LogUtil.d("Settings", "come to Setting");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = getSharedPreferences(WelcomeActivity.PREF_NAME, MODE_PRIVATE).edit();
        Intent intent = new Intent(this, AutoUpdateService.class);
        if (isChecked) {
            editor.putBoolean("isAuto", true);
            setFreq.setVisibility(View.VISIBLE);
            startService(intent);
            Toast.makeText(this, "允许自动更新", Toast.LENGTH_SHORT).show();
        } else {
            editor.putBoolean("isAuto", false);
            setFreq.setVisibility(View.GONE);
            Toast.makeText(this, "禁止自动更新", Toast.LENGTH_SHORT).show();
            stopService(intent);
        }
        editor.commit();
    }


    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = getSharedPreferences(WelcomeActivity.PREF_NAME, MODE_PRIVATE).edit();
        editor.putLong("freq", Long.parseLong(freq.getText().toString()));
        editor.commit();
        super.onBackPressed();
    }
}
