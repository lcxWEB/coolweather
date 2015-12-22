package com.cxli.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;

import com.cxli.coolweather.app.R;
import com.cxli.coolweather.app.util.LogUtil;


/**
 * Created by lcx on 2015/12/19.
 */
public class WelcomeActivity extends Activity {

    boolean isFirstIn = false;

    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;

    //延迟3秒
    private static final long SPLANSH_DELAY_MILLS = 3000;

    public static final String PREF_NAME = "init_setting";

    /**
     * Handler:跳转到不同界面
     */

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
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

        Intent intent = new Intent(WelcomeActivity.this, ChooseAreaActivity.class);
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

        if(!isFirstIn) {
            //如果不是第一次运行则，3秒后跳转到主界面
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLANSH_DELAY_MILLS);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLANSH_DELAY_MILLS);
        }
    }
}
