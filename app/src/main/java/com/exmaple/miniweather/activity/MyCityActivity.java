package com.exmaple.miniweather.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.exmaple.miniweather.R;
import com.exmaple.miniweather.db.MiniWeatherDB;
import com.exmaple.miniweather.model.County;

import java.util.ArrayList;
import java.util.List;

public class MyCityActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;
    private MiniWeatherDB miniWeatherDB;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<String>();
    private Button addCity;
    private Button clearCity;

    private List<County> countyList;
    private County selectedCounty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_city);
        miniWeatherDB=MiniWeatherDB.getInstance(this);
        listView=(ListView) findViewById(R.id.list_view);
        countyList=miniWeatherDB.loadMyCity();
        dataList.clear();
        for (County c:countyList) {
            dataList.add(c.getCountyName());
        }
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        addCity=(Button) findViewById(R.id.add_city);
        addCity.setOnClickListener(this);

        clearCity=(Button) findViewById(R.id.clear_city);
        clearCity.setOnClickListener(this);

        String newCity=getIntent().getStringExtra("new_city");
        if(!TextUtils.isEmpty(newCity)){
            //返回需要新添加的城市,更新
            miniWeatherDB.saveMyCity(newCity);
            countyList=miniWeatherDB.loadMyCity();
            dataList.clear();
            for (County c:countyList) {
                dataList.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }

        //从我的城市列表直接跳转到该城市天气界面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCounty = countyList.get(position);
                String countyCode = selectedCounty.getCountyCode();
                Intent intent = new Intent(MyCityActivity.this, WeatherActivity.class);
                intent.putExtra("county_code", countyCode);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.add_city:
                Intent intent = new Intent(MyCityActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_my_city_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.clear_city:
                AlertDialog.Builder dialog=new AlertDialog.Builder(MyCityActivity.this);
                dialog.setMessage("确认要清空列表吗?");
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        miniWeatherDB.clearMyCity();
                        countyList.clear();
                        dataList.clear();
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent(this,WeatherActivity.class);
        startActivity(intent);
        finish();
    }
}
