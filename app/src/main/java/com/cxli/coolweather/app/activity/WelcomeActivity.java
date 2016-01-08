package com.cxli.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.cxli.coolweather.app.R;
import com.cxli.coolweather.app.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by lcx on 2015/12/19.
 */
public class WelcomeActivity extends Activity {

    public static final String PREF_NAME = "init_setting";
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;

    //延迟3秒
    private static final long SPLANSH_DELAY_MILLS = 3000;
    boolean isFirstIn = false;
    /**
     * Handler:跳转到不同界面
     */

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
        }
    };

    private void goHome() {

//        Intent intent = new Intent(WelcomeActivity.this, ChooseAreaActivity.class);
        //加了DrawerLayout后，直接跳转到weatherActivity
        Intent intent = new Intent(WelcomeActivity.this, WeatherActivity.class);
        WelcomeActivity.this.startActivity(intent);
        WelcomeActivity.this.finish();
    }

    private void goGuide() {
        Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
        WelcomeActivity.this.startActivity(intent);
        LogUtil.d("Welcome", "guide");
        WelcomeActivity.this.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);
        init();
    }

    private void init() {
        //读取SharedPreferences中需要的数据
        //使用SharedPreferences来记录程序是否是第一次使用
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        //取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);

        if (!isFirstIn) {
            //如果不是第一次运行则，3秒后跳转到主界面
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLANSH_DELAY_MILLS);
        } else {
            importDatabase();
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLANSH_DELAY_MILLS);
        }
    }

    private void importDatabase() {
        //存放数据库的目录
        String path = "/data/data/com.cxli.coolweather.app/databases";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //数据库文件
        File file = new File(dir, "cool_weather");
        if (!file.exists()) {
            try {
                file.createNewFile();
                //加载需要导入的数据库
                InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.cool_weather);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
