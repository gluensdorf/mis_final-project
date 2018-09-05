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

    String TAG = "Compass";
    private SensorManager mSensorManager;
    private Sensor mGravitySensor;
    private Sensor mMagnetometer;
    private Sensor mRotation;
//    public Sensor mAccelerometer;
//
//    public float[] mAcceleration;
//    public float[] mMagneticRotationData;
//    private float azimutInDegrees;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    private float[] R = new float[9];
    private float[] I = new float[9];

    private float azimuth;

//    private int mAzimuth = 0;

    private MyView circleMyView;//= new MyView(this.getApplicationContext());

    private static final String jsonLandmarkData = "/landmarkData";
    public static String landmarkData = "30";
    private JSONArray jsonArray;
    public JSONObject jsonObject;
    private String tagLatLngString;
    private String idLandmarks;
    private String latLandmarks;
    private String lngLandmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataLayerListenerService foobar = new DataLayerListenerService();

        circleMyView = new MyView(this);
        setContentView(circleMyView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
//        dataBroadcastReceiver dataReceiver = new dataBroadcastReceiver();
//        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, messageFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "onResume: onResume");

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI);
//        mSensorManager.registerListener(this, mGravitySensor,
//                SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI);
//        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_GAME);
//        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI);

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
        final float alpha = 0.97f;
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                Log.d(TAG, "onSensorChanged: TYPE_ACCELEROMETER");
                mAccelerometerData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                Log.d(TAG, "onSensorChanged: TYPE_MAGNETIC_FIELD");
                mMagnetometerData = event.values.clone();
            default:
                return;
        }
        Log.d(TAG, "onSensorChanged: " + event.sensor.getName());

//        synchronized (this) {
//            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
//                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
//                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
////                Log.d(TAG, "onSensorChanged0: " + Float.toString(event.values[0]));
////                Log.d(TAG, "onSensorChanged1: " + Float.toString(event.values[1]));
////                Log.d(TAG, "onSensorChanged2: " + Float.toString(event.values[2]));
//            }
//            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
////            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
////                mGeomagnetic = event.values;
//                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
//                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
//                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
////                Log.d(TAG, "onSensorChanged0: " + Float.toString(event.values[0]));
////                Log.d(TAG, "onSensorChanged1: " + Float.toString(event.values[1]));
////                Log.d(TAG, "onSensorChanged2: " + Float.toString(event.values[2]));
//                // Log.e(TAG, Float.toString(event.values[0]));
//                Log.d(TAG, "onSensorChanged: " + Math.toDegrees(mGeomagnetic[1]));
//            }
//
//            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
////            Log.d(TAG, "onSensorChanged: " + success);
//            if (success) {
//                float orientation[] = new float[3];
//                SensorManager.getOrientation(R, orientation);
//                Log.d(TAG, "azimuth (rad): " + azimuth);
//                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
//                azimuth = (azimuth + 0 + 360) % 360;
//                circleMyView.setDegrees(azimuth);
//            }
//        }
    }

//    public void sensorAction() {
//        float R[] = new float[9];
//        float I[] = new float[9];
//
//        boolean success = SensorManager.getRotationMatrix(R, I, mAcceleration, mMagneticRotationData);
//
//        if (success) {
//            float orientation[] = new float[3];
//            SensorManager.getOrientation(R, orientation);
//            Log.d("azimuth ", "sensorAction: " + orientation[0]);
////            Log.d("2", "sensorAction: " + orientation[1]);
////            Log.d("3", "sensorAction: " + orientation[2]);
//            mAzimuth = (int) (Math.toDegrees(orientation[0]) + 360) % 360;
//
//            circleMyView.setDegrees(mAzimuth);
////            Log.d("Wear Log1", "sensorAction: " + Math.toDegrees(SensorManager.getOrientation(R, orientation) [0]));
////            Log.d("Wear Log2", "sensorAction: " + mAzimuth);
//        }
//    }

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
            loadLandmarksData();

        }
    }

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
            jsonObject = new JSONObject(data);
            //get first element, find tag, x and y, make them into tagString
            idLandmarks = jsonObject.get("tags").toString();
            latLandmarks = jsonObject.get("x").toString();
            lngLandmarks = jsonObject.get("y").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLandmarksData(){
        //iterate over string to split them
        for(int i = 0; i < idLandmarks.length(); i++){
           String [] tagLatLngString = landmarkData.toString().split(", "); //what is the seperation symbol
            // should we trim the date to remove leerzeichen?
            String tag = tagLatLngString[0];
            Double lat = Double.parseDouble(tagLatLngString[1]);
            Double lng = Double.parseDouble(tagLatLngString[2]);
            //                MyView.drawCircle(lat, lng);
        }
    }

}


