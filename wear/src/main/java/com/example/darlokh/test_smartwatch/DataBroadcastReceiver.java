package com.example.darlokh.test_smartwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DataBroadcastReceiver extends BroadcastReceiver {

    public String landmarkData;

    @Override
    public void onReceive(Context context, Intent intent) {
        landmarkData = intent.getStringExtra("data");
        Log.d("log", "onReceive: something" + landmarkData);

    }
}
