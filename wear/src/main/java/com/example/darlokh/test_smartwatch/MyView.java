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
    Paint waterBodiesPaint = new Paint();
    Paint towerPaint = new Paint();
    Paint natureMarks = new Paint();
    Paint electricityMarks = new Paint();
    Canvas canvas = new Canvas();

    public int x,y,rad, ls;
    public float turnDegrees;

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
    }

    @Override
    protected void onDraw(Canvas blaCanvas) {
        super.onDraw(blaCanvas);
        drawLandmarks();
        rad = Integer.parseInt(MainActivity.landmarkData);

        canvas = blaCanvas;
        canvas.rotate(turnDegrees, this.getWidth()/2, this.getHeight()/2);
        canvas.drawCircle(x, y, rad, waterBodiesPaint);

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
