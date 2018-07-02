package com.example.darlokh.test_smartwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends WearableActivity implements SensorEventListener {

    public SensorManager mSensorManager;
    public Sensor mGravitySensor;
    public Sensor mMagnetometer;
    public Sensor mAccelerometer;

    public float[] mGravity;
    public float[] mMagneticRotationData;
    private float azimutInDegrees;

    private int mAzimuth = 0;

    private MyView circleMyView;//= new MyView(this.getApplicationContext());

    private static final String jsonLandmarkData = "/landmarkData";
    private JSONArray jsonArray;
    public JSONObject jsonObject;
    private String tagLatLngString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataLayerListenerService foobar = new DataLayerListenerService();


        circleMyView = new MyView(this.getApplicationContext());
        setContentView(circleMyView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        dataBroadcastReceiver dataReceiver = new dataBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, messageFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "onResume: onResume");
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

//        Wearable.getDataClient(this).addListener(this);
    }


    @Override
    protected void onPause(){
        super.onPause();
        Log.d("onPause", "onPause: onPause");
        mSensorManager.unregisterListener(this);
//        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    //as little action as possible within this function
    public void onSensorChanged(SensorEvent event) {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            mMagneticRotationData = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            mGravity = event.values;
        }

        if (mGravity != null && mMagneticRotationData != null) {
            sensorAction();
        }
    }

    public void sensorAction() {
        float R[] = new float[9];
        float I[] = new float[9];

        boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mMagneticRotationData);
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);

            mAzimuth = (int) (Math.toDegrees( SensorManager.getOrientation( R, orientation) [0]) + 360) %360;
//            Log.d("data", "sensorAction: AZi" + " " + mAzimuth);

            circleMyView.setDegrees(mAzimuth);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STOP", "onStop: STOP");
    }


    public class dataBroadcastReceiver extends BroadcastReceiver {

        public String landmarkData;

        @Override
        public void onReceive(Context context, Intent intent) {
            landmarkData = intent.getStringExtra("data");
            Log.d("log", "onReceive: something" + landmarkData);
            doSomething(landmarkData);
            loadLandmarksData(landmarkData);
        }
    }

    private void doSomething(String data) {
        try {
            jsonObject = new JSONObject(data);
           String jsonID = jsonObject.get("id").toString();
//            jsonArray = jsonObject.getJSONArray("locations");
//            System.out.println(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void loadLandmarksData(String landmarkData){
        //iterate over string to split them

        for(int i = 0; i < landmarkData.length(); i++){
            String [] tagLatLngString = landmarkData.toString().split(", "); //what is the seperation symbol
            //should we trim the date to remove leerzeichen?
                String tag = tagLatLngString[0];
                Double lat = Double.parseDouble(tagLatLngString[1]);
                Double lng = Double.parseDouble(tagLatLngString[2]);
//                MyView.drawCircle(lat, lng);
        }
    }
}


