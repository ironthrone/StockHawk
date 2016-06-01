package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/5/26.
 */
public class LineGraph extends View {

    private Paint mPaint;

    public LineGraph(Context context) {
        super(context);
        init();
    }

    public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
        drawDividingLine(canvas);
        drawPriceIndicator(canvas);
        drawLines(canvas);
    }

    private void drawBorder(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(0,0,0,getHeight(),mPaint);
        canvas.drawLine(0,getHeight(),getWidth(),getHeight(),mPaint);
        canvas.drawLine(getWidth(),getWidth(),getWidth(),0,mPaint);
        canvas.drawLine(getWidth(),0,0,0,mPaint);
    }

    private List<Double> mPoints = new ArrayList<>();
    private void drawLines(Canvas canvas) {
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(3);
        for (int i = 0; i < mPoints.size() - 1; i++) {
            Double curr = mPoints.get(i);
            Double next = mPoints.get(i + 1);

        canvas.drawLine(toX(i),toY(curr),toX(i + 1),toY(next),mPaint);

        }
    }

//    private static final int TOTAL_TIME = 4 * 60 * 1000 * 1000;
    private  int POINT_NUM = 10;
    private float toX(int position){
         float result = getWidth()*position/POINT_NUM;

        System.out.println("x: " +  result);
        return result;

    }

    private double mMiddlePrice = 0 ;
    private double mDeviation = 0 ;

    private float toY(double price){
        double part = getHeight()/(2* mDeviation);
        float result =  (float)( - (price - mMiddlePrice) * part + getHeight()/2);
        System.out.println("y: " + result);
        return result;
    }

    private void drawPriceIndicator(Canvas canvas) {
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(48);
        double part = (mDeviation)/2;
        float part2 = getHeight()/4;
        canvas.drawText(mMax + "",0,0,mPaint);
        canvas.drawText((mMax + mMiddlePrice)/2 + "",0, part2,mPaint);
        canvas.drawText(mMiddlePrice + "",0, 2 * part2,mPaint);
        canvas.drawText((mMiddlePrice + mMin)/2 + "",0,3 * part2,mPaint);
        canvas.drawText(mMin + "",0,getHeight(),mPaint);
    }

    private void drawDividingLine(Canvas canvas) {
        float part = getHeight()/4;
        mPaint.setColor(Color.RED);
        canvas.drawLine(0,part,getWidth(),part,mPaint);
        canvas.drawLine(0,2 * part,getWidth(),2 * part,mPaint);
        canvas.drawLine(0,3 * part,getWidth(),3 * part,mPaint);
    }


    private double mMax = 0;
    private double mMin = 0;



//    public void addData(Data data){
//            mPoints.add(data);
//        mMax = data.price > mMax ? data.price : mMax;
//        mMin = data.price < mMin ? data.price : mMin;
//        mDeviation = mMax - mMiddlePrice;
//        setDeviation(mMax,mMin);
//    }

    private void setDeviation(double max, double min){
        mDeviation = Math.max(Math.abs(mMax - mMiddlePrice),Math.abs(mMin - mMiddlePrice));

    }

    public void setDatas(List<Double> datas){
        if(datas.size() == 0) return;
        POINT_NUM = datas.size();

        mPoints.clear();
        mPoints.addAll(datas);


        List<Double> temp = new ArrayList<>(datas);
        Collections.sort(temp);

        mMin = temp.get(0);
        mMax = temp.get(temp.size() - 1);
        mMiddlePrice = (mMax + mMin )/2;
        setDeviation(mMax,mMin);
    }


}
