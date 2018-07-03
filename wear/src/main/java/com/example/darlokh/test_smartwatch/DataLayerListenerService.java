package com.example.darlokh.test_smartwatch;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;

import java.util.List;

// https://stackoverflow.com/questions/24894711/android-wear-watchface-settings-on-host/24896043#24896043

public class DataLayerListenerService extends WearableListenerService {
    public static String data;
    public JSONArray newJSONArray;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            if("/landmarkData".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                String stringExample = map.getString("com.example.key.landmarkdata");
                MainActivity.landmarkData = "50";
                System.out.println(stringExample);

                Intent dataIntent = new Intent();
                dataIntent.setAction(Intent.ACTION_SEND);
                dataIntent.putExtra("data", stringExample);
                LocalBroadcastManager.getInstance(this).sendBroadcast(dataIntent);
            }
        }
    }
}