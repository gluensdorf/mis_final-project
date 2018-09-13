package com.example.darlokh.test_smartwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.darlokh.test_smartwatch.MainActivity.landmarkData;

public class MyView extends View
{
    Paint paint = new Paint();
    Paint waterBodiesPaint = new Paint();
    Paint towerPaint = new Paint();
    Paint natureMarks = new Paint();
    Paint electricityMarks = new Paint();
    Paint myLocationPaint = new Paint();
    Canvas canvas = new Canvas();

    public int x,y,rad, ls;
    public float turnDegrees;
    private String TAG = "MyViewWatch";
    private JSONArray jsonArray = null;
    private JSONObject jsonObject = null;
    float xF, yF, xF2, yF2;

    public MyView(Context context)
    {
        super(context);
        x = 50;
        y = 100;
        rad = 30;
        ls = 50;

        //for if we want the circles on the screens edge
        xF = (float)(ls * Math.cos(170 * Math.PI / 180F)) + getWidth()/2;
        yF = (float)(ls * Math.sin(170 * Math.PI / 180F)) + getHeight()/2;
        xF2 = (float)(ls * Math.cos(24 * Math.PI / 180F)) + getWidth()/2;
        yF2 = (float)(ls * Math.sin(24 * Math.PI / 180F)) + getHeight()/2;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        waterBodiesPaint.setColor(Color.parseColor("#105e82"));
        paint.setColor(Color.parseColor("#CD5C5C"));
        towerPaint.setColor(Color.parseColor("#823410"));
        natureMarks.setColor(Color.parseColor("#478210"));
        electricityMarks.setColor(Color.parseColor("#acabb2"));
        myLocationPaint.setColor(Color.parseColor("#ffffff"));
    }

    @Override
    protected void onDraw(Canvas blaCanvas) {
        super.onDraw(blaCanvas);
//        drawLandmarks();
        rad = Integer.parseInt(MainActivity.landmarkData);

        canvas = blaCanvas;
        canvas.rotate(turnDegrees, this.getWidth()/2, this.getHeight()/2);
        canvas.drawCircle(getWidth()/2,30, rad, towerPaint);
//        canvas.drawCircle(x, y, rad, waterBodiesPaint);
        if (jsonArray != null) {
            double lat;
            double lng;
            String tag;
            for (int i = 0; i <= jsonArray.length() -1; i++) {
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    tag = jsonObject.get("tag").toString();
                    lat = jsonObject.getDouble("x");//Double.parseDouble(tagLatLngString[1]);
                    lng = jsonObject.getDouble("y");//Double.parseDouble(tagLatLngString[2]);
//                    Log.d(TAG, "Tag: " + tag);
//                    Log.d(TAG, "x/lat: " + lat);
//                    Log.d(TAG, "y/lng: " + lng);
                    switch (tag) {
                        case "school":
                            break;
                        case "myLocation":
                            break;
                        case "place_of_worship":
//                            Log.d(TAG, "drawLandmark: should draw an icon.");
//                            Log.d(TAG, "drawLandmark latitude: " + (int) lat);
//                            Log.d(TAG, "drawLandmark longitude: " + (int) lng);
                            canvas.drawCircle((int) lat, (int) lng, rad / 2, waterBodiesPaint);
                            break;
                        default:
                            break;
                    }
                } catch (JSONException jsonEx) {
                    jsonEx.printStackTrace();
                }
            }
        }
        canvas.drawCircle(getWidth()/2, getHeight()/2,rad/2, myLocationPaint);
        invalidate();
    }

    public void fillArray(JSONArray arr){
        jsonArray = arr;
    }
    // icons may indicate rough shape of a landmark
    // triangle => tower, something tall or "pointy"
    // square => a building, some kind of infrastructure
    // circle => misc, everything else
    //
    // colorcoding will consist out of three variants (white background)
    // red, green, blue
    public void drawLandmark(double lat, double lng, String tag){
        switch (tag){
            case "school":
                break;
            case "myLocation":
                break;
            case "place_of_worship":
//                Log.d(TAG, "drawLandmark: should draw an icon.");
//                Log.d(TAG, "drawLandmark latitude: " + (int) lat);
//                Log.d(TAG, "drawLandmark longitude: " + (int) lng);
                canvas.drawCircle((int) lat, (int) lng, rad/2, waterBodiesPaint);
                break;
            default:
                return;
            }
    }

    void setDegrees(float newDegrees) {
//        canvas.rotate(newDegrees);
        //both newDegrees and turnDegrees are working fine
        turnDegrees = newDegrees;
    }
}
