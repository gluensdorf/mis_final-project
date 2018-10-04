package com.example.darlokh.test_smartwatch;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.widget.Toast;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends WearableActivity implements SensorEventListener, DataClient.OnDataChangedListener {

    String TAG = "Compass";
    private SensorManager mSensorManager;
    private Sensor mMagnetometer;

    private float[] orientation = new float[3];
    private float[] rMat = new float[9];
    private int mAzimuth = 0; //degree

    private MyView circleMyView;

    private static final String LANDMARKDATA_KEY = "com.example.key.landmarkdata";
    private static final String jsonLandmarkData = "/landmarkData";
    private JSONArray jsonArray;
    private String STATE_LANDMARKS = "landmarkJSONArray";
    public JSONObject jsonObject;
    String idLandmarks;
    String latLandmarks;
    String lngLandmarks;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents){
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            if(jsonLandmarkData.equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                String newData = map.getString(LANDMARKDATA_KEY);
                prepareLandmarkData(newData);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAmbientEnabled();
        circleMyView = new MyView(this);
        circleMyView.setBackgroundColor(Color.WHITE);
        setContentView(circleMyView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        DataBroadcastReceiver dataReceiver = new DataBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, messageFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mMagnetometer,
                100000); // 100.000 micro seconds = 0.1 seconds
//                SensorManager.SENSOR_DELAY_NORMAL);
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
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
            circleMyView.setDegrees(-mAzimuth);
        } else {
            return;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save current landmarks
        if (jsonArray != null) {
            savedInstanceState.putString(STATE_LANDMARKS, jsonArray.toString());
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        if (savedInstanceState.getString(STATE_LANDMARKS) != null) {
            String tmpString = savedInstanceState.getString(STATE_LANDMARKS);
            try {
                JSONArray tmpJSONArr = new JSONArray(tmpString);
                circleMyView.fillArray(tmpJSONArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareLandmarkData(String data) {
        try {
            if (data != null) {
                jsonArray = new JSONArray(data);
                jsonObject = jsonArray.getJSONObject(0);
                idLandmarks = jsonObject.get("tag").toString();
                lngLandmarks = jsonObject.get("x").toString();
                latLandmarks = jsonObject.get("y").toString();
                if (jsonObject.has("factor")) {
                    circleMyView.setFactor((float) jsonObject.getDouble("factor"));
                }
                loadLandmarksData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLandmarksData(){
        try {
            for (int i = 1; i < jsonArray.length(); i++) {
                circleMyView.fillArray(jsonArray);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}


