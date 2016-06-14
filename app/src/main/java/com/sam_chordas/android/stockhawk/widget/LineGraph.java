package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.BuildConfig;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/5/26.
 */
public class LineGraph extends View {

    private Paint mPaint;
        DecimalFormat mFormat = new DecimalFormat("#.00");
    private double leftWidth;
private List<Double> mPoints = new ArrayList<>();

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
        leftWidth = getWidth()/6;
        drawBorder(canvas);
        drawDividingLine(canvas);
        drawPriceIndicator(canvas);
        drawLines(canvas);
    }

    private void drawBorder(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        int paintWidth = 1;
        mPaint.setStrokeWidth(paintWidth);
        canvas.drawLine(0,0,0,getHeight(),mPaint);
        canvas.drawLine(0,getHeight()-paintWidth,getWidth(),getHeight()-paintWidth,mPaint);
        canvas.drawLine(getWidth(),getWidth(),getWidth(),0,mPaint);
        canvas.drawLine(getWidth(),0,0,0,mPaint);
    }

    private void drawLines(Canvas canvas) {
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(3);
        for (int i = 0; i < mPoints.size() - 1; i++) {
            Double curr = mPoints.get(i);
            Double next = mPoints.get(i + 1);

        canvas.drawLine(toX(i),toY(curr),toX(i + 1),toY(next),mPaint);

        }
    }

    private  int POINT_NUM = 10;
    private float toX(int position){
         float result = getWidth()*position/POINT_NUM;

        if(BuildConfig.DEBUG){

        System.out.println("x: " +  result);
        }
        return result;

    }

    private double mMiddlePrice = 0 ;
    private double mDeviation = 0 ;

    private float toY(double price){
        double part = getHeight()/(2* mDeviation);
        float result =  (float)( - (price - mMiddlePrice) * part + getHeight()/2);
        if(BuildConfig.DEBUG){
        System.out.println("y: " + result);

        }
        return result;
    }

    private void drawPriceIndicator(Canvas canvas) {
        mPaint.setColor(Color.RED);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,getResources().getDisplayMetrics());
        mPaint.setTextSize(textSize);
        float part2 = getHeight()/4;
        canvas.drawText(mFormat.format(mMax),0,0 + textSize,mPaint);
        canvas.drawText(mFormat.format((mMax + mMiddlePrice)/2),0, part2,mPaint);
        canvas.drawText(mFormat.format(mMiddlePrice),0, 2 * part2,mPaint);
        canvas.drawText(mFormat.format((mMiddlePrice + mMin)/2),0,3 * part2,mPaint);
        canvas.drawText(mFormat.format(mMin),0,getHeight(),mPaint);
    }

    private void drawDividingLine(Canvas canvas) {
        float part = getHeight()/4;
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(0);
        canvas.drawLine(0,part,getWidth(),part,mPaint);
        canvas.drawLine(0,2 * part,getWidth(),2 * part,mPaint);
        canvas.drawLine(0,3 * part,getWidth(),3 * part,mPaint);
    }


    private double mMax = 0;
    private double mMin = 0;


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
        invalidate();
    }


}
