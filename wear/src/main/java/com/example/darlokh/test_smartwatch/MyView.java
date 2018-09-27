package com.example.darlokh.test_smartwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyView extends View
{
    Paint paint = new Paint();
    Paint waterBodiesMarker = new Paint();
    Paint northMarker = new Paint();
    Paint natureMarker = new Paint();
    Paint electricityMarker = new Paint();
    Paint myLocationPaint = new Paint();
    Paint historicMarker = new Paint();
    Paint buildingMarker = new Paint();
    Paint myTarget = new Paint();
    Paint myTargetInner = new Paint();
    Paint myTargetLine = new Paint();
    Paint distanceIndicatorPaint = new Paint();
    Canvas canvas = new Canvas();
    Path path = new Path();

    private float factor = 100;
    public int rad;
    public float turnDegrees;
    private String TAG = "MyViewWatch";
    private JSONArray jsonArray = null;
    private JSONObject jsonObject = null;

    public MyView(Context context)
    {
        super(context);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
//        paint.setColor(Color.parseColor("#CD5C5C"));

        myTarget.setColor(Color.GREEN);
        myTargetInner.setColor(Color.RED);
        myTargetLine.setColor(Color.BLACK);
        myTargetLine.setAntiAlias(true);
        myLocationPaint.setColor(Color.BLACK);

        northMarker.setColor(Color.RED);
        waterBodiesMarker.setColor(Color.BLUE);
        natureMarker.setColor(Color.GREEN);
        electricityMarker.setColor(Color.parseColor("#CCCC00"));
        buildingMarker.setColor(Color.parseColor("#C0C0C0"));

        distanceIndicatorPaint.setColor(Color.RED);
        distanceIndicatorPaint.setAntiAlias(true);
        distanceIndicatorPaint.setStrokeWidth(1.0f);
        distanceIndicatorPaint.setStyle(Paint.Style.STROKE);

        historicMarker.setColor(Color.RED);
        historicMarker.setStrokeWidth(3.0f);
        historicMarker.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas myCanvas) {
        // icons may indicate rough shape of a landmark
        // triangle => tower, something tall or "pointy"
        // square => a building, some kind of infrastructure
        // circle => misc, everything else
        //
        // colorcoding will consist out of five variants (white background)
        // red, green, blue, yellow, grey
        super.onDraw(myCanvas);
        float left = this.getWidth() -10;
        float top = this.getHeight() / 2 - 5;
        float right = this.getWidth() + 10;
        float bottom = this.getHeight() / 2 + 5;
        rad = 20;

        canvas = myCanvas;
        canvas.rotate(270 + turnDegrees, this.getWidth()/2, this.getHeight()/2);
        canvas.drawRect(left, top, right, bottom, northMarker);

        if (jsonArray != null) {
            int lat, lon, squareTop, squareLeft, squareBottom, squareRight;
            String tag;
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.has("factor")){
                        factor = (float) jsonObject.getDouble("factor");
                    } else {
                        tag = jsonObject.get("tag").toString();
                        lon = (int) jsonObject.getDouble("y");
                        lat = (int) jsonObject.getDouble("x");
                        switch (tag) {
                            case "myTarget":
                                canvas.drawLine(getWidth()/2, getHeight()/2, lon, lat, myTargetLine);
                                canvas.drawCircle(lon, lat, rad / 2, myTarget);
                                canvas.drawCircle(lon, lat, rad / 4, myTargetInner);
                                break;
                            case "natural":
                                canvas.drawPath(createTrianglePath(lon, lat), natureMarker);
                                break;
                            case "historic":
                                canvas.drawPath(createTrianglePath(lon, lat), historicMarker);
                                break;
                            case "man_made":
                                squareLeft = lon - rad / 2;
                                squareTop = lat - rad / 2;
                                squareRight = lon + rad / 2;
                                squareBottom = lat + rad / 2;
                                canvas.drawRect(squareLeft, squareTop, squareRight, squareBottom, electricityMarker);
                                break;
                            case "waterway":
                                canvas.drawCircle(lon, lat, rad / 2, waterBodiesMarker);
                                break;
                            case "building":
                                squareLeft = lon - rad / 2;
                                squareTop = lat - rad / 2;
                                squareRight = lon + rad / 2;
                                squareBottom = lat + rad / 2;
                                canvas.drawRect(squareLeft, squareTop, squareRight, squareBottom, buildingMarker);
                                break;
                            default:
                                break;
                        }
                    }
                } catch (JSONException jsonEx) {
                    jsonEx.printStackTrace();
                }
            }
        }
        for (int i = 1; i <= (int) ((getWidth()/2)/factor); i++){
            canvas.drawCircle(getWidth()/2, getHeight()/2, i*factor, distanceIndicatorPaint);
        }
        canvas.drawCircle(getWidth()/2, getHeight()/2, rad / 2.5f, myLocationPaint);
        invalidate();
    }

    public void setFactor(float newFactor){
        factor = newFactor;
    }

    public void fillArray(JSONArray arr){
        jsonArray = arr;
    }

    private Path createTrianglePath(int centerX, int centerY){
        // to get a triangle from one given point (given point is the center)
        // https://stackoverflow.com/questions/11449856/draw-a-equilateral-triangle-given-the-center
        int ax, ay, bx, by, cx, cy;

        ax = (int) (centerX - 0.33*rad);
        ay = (int) (centerY - -0.5*rad);
        bx = (int) (centerX - 0.33*rad);
        by = (int) (centerY - 0.5*rad);
        cx = (int) (centerX - -0.66*rad);
        cy = centerY;

        path.reset();
        path.moveTo(cx, cy);
        path.lineTo(ax, ay);
        path.lineTo(bx, by);
        path.close();

        return path;
    }

    void setDegrees(float newDegrees) {
        turnDegrees = newDegrees;
    }
}
