package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.data.StockHistoryColumn;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends Activity {

    public static final String SYMBOL = "symbol";
    public static final String BID_PRICE = "bitPrice";
    private String mSymbol;
    private double mBitPrice;
    private OkHttpClient client = new OkHttpClient();

    @BindView(R.id.price_graph) LineGraph mLineGraph;
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
                            private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);


        initToolbar();
        getNetData();
    }

    private void initToolbar() {
        mSymbol = getIntent().getStringExtra(SYMBOL);
        mBitPrice = getIntent().getDoubleExtra(BID_PRICE,0);
        mSymbol_TV.setText(mSymbol);
        mPriceNow_TV.setText(String.valueOf(mBitPrice));
    }

    private void getNetData() {
        String startDate;
        Cursor cursor = getContentResolver().query(QuoteProvider.StockHistory.CONTENT_URI,
                new String[]{StockHistoryColumn.DATE},
                StockHistoryColumn.SYMBOL + " = ?",
                new String[]{mSymbol},
                StockHistoryColumn.DATE + " desc");
        if(cursor != null && cursor.moveToFirst()){
            long lastTime = cursor.getLong(cursor.getColumnIndex(StockHistoryColumn.DATE));
            if(lastTime == getStartOfToday()){
                startDate = "";
            }else {
                startDate = mFormat.format(new Date(lastTime));
            }
        cursor.close();

        }else {
            startDate = getStartTime();
        }
        if(startDate.equals("")){
            return;
        }
        Request request = new Request.Builder()
                .url(buildUrl(mSymbol,startDate))
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
                store(response.body().string());
                showStock();
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
    /*
    {
 "query": {
  "count": 20,
  "created": "2016-05-31T13:54:27Z",
  "lang": "en-US",
  "results": {
   "quote": [
    {
     "Symbol": "YHOO",
     "Date": "2016-05-27",
     "Open": "36.880001",
     "High": "37.880001",
     "Low": "36.84",
     "Close": "37.82",
     "Volume": "14642500",
     "Adj_Close": "37.82"
    },
     */

    private void store(String json){
                    try {
                        JSONObject root_Json = new JSONObject(json);

                        JSONArray query_Array = root_Json.getJSONObject("query").getJSONObject("results").getJSONArray("quote");
                        List<ContentValues> cvs = new ArrayList<>();
                        for (int i = 0; i < query_Array.length(); i++) {
                            JSONObject object = query_Array.getJSONObject(i);
                            ContentValues cv = new ContentValues();
                            cv.put(StockHistoryColumn.SYMBOL,object.getString("Symbol"));
                            long date = mFormat.parse(object.getString("Date")).getTime();
                            cv.put(StockHistoryColumn.DATE,date);
                            cv.put(StockHistoryColumn.OPEN,object.getString("Open"));
                            cv.put(StockHistoryColumn.HIGH,object.getString("High"));
                            cv.put(StockHistoryColumn.LOW,object.getString("Low"));
                            cv.put(StockHistoryColumn.CLOSE,object.getString("Close"));
                            cv.put(StockHistoryColumn.VOLUME,object.getString("Volume"));
                            cv.put(StockHistoryColumn.ADJ_CLOSE,object.getString("Adj_Close"));
                            cvs.add(cv);
                        }
                        getContentResolver().bulkInsert(QuoteProvider.StockHistory.CONTENT_URI,cvs.toArray(new ContentValues[]{}));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

    }

    private void showStock(){
        Cursor cursor = getContentResolver().query(QuoteProvider.StockHistory.CONTENT_URI,
                new String[]{StockHistoryColumn.CLOSE,StockHistoryColumn.DATE},
                StockHistoryColumn.SYMBOL + " = ?",
                new String[]{mSymbol},
                StockHistoryColumn.DATE + " asc");
        if(cursor == null) return;
        final List<Double> prices = new ArrayList<>();
        while (cursor.moveToNext()){
            prices.add(cursor.getDouble(cursor.getColumnIndex(StockHistoryColumn.CLOSE)));
        }
        cursor.close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        mLineGraph.setDatas(prices);
                mHighest_TV.setText(String.format("%.2f",getHighestPrice(prices)));
                mLowest_TV.setText(String.format("%.2f",getLowestPrice(prices)));
                mAverage_TV.setText(String.format("%.2f",getAveragePrice(prices)));

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
    https://query.yahooapis.com/v1/public/yql?
    q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22YHOO%22%20and%20startDate%20%3D%20%222016-04-30%22%20and%20endDate%20%3D%20%222016-05-31%22&mFormat=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=
    */
    private String buildUrl(String symbol,String startDate){
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("https://query.yahooapis.com/v1/public/yql?q=");
            sb .append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = \"" + symbol +"\" and startDate = \"" + startDate + "\" and endDate = \"" + formatDate(new Date()) + "\"","UTF-8"));
            sb.append("&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
