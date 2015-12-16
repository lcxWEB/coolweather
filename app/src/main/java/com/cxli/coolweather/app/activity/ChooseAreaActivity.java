package com.cxli.coolweather.app.activity;

/**
 * Created by cx.li on 2015/12/10.
 */

        import java.util.ArrayList;
        import java.util.List;

        import android.Manifest;
        import android.app.Activity;
        import android.app.ActivityManager;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Build;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.view.Window;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.cxli.coolweather.app.R;
        import com.cxli.coolweather.app.db.CoolWeatherDB;
        import com.cxli.coolweather.app.db.CoolWeatherOpenHelper;
        import com.cxli.coolweather.app.model.City;
        import com.cxli.coolweather.app.model.County;
        import com.cxli.coolweather.app.model.Province;
        import com.cxli.coolweather.app.util.HttpCallBackListener;
        import com.cxli.coolweather.app.util.HttpUtil;
        import com.cxli.coolweather.app.util.Utility;


public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private CoolWeatherOpenHelper openHelper;
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

        isFromWeatherActivity = getIntent().getBooleanExtra("is_from_weather", false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() <= 0) {
            init();//初始化省市县级数据到数据库中
        }
        listView.setOnItemClickListener(new OnItemClickListener() {
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
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("cityCode", cityCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();  // 加载省级数据
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
                p.setProvinceCode("CN0" + (i+1));
            } else {
                p.setProvinceCode("CN" + (i+1));
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
        String[] city9 ={"石家庄", "保定", "张家口", "承德", "唐山", "廊坊", "沧州", "衡水", "邢台", "邯郸", "秦皇岛"};



        City ci = new City();
        for (int i = 0; i < city1.length; i++) {
            ci.setCityName(city1[i]);
            if (i < 9) {
                ci.setCityCode("CN101010" + (i+1) +"00");
            } else {
                ci.setCityCode("CN10101" + (i++));
            }
            ci.setProvinceId(1);
            coolWeatherDB.saveCity(ci);
        }
        for (int i = 0; i < city2.length; i++) {
            ci.setCityName(city2[i]);
            if (i < 9) {
                ci.setCityCode("CN101020" + (i+1) +"00");
            } else {
                ci.setCityCode("CN10102" + (i+1));
            }
            ci.setProvinceId(2);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city3.length; i++) {
            ci.setCityName(city3[i]);
            if (i < 9) {
                ci.setCityCode("CN101030" + (i+1) +"00");
            } else {
                ci.setCityCode("CN10103" + (i+1));
            }
            ci.setProvinceId(3);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city4.length; i++) {
            ci.setCityName(city4[i]);
            if (i < 9) {
                ci.setCityCode("CN101040" + (i+1) +"00");
            } else {
                ci.setCityCode("CN10104" + (i+1));
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
                    ci.setCityCode("CN101050" + (i+1) + "01");
                } else {
                    ci.setCityCode("CN10105" + (i+1) + "01");
                }
            }
            ci.setProvinceId(5);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city6.length; i++) {
            ci.setCityName(city6[i]);
            if (i < 9) {
                ci.setCityCode("CN101060" + (i+1) + "01");
            } else {
                ci.setCityCode("CN10106" + (i+1) + "01");
            }
            ci.setProvinceId(6);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city7.length; i++) {
            ci.setCityName(city7[i]);
            if (i < 9) {
                ci.setCityCode("CN101070" + (i+1) + "01");
            } else {
                ci.setCityCode("CN10107" + (i+1) + "01");
            }
            ci.setProvinceId(7);
            coolWeatherDB.saveCity(ci);
        }

        for (int i = 0; i < city8.length; i++) {
            ci.setCityName(city8[i]);
            if (i < 9) {
                ci.setCityCode("CN101080" + (i+1) + "01");
            } else {
                ci.setCityCode("CN10108" + (i+1) + "01");
            }
            ci.setProvinceId(8);
            coolWeatherDB.saveCity(ci);
        }
        for (int i = 0; i < city9.length; i++) {
            ci.setCityName(city9[i]);
            if (i < 9) {
                ci.setCityCode("CN101090" + (i+1) + "01");
            } else {
                ci.setCityCode("CN10109" + (i+1) + "01");
            }
            ci.setProvinceId(9);
            coolWeatherDB.saveCity(ci);
        }
    }

    /**
     * 查询全国所有的省，优先从数据库查询。
     */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
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

    /**
     * 查询选中省内所有的市，优先从数据库查询。
     */
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

    /**
     * 查询选中市内所有的县，优先从数据库查询。
     */
    private void queryCounties() {
        countyList = coolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }
    }

    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

}
