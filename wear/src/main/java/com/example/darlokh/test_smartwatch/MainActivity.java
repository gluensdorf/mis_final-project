package com.example.darlokh.test_smartwatch;


import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.EventListener;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }


    @Override
    protected void onPause(){
        super.onPause();
        Log.d("onPause", "onPause: onPause");
        mSensorManager.unregisterListener(this);
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
            Log.d("data", "sensorAction: AZi" + " " + mAzimuth);

            circleMyView.setDegrees(mAzimuth);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STOP", "onStop: STOP");
    }
}