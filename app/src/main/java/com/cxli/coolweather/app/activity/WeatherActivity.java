package com.cxli.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cxli.coolweather.app.R;
import com.cxli.coolweather.app.db.CoolWeatherDB;
import com.cxli.coolweather.app.model.City;
import com.cxli.coolweather.app.model.County;
import com.cxli.coolweather.app.model.Province;
import com.cxli.coolweather.app.service.AutoUpdateService;
import com.cxli.coolweather.app.util.HttpCallBackListener;
import com.cxli.coolweather.app.util.HttpUtil;
import com.cxli.coolweather.app.util.LogUtil;
import com.cxli.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by lcx on 2015/12/15.
 */
public class WeatherActivity extends Activity {
    public static final String KEY = "a1213f6e06e84d8286603bd7e6f8e8bd";

    public static final String PREF_WEATHER = "pref_weather";
    public static final String TAG = "WeatherActivity";
    private RelativeLayout background;
    private LinearLayout cityDesp;
    private TextView cityName;
    private ImageView image;

    /**
     * 用于显示发布时间
     */
    private TextView publishText;
    private TextView weatherDesp;
    private TextView tmp;
    private TextView currentDate;
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
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;


    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();


    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;
    /**
     * 是否从WeatherActivity中跳转过来。
     */
    private boolean isFromWeatherActivity;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        //在activity中不显示图标icon
        getActionBar().setDisplayShowHomeEnabled(false);
//        设置actionbar不显示标题
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable background = getResources().getDrawable(R.drawable.top_bar_background);
        getActionBar().setBackgroundDrawable(background);

        initViews();
        initData();
        initEvents();

//        String cityCode = getIntent().getStringExtra("cityCode");
    }

    private void initViews() {
        background = (RelativeLayout) findViewById(R.id.background);
        cityDesp = (LinearLayout) findViewById(R.id.citydesp);

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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.setDrawerListener(new CitySelectDrawerListener());
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, this.mDrawerLayout,
               R.string.drawer_open, R.string.drawer_close);

        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        // Set the adapter for the list view
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
    }

    private void initData() {
        coolWeatherDB = CoolWeatherDB.getInstance(this);
       /* //获取省份列表
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() <= 0) {
            init();//初始化省市县级数据到数据库中
        }*/
        queryProvinces();  // 加载省级数据

        SharedPreferences prefs = getSharedPreferences(PREF_WEATHER, MODE_PRIVATE);
        String cityCode = prefs.getString("city_code", "");
        LogUtil.d(TAG, cityCode);
        if (!TextUtils.isEmpty(cityCode)) {
            //默认显示上次保存的数据
            showWeather();
        } else {
            //安装收首次启动，city_code为空，设置默认背景图片
            background.setBackgroundResource(R.drawable.day);
            //安装后首次启动，默认开启抽屉
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }

    }

    private void init() {
        String[] pro = {"北京", "上海", "天津", "重庆", "黑龙江", "吉林", "辽宁",
                "内蒙古", "河北", "山西", "陕西", "山东", "新疆", "西藏", "青海", "甘肃",
                "宁夏", "河南", "江苏", "湖北", "浙江", "安徽", "福建", "江西", "湖南",
                "贵州", "四川", "广东", "云南", "广西", "海南", "香港", "澳门", "台湾"};

        Province p = new Province();
        for (int i = 0; i < pro.length; i++) {
            p.setProvinceName(pro[i]);
            if (i < 9) {
                p.setProvinceCode("CN0" + (i + 1));
            } else {
                p.setProvinceCode("CN" + (i + 1));
            }
            coolWeatherDB.saveProvince(p);
        }

        String[] city1 = {"北京"};
        String[] city2 = {"上海"};
        String[] city3 = {"天津"};
        String[] city4 = {"重庆"};
        String[] city5 = {"哈尔滨", "齐齐哈尔", "牡丹江", "佳木斯", "绥化", "黑河", "大兴安岭",
                "伊春", "大庆", "七台河", "鸡西", "鹤岗", "双鸭山"};
        String[] city6 = {"长春", "吉林", "延边", "四平", "通化", "白城", "辽源", "松原", "白山"};
        String[] city7 = {"沈阳", "大连", "鞍山", "抚顺", "本溪", "丹东", "锦州", "营口", "阜新", "辽阳",
                "铁岭", "朝阳", "盘锦", "葫芦岛"};
        String[] city8 = {"呼和浩特", "包头", "乌海", "乌兰察布", "通辽", "兴安盟", "赤峰", "鄂尔多斯", "巴彦淖尔",
                "锡林郭勒", "呼伦贝尔", "阿拉善盟"};
        String[] city9 = {"石家庄", "保定", "张家口", "承德", "唐山", "廊坊", "沧州", "衡水", "邢台", "邯郸", "秦皇岛"};


        City ci = new City();
        for (int i = 0; i < city1.length; i++) {
            ci.setCityName(city1[i]);
            if (i < 9) {
                ci.setCityCode("CN101010" + (i + 1) + "00");
            } else {
                ci.setCityCode("CN10101" + (i++));
            }
            ci.setProvinceId(1);
            coolWeatherDB.saveCity(ci);
        }
        for (int i = 0; i < city2.length; i++) {
            ci.setCityName(city2[i]);
            if (i < 9) {
                ci.setCityCode("CN101020" + (i + 1) + "00");
            } else {
                ci.setCityCode("CN10102" + (i + 1));
            }
            ci.setProvinceId(2);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city3.length; i++) {
            ci.setCityName(city3[i]);
            if (i < 9) {
                ci.setCityCode("CN101030" + (i + 1) + "00");
            } else {
                ci.setCityCode("CN10103" + (i + 1));
            }
            ci.setProvinceId(3);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city4.length; i++) {
            ci.setCityName(city4[i]);
            if (i < 9) {
                ci.setCityCode("CN101040" + (i + 1) + "00");
            } else {
                ci.setCityCode("CN10104" + (i + 1));
            }
            ci.setProvinceId(4);
            coolWeatherDB.saveCity(ci);
        }
        for (int i = 0; i < city5.length; i++) {
            ci.setCityName(city5[i]);
            if (city5[i].equals("七台河")) {
                ci.setCityCode("CN101051002");
            } else {
                if (i < 9) {
                    ci.setCityCode("CN101050" + (i + 1) + "01");
                } else {
                    ci.setCityCode("CN10105" + (i + 1) + "01");
                }
            }
            ci.setProvinceId(5);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city6.length; i++) {
            ci.setCityName(city6[i]);
            if (i < 9) {
                ci.setCityCode("CN101060" + (i + 1) + "01");
            } else {
                ci.setCityCode("CN10106" + (i + 1) + "01");
            }
            ci.setProvinceId(6);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city7.length; i++) {
            ci.setCityName(city7[i]);
            if (i < 9) {
                ci.setCityCode("CN101070" + (i + 1) + "01");
            } else {
                ci.setCityCode("CN10107" + (i + 1) + "01");
            }
            ci.setProvinceId(7);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city8.length; i++) {
            ci.setCityName(city8[i]);
            if (i < 9) {
                ci.setCityCode("CN101080" + (i + 1) + "01");
            } else {
                ci.setCityCode("CN10108" + (i + 1) + "01");
            }
            ci.setProvinceId(8);
            coolWeatherDB.saveCity(ci);
        }
        for (int i = 0; i < city9.length; i++) {
            ci.setCityName(city9[i]);
            if (i < 9) {
                ci.setCityCode("CN101090" + (i + 1) + "01");
            } else {
                ci.setCityCode("CN10109" + (i + 1) + "01");
            }
            ci.setProvinceId(9);
            coolWeatherDB.saveCity(ci);
        }
    }

    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        LogUtil.d(TAG, provinceList.size()+"省级数据");
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }
    }

    private void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }
    }

    private void initEvents() {
        // Set the list's click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index,
                                    long arg3) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    //selectedCity = cityList.get(index);
                    //queryCounties();
                    String cityCode = cityList.get(index).getCityCode();
                    /*Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("cityCode", cityCode);
                    startActivity(intent);
                    finish();*/
                    if (!TextUtils.isEmpty(cityCode)) {
                        //有城市代号时就去查询天气
                        publishText.setText("同步中...");
                        showProgressDialog();
                        scrollView.setVisibility(View.INVISIBLE);
                        cityDesp.setVisibility(View.INVISIBLE);
                        queryFromServer(cityCode);
                        //关闭抽屉
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        LogUtil.d(TAG, cityCode);
                    } else {
                        showWeather();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //当menuitem被选中时调用/** 菜单键点击的事件处理 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshWeather();
                return true;
            /*case R.id.home:
                goChoose();
                return true;*/
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
                return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
        } else {
            //安装收首次启动，city_code为空，设置默认背景图片
            background.setBackgroundResource(R.drawable.day);
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

    /**
     * 抽屉菜单的监听
     */
    private class CitySelectDrawerListener implements DrawerLayout.DrawerListener {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mActionBarDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            mActionBarDrawerToggle.onDrawerOpened(drawerView);

        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mActionBarDrawerToggle.onDrawerClosed(drawerView);
            //每次打开抽屉，初始化datalist为省级数据
            queryProvinces();
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            mActionBarDrawerToggle.onDrawerStateChanged(newState);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //该方法会自动和actionBar关联, 将开关的图片显示在了action上，如果不设置，也可以有抽屉的效果，不过是默认的图标
        mActionBarDrawerToggle.syncState();
    }


}
