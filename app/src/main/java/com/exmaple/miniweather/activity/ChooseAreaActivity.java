package com.exmaple.miniweather.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exmaple.miniweather.R;
import com.exmaple.miniweather.model.City;
import com.exmaple.miniweather.model.County;
import com.exmaple.miniweather.db.MiniWeatherDB;
import com.exmaple.miniweather.model.Province;
import com.exmaple.miniweather.util.HttpCallBackListener;
import com.exmaple.miniweather.util.HttpUtil;
import com.exmaple.miniweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {

    private static final int PROVINCE_LEVEL=0;
    private static final int CITY_LEVEL=1;
    private static final int COUNTY_LEVEL=2;
    private int CURRENT_LEVEL=PROVINCE_LEVEL;

    private ListView listView;
    private TextView textView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;
    private MiniWeatherDB miniWeatherDB;
    private ProgressDialog progressDialog;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Boolean isCitySelected;
    private Boolean isFromWeatherActivity;
    private Boolean isFromMyCityActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences=getSharedPreferences("data", MODE_PRIVATE);
        isCitySelected=preferences.getBoolean("city_selected", false);
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity", false);
        isFromMyCityActivity=getIntent().getBooleanExtra("from_my_city_activity",false);

        //已选城市
        if(isCitySelected && !isFromWeatherActivity && !isFromMyCityActivity){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        //未选城市
        if(!isCitySelected){
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setMessage("请选择你的城市");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
        }

        miniWeatherDB=MiniWeatherDB.getInstance(this);
        textView=(TextView) findViewById(R.id.title_text);
        listView=(ListView) findViewById(R.id.list_view);
        dataList=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (CURRENT_LEVEL == PROVINCE_LEVEL) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (CURRENT_LEVEL == CITY_LEVEL) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (CURRENT_LEVEL == COUNTY_LEVEL) {
                    selectedCounty = countyList.get(position);
                    if (isFromMyCityActivity) {
                        //添加到我的城市列表
                        String newCity = selectedCounty.getCountyCode() + selectedCounty.getCountyName();
                        Intent intent = new Intent(ChooseAreaActivity.this, MyCityActivity.class);
                        intent.putExtra("new_city", newCity);
                        startActivity(intent);
                    } else {
                        //普通选城市
                        if(!isCitySelected){
                            //将初选的默认添加到我的城市列表
                            miniWeatherDB.saveMyCity(selectedCounty.getCountyCode()+selectedCounty.getCountyName());
                        }
                        String countyCode = selectedCounty.getCountyCode();
                        Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                        intent.putExtra("county_code", countyCode);
                        startActivity(intent);
                    }
                    finish();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国的省份,先查数据库,没有的话去服务器查
     */
    public void queryProvinces(){
        provinceList=miniWeatherDB.loadProvince();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province p:provinceList){
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            CURRENT_LEVEL=PROVINCE_LEVEL;
            textView.setText("中国");
        }else{
            queryFromServer(null,"province");
        }
    }

    /**
     * 查询选中省份内的城市,先查数据库,没有的话去服务器查
     */
    public void queryCities(){
        cityList=miniWeatherDB.loadCity(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City c:cityList){
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            CURRENT_LEVEL=CITY_LEVEL;
            textView.setText(selectedProvince.getProvinceName());
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    /**
     * 查询选中城市内的区县,先查数据库,没有的话去服务器查
     */
    public void queryCounties(){
        countyList=miniWeatherDB.loadCounty((selectedCity.getId()));
        if(countyList.size()>0){
            dataList.clear();
            for (County c:countyList){
                dataList.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();;
            listView.setSelection(0);
            CURRENT_LEVEL=COUNTY_LEVEL;
            textView.setText(selectedCity.getCityName());
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    /**
     * 根据传入的代号和类型查询省市县数据
     */
    public void queryFromServer(final String code, final String type) {
        String address = new String();
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if(type.equals("province")){
                    result=Utility.handleProvinceResponse(miniWeatherDB,response);
                }else if(type.equals("city")){
                    result=Utility.handleCityResponse(miniWeatherDB, response, selectedProvince.getId());
                }else if(type.equals("county")){
                    result=Utility.handleCountyResponse(miniWeatherDB,response,selectedCity.getId());
                }
                if(result){
                    //回到主线程处理UI逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(type.equals("province")){
                                queryProvinces();
                            }else if(type.equals("city")){
                                queryCities();
                            }else if(type.equals("county")){
                                queryCounties();
                            }
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
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /**
     * 判断返回界面
     */
    @Override
    public void onBackPressed(){
        if(CURRENT_LEVEL==COUNTY_LEVEL){
            queryCities();
        }else if(CURRENT_LEVEL==CITY_LEVEL){
            queryProvinces();
        }else if(CURRENT_LEVEL==PROVINCE_LEVEL){
            if(isFromWeatherActivity){
                Intent intent=new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }else if(isFromMyCityActivity){
                Intent intent=new Intent(this,MyCityActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

}
