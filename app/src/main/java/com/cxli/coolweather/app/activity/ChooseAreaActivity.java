package com.cxli.coolweather.app.activity;

/**
 * Created by cx.li on 2015/12/10.
 */

        import java.util.ArrayList;
        import java.util.List;

        import android.Manifest;
        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Build;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.text.TextUtils;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        init();//初始化省市县级数据到数据库中

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index,
                                    long arg3) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    queryCounties();
                }
            }
        });
        queryProvinces();  // 加载省级数据

       // Build.VERSION_CODES.M
        //Manifest
    }

    private void init() {
        String[] pro = {"北京", "上海", "天津", "重庆", "黑龙江", "吉林", "辽宁",
                "内蒙古", "河北", "山西", "陕西", "山东", "新疆", "西藏", "青海", "甘肃",
                "宁夏", "河南", "江苏", "湖北", "浙江", "安徽", "福建", "江西", "湖南",
                "贵州", "四川", "广东", "云南", "广西", "海南", "香港", "澳门", "台湾"};

        Province p = new Province();
        for (int i = 0; i < pro.length; i++) {
            p.setProvinceName(pro[i]);
            if (i < 10) {
            p.setProvinceCode("CN0" + (i++));
            } else {
                p.setProvinceName("CN" + i);
            }
            coolWeatherDB.saveProvince(p);
        }

        String[] city = {"北京", "上海", "天津", "重庆", "哈尔滨", "齐齐哈尔", "牡丹江",
                "佳木斯", "绥化", "黑河", "大兴安岭", "伊春", "大庆", "七台河", "鸡西", "鹤岗",
                "双鸭山", "长春", "吉林", "延边", "四平", "通化", "白城", "辽源", "松原", "白山",
                "沈阳", "大连", "鞍山", "抚顺", "本溪", "丹东", "锦州", "营口", "阜新", "辽阳",
                "铁岭", "朝阳", "盘锦", "葫芦岛", "呼和浩特", "包头", "乌海", "乌兰察布", "通辽",
                "兴安盟", "赤峰", "鄂尔多斯", "巴彦淖尔", "锡林郭勒", "呼伦贝尔", "阿拉善盟", "石家庄",
                "保定", "张家口", "承德", "唐山", "廊坊", "沧州", "衡水", "邢台", "邯郸", "秦皇岛",};


        City ci = new City();
        for (int i = 0; i < city.length; i++) {
            ci.setCityName(city[i]);
            if (i < 10) {
                ci.setCityCode("CN1010" + (i++));
            } else {
                ci.setCityCode("CN101" + (i++));
            }

            coolWeatherDB.saveCity(ci);
        }

    }
    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再对数据库进行初始化。
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
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
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
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
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
     * 根据传入的代号和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(coolWeatherDB,
                            response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(coolWeatherDB,
                            response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(coolWeatherDB,
                            response, selectedCity.getId());
                }
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
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
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }

}
