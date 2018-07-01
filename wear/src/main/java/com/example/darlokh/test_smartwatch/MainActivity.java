package com.example.darlokh.test_smartwatch;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class MainActivity extends WearableActivity implements SensorEventListener { //, DataClient.OnDataChangedListener

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
    public static String landmarkData = "30";

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

//    @Override
//    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
//      for (DataEvent event : dataEventBuffer) {
//          if (event.getType() == DataEvent.TYPE_CHANGED) {
//              DataItem item = event.getDataItem();
//              if (item.getUri().getPath().compareTo("/landmarksData") == 0) {
//                  DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
////                  loadLandmarksData();
//              }
//          } else if (event.getType() == DataEvent.TYPE_DELETED) {
//              //DataItem deleted
//          }
//      }
//    }

//    private void loadLandmarksData(){
//        //iterate over string to split them
//        for(){
//            String[] tagLatLngString = ....getValue().toString().split(", ");
//            if (tagLatLngString.length == 2 || tagLatLngString == 3) {
//                String tag = tagLatLngString[0];
//                Double lat = Double.parseDouble(tagLatLngString[1]);
//                Double lng = Double.parseDouble(tagLatLngString[2]);
//
//                MyView.drawCircle(lat, lng);
//            } else {
//                ArrayList<Landmark> landmarksArray = new ArrayList<>();
//                for (int i = 0; i < tagLatLngString.length; i = i+3) {
//                    String tag = tagLatLngString[i+0];
//                    Double lat = Double.parseDouble(tagLatLngString[i+1]);
//                    Double lng = Double.parseDouble(tagLatLngString[i+2]);
//
//                    MyView.drawCircle(lat, lng);
//                }
//            }
//        }
//    }

}


