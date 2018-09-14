package com.example.darlokh.test_smartwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends WearableActivity implements SensorEventListener, DataClient.OnDataChangedListener {

    String TAG = "Compass";
    private SensorManager mSensorManager;
    private Sensor mMagnetometer;

    private float[] orientation = new float[3];
    private float[] rMat = new float[9];
    private int mAzimuth = 0; //degree

    private MyView circleMyView;//= new MyView(this.getApplicationContext());

    private static final String LANDMARKDATA_KEY = "com.example.key.landmarkdata";
    private static final String jsonLandmarkData = "/landmarkData";
    public static String landmarkData = "30";
    private JSONArray jsonArray;
    public JSONObject jsonObject;
//    private String tagLatLngString;
    private String idLandmarks;
    private String latLandmarks;
    private String lngLandmarks;
    private String STATE_LANDMARKS = "landmarkJSONArray";
//    private DataLayerListenerService mDataLayerListener;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents){
        Log.d(TAG, "onDataChanged: ABCD");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            Log.d(TAG, "onDataChanged: " + path);
            if(jsonLandmarkData.equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                Log.d("DataLayerListener", "onDataChanged: " + "FOOBAR");
                String stringExample = map.getString(LANDMARKDATA_KEY);
//                MainActivity.landmarkData = "50";
                System.out.println(stringExample);
                doSomething(stringExample);

//                Intent dataIntent = new Intent();
//                dataIntent.setAction(Intent.ACTION_SEND);
//                dataIntent.putExtra("data", stringExample);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(dataIntent);
            }
        }

//        for (DataEvent event : dataEvents) {
//            if (event.getType() == DataEvent.TYPE_CHANGED) {
//                // DataItem changed
//                DataItem item = event.getDataItem();
//                if (item.getUri().getPath().compareTo(jsonLandmarkData) == 0) {
//                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
//                    Log.d(TAG, "onDataChanged: " + dataMap.getString(jsonLandmarkData));
//                }
//            } else if (event.getType() == DataEvent.TYPE_DELETED){
//                // DataItem deleted
//            }
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mDataLayerListener = new DataLayerListenerService();

        circleMyView = new MyView(this);
        setContentView(circleMyView);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        DataBroadcastReceiver dataReceiver = new DataBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, messageFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("onResume", "onResume: onResume");
        mSensorManager.registerListener(this, mMagnetometer,
                100000); // 100.000 micro seconds = 0.1 seconds
//                SensorManager.SENSOR_DELAY_NORMAL);
        Wearable.getDataClient(this).addListener(this);
//        if (jsonArray != null) {
//            circleMyView.fillArray(jsonArray);
//        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("onPause", "onPause: onPause");
        mSensorManager.unregisterListener(this);
        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    //as little action as possible within this function
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
//            Log.d(TAG, "onSensorChanged: " + mAzimuth);
            circleMyView.setDegrees(-mAzimuth);
        } else {
            return;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STOP", "onStop: STOP");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save current landmarks
        savedInstanceState.putString(STATE_LANDMARKS, jsonArray.toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        String tmpString = savedInstanceState.getString(STATE_LANDMARKS);
        try {
            JSONArray tmpJSONArr = new JSONArray(tmpString);
            circleMyView.fillArray(tmpJSONArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//    public class DataBroadcastReceiver extends BroadcastReceiver {
//
//        public String landmarkData;
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "onReceive: ");
//            landmarkData = intent.getStringExtra("data");
////            Log.d("log", "onReceive: something" + landmarkData);
//            doSomething(landmarkData);
//            loadLandmarksData();
//
//        }
//    }

    // TODO: implementing the following pseudocode
    // add new attribute landmarkList
    // iterate over the JSONObject
    // read landmark per landmark out
    // add new landmark with their data to the landmarkList
    // in myView iterate over landmarkList with a new function
    // the new function reads the tag, decides which form the landmark will have by the name
    // and uses the coordinates from the landmark in its form accordingly
    // then it draws the landmark (this is done for every landmark)
    //
    // amount of landmarks which will be drawn can be adjusted because the landmarks
    // are ordered by their distance to myLocation - the iteration can stop when the cap is reached
    //
    private void doSomething(String data) {
        try {
            Log.d(TAG, "doSomething data-length: " + data);
            if (data != null) {
                jsonArray = new JSONArray(data);
                jsonObject = jsonArray.getJSONObject(0);
                //get first element, find tag, x and y, make them into tagString
                idLandmarks = jsonObject.get("tag").toString();
                latLandmarks = jsonObject.get("x").toString();
                lngLandmarks = jsonObject.get("y").toString();
                loadLandmarksData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLandmarksData(){
        try {
            Log.d(TAG, "loadLandmarksData: jsonArray length = " + jsonArray.length());
            //iterate over string to split them
            for (int i = 1; i < jsonArray.length(); i++) {
//                String[] tagLatLngString = landmarkData.toString().split(", "); //what is the seperation symbol
                // should we trim the date to remove leerzeichen?
//                try {
                    circleMyView.fillArray(jsonArray);
//                    jsonObject = jsonArray.getJSONObject(i);
//                    String tag = jsonObject.get("tag").toString();
//                    Double lat = jsonObject.getDouble("x");//Double.parseDouble(tagLatLngString[1]);
//                    Double lng = jsonObject.getDouble("y");//Double.parseDouble(tagLatLngString[2]);
//                    Log.d(TAG, "Tag: " + tag);
//                    Log.d(TAG, "x/lat: " + lat);
//                    Log.d(TAG, "y/lng: " + lng);
//
//                    circleMyView.drawLandmark(lat, lng, tag);
                    //                MyView.drawCircle(lat, lng);
//                } catch (JSONException jsonEx) {
//                    jsonEx.printStackTrace();
//                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}


