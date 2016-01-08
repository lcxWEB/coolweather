package com.cxli.coolweather.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cxli.coolweather.app.R;
import com.cxli.coolweather.app.activity.WeatherActivity;
import com.cxli.coolweather.app.activity.WelcomeActivity;

import java.util.ArrayList;

/**
 * Created by lcx on 2015/12/19.
 */
public class MyViewPagerAdapter extends PagerAdapter {

    //界面列表
    private ArrayList<View> views;
    private Activity activity;

    public MyViewPagerAdapter (ArrayList<View> views, Activity activity) {
        this.views = views;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    //初始化position位置的界面
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ((ViewPager) container).addView(views.get(position));

        if (position == views.size() - 1){
            Button startButton = (Button) container.findViewById(R.id.jinru);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置已经引导
                    setGuided();
                    goHome();
                }
            });
        }

        return views.get(position);
    }

    private void goHome() {
//        Intent intent = new Intent(activity, ChooseAreaActivity.class);
        //加了DrawerLayout后，直接跳转到weatherActivity
        Intent intent = new Intent(activity, WeatherActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }


    private void setGuided() {

        SharedPreferences preferences = activity.getSharedPreferences(WelcomeActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("isFirstIn", false);
        editor.commit();
    }

    //销毁position位置的界面
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(views.get(position));
    }

    //判断是否由对象生成界面
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
