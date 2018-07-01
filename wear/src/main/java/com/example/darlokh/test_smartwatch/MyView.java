package com.example.darlokh.test_smartwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import static com.example.darlokh.test_smartwatch.MainActivity.landmarkData;

public class MyView extends View
{
    Paint paint = new Paint();
    Canvas canvas;
    public int x,y,rad;
    public float turnDegrees;

    public MyView(Context context)
    {
        super(context);
//        paint = ;
//        canvas = new Canvas();
        x = 50;
        y = 100;
        rad = 30;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
//        canvas.drawPaint(paint);
        paint.setColor(Color.parseColor("#CD5C5C"));

    }

    @Override
    protected void onDraw(Canvas blaCanvas) {
        super.onDraw(blaCanvas);
        drawLandmarks();
        rad = Integer.parseInt(MainActivity.landmarkData);

        canvas = blaCanvas;
        canvas.rotate(turnDegrees, this.getWidth()/2, this.getHeight()/2);
        canvas.drawCircle(x, y, rad, paint);
        invalidate();
    }

    void drawLandmarks(){
    }

    void setDegrees(float newDegrees) {
//        canvas.rotate(newDegrees);
        //both newDegrees and turnDegrees are working fine
        turnDegrees = newDegrees;
    }
}
