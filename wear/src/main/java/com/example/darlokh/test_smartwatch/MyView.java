package com.example.darlokh.test_smartwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class MyView extends View
{
    Paint paint = new Paint();
    Paint waterBodiesPaint = new Paint();
    Paint towerPaint = new Paint();
    Paint natureMarks = new Paint();
    Paint electricityMarks = new Paint();
    Canvas canvas;
    public int x,y,rad, ls;
    public float turnDegrees;

    float xF, yF, xF2, yF2;

    public MyView(Context context)
    {
        super(context);
//        paint = ;
//        canvas = new Canvas();
        x = 50;
        y = 100;
        rad = 20;
        ls = 50;

        float xF = (float)(ls * Math.cos(170 * Math.PI / 180F)) + getWidth()/2;
        float yF = (float)(ls * Math.sin(170 * Math.PI / 180F)) + getHeight()/2;
        float xF2 = (float)(ls * Math.cos(24 * Math.PI / 180F)) + getWidth()/2;
        float yF2 = (float)(ls * Math.sin(24 * Math.PI / 180F)) + getHeight()/2;

        Log.d("tag", "MyView: xF" + xF);
        Log.d("tag", "MyView: yF" + yF);
        Log.d("tag", "MyView: xF2" + xF2);
        Log.d("tag", "MyView: yF2" + yF2);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        waterBodiesPaint.setColor(Color.parseColor("#105e82"));
        towerPaint.setColor(Color.parseColor("#823410"));
        natureMarks.setColor(Color.parseColor("#478210"));
        electricityMarks.setColor(Color.parseColor("#acabb2"));
    }

    @Override
    protected void onDraw(Canvas blaCanvas) {
        super.onDraw(blaCanvas);
        canvas = blaCanvas;
        canvas.drawPaint(paint);
//        canvas.rotate(turnDegrees, this.getWidth()/2, this.getHeight()/2);
        canvas.drawCircle(canvas.getWidth()/2+xF, canvas.getHeight()/2+yF, rad, towerPaint);
        canvas.drawCircle(xF2, yF2, rad, waterBodiesPaint);


        invalidate();
    }


    void setDegrees(float newDegrees) {
//        canvas.rotate(newDegrees);
        //both newDegrees and turnDegrees are working fine
        turnDegrees = newDegrees;
    }
}
