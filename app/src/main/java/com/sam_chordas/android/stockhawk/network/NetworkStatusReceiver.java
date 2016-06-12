package com.sam_chordas.android.stockhawk.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2016/6/12.
 */
public class NetworkStatusReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkStatusReceiver.class.getSimpleName();

    public NetworkStatusReceiver(UICallback uiCallback) {
        this.uiCallback = uiCallback;
    }

    private UICallback uiCallback;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,intent.getAction());
        uiCallback.call();
    }

    public interface UICallback{
        void call();
    }
}
