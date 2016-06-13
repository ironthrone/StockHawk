package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.widget.LineGraph;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String SYMBOL = "symbol";
    public static final String BID_PRICE = "bitPrice";
    private static final String DATA = "data";
    private String mSymbol;
    private double mBitPrice;
    private OkHttpClient client = new OkHttpClient();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.price_graph)
    LineGraph mLineGraph;
    @BindView(R.id.highest_price)
     TextView mHighest_TV;
    @BindView(R.id.lowest_price)
     TextView mLowest_TV;
    @BindView(R.id.average_price)
     TextView mAverage_TV;
    @BindView(R.id.symbol)
     TextView mSymbol_TV;
    @BindView(R.id.price_now)
     TextView mPriceNow_TV;
    @BindView(R.id.period)
    TextView mPeriod_TV;

                            private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    private double max;
    private double min;
    private String duration;
    private ArrayList<Double>  mDatas = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);


        initToolbar();
        duration = getDuration();
        mPeriod_TV.setText(duration);
        if(savedInstanceState == null){

        getNetData();
        }else {
            mDatas.addAll((ArrayList<Double>)savedInstanceState.get(DATA));
            showStock(mDatas);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DATA,mDatas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this,SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mSymbol = getIntent().getStringExtra(SYMBOL);
        mBitPrice = getIntent().getDoubleExtra(BID_PRICE,0);
        mSymbol_TV.setText(mSymbol);
        mPriceNow_TV.setText(String.valueOf(mBitPrice));

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(!duration.equals(getDuration())){
            getNetData();
            duration = getDuration();
            mPeriod_TV.setText(duration);
        }
    }

    private String getDuration(){
        return  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getString(R.string.pref_period_key),getString(R.string.pref_period_default));
    }
    private void getNetData() {
        Request request = new Request.Builder()
                .url(buildUrl(mSymbol))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(!response.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                return;
                }
                parse(response.body().string());
            }
        });
    }


    private long getStartOfToday(){
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                return calendar.getTime().getTime();

    }
//    finance_charts_json_callback( { "meta" :
//        {
//            "uri" :"/instrument/1.0/fb/chartdata;type=close;range=1m/json",
//                "ticker" : "fb",
//                "Company-Name" : "Facebook, Inc.",
//                "Exchange-Name" : "NMS",
//                "unit" : "DAY",
//                "timestamp" : "",
//                "first-trade" : "20120518",
//                "last-trade" : "20160610",
//                "currency" : "USD",
//                "previous_close_price" : 120.2800
//        }
//        ,
//        "Date" : {"min" :20160513,"max" :20160610 }
//        ,
//        "labels" : [20160513,20160516,20160523,20160531,20160606 ]
//        ,
//        "ranges" : {"close" : {"min" :115.9700,"max" :119.8100 } }
//        ,
//        "series" : [
//        { "Date" :20160513,"close" :119.8100 }
//        , { "Date" :20160516,"close" :118.6700 }
//        , { "Date" :20160517,"close" :117.3500 }
//        , { "Date" :20160518,"close" :117.6500 }
//        , { "Date" :20160519,"close" :116.8100 }
//        , { "Date" :20160520,"close" :117.3500 }
//        , { "Date" :20160523,"close" :115.9700 }
//        , { "Date" :20160524,"close" :117.7000 }
//        , { "Date" :20160525,"close" :117.8900 }
//        , { "Date" :20160526,"close" :119.4700 }
//        , { "Date" :20160527,"close" :119.3800 }
//        , { "Date" :20160531,"close" :118.8100 }
//        , { "Date" :20160601,"close" :118.7800 }
//        , { "Date" :20160602,"close" :118.9300 }
//        , { "Date" :20160603,"close" :118.4700 }
//        , { "Date" :20160606,"close" :118.7900 }
//        , { "Date" :20160607,"close" :117.7600 }
//        , { "Date" :20160608,"close" :118.3900 }
//        , { "Date" :20160609,"close" :118.5600 }
//        , { "Date" :20160610,"close" :116.6200 }
//
//        ]
//    } )

    private void parse(String result){
            StringBuilder sb = new StringBuilder(result);
        String begin = "finance_charts_json_callback( ";
            sb.deleteCharAt(result.length() -1);
            sb.delete(0,begin.length() - 1);
        String json = sb.toString();
                    try {
                        JSONObject root_Json = new JSONObject(json);

                        JSONArray jsonSeries = root_Json.getJSONArray("series");
                        JSONObject jsonMinMax = root_Json.getJSONObject("ranges").getJSONObject("close");
                        min = jsonMinMax.getDouble("min");
                        max = jsonMinMax.getDouble("max");
                        for (int i = 0; i < jsonSeries.length(); i++) {
                            JSONObject object = jsonSeries.getJSONObject(i);
                            mDatas.add(object.getDouble("close"));
                        }
                        showStock(mDatas);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

    }

    private void showStock(final List<Double> datas){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        mLineGraph.setDatas(datas);
                mHighest_TV.setText(String.format("%.2f",getHighestPrice(datas)));
                mLowest_TV.setText(String.format("%.2f",getLowestPrice(datas)));
                mAverage_TV.setText(String.format("%.2f",getAveragePrice(datas)));

            }
        });
    }

    private double getHighestPrice(List<Double> list){
        Collections.sort(list,Collections.<Double>reverseOrder());
        return list.get(0);
    }

    private double getLowestPrice(List<Double> list){
        Collections.sort(list);
        return list.get(0);
    }

    private double getAveragePrice(List<Double> list){
        int num = list.size();
        double total = 0;
        for (Double dou : list) {
            total += dou;
        }
        return total/num;
    }

    /*
    http://chartapi.finance.yahoo.com/instrument/1.0/fb/chartdata;type=close;range=7d/json
    */
    private String buildUrl(String symbol){

        StringBuilder sb = new StringBuilder();
            sb.append("http://chartapi.finance.yahoo.com/instrument/1.0/" + symbol + "/chartdata;type=close;range=" + duration + "/json");
        return sb.toString();
    }

    private String getStartTime(){
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH,-90);
        Date before90Date = calendar.getTime();
//        long current = date.getTime();
//        long nintyDays =  90 * 24 * 60 * 60 * 1000L;
//        long before90Days  = current - nintyDays;
//        Date before90Date = new Date(before90Days);
        return formatDate(before90Date);
    }

    private String formatDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
