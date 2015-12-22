package com.cxli.coolweather.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cxli.coolweather.app.R;
import com.cxli.coolweather.app.adapter.MyViewPagerAdapter;

import java.util.ArrayList;
/**
 * Created by lcx on 2015/12/19.
 */
public class GuideActivity extends Activity {

    private ViewPager vp;

    private ArrayList<View> views;

    private MyViewPagerAdapter vpAdapter;

    //底部圆点图片
    private ImageView[] dots;

    private int currentIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.guide);
        //初始化页面
        initViews();
        //初始化底部小点
        initDots();
    }

    private void initViews() {

        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<>();
        views.add(inflater.inflate(R.layout.guide1, null));
        views.add(inflater.inflate(R.layout.guide2, null));
        views.add(inflater.inflate(R.layout.guide3, null));

        vpAdapter = new MyViewPagerAdapter(views, GuideActivity.this);

        vp = (ViewPager) findViewById(R.id.guidePages);
        vp.setAdapter(vpAdapter);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //界面选中时调用
            @Override
            public void onPageSelected(int position) {
                //设置底部小点选中状态
                setCurrentDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setCurrentDot(int position) {
        if(position < 0 || position > views.size() - 1
                || currentIndex == position ) {
            return;
        }

        dots[position].setImageResource(R.drawable.point_selected);
        dots[currentIndex].setImageResource(R.drawable.point);
        currentIndex = position;
    }

    private void initDots() {

        LinearLayout ll = (LinearLayout) findViewById(R.id.dots);

        dots = new ImageView[views.size()];

        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setImageResource(R.drawable.point);
        }

        currentIndex = 0;
        dots[currentIndex].setImageResource(R.drawable.point_selected);

    }
}
