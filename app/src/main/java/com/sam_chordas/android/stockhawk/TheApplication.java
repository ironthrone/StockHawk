package com.sam_chordas.android.stockhawk;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/6/8.
 */
public class TheApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
