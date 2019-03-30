package com.radioknit.mminewapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by nishant on 25/4/17.
 */

public class DrawLine extends View {

    Paint paint = new Paint();
    Paint paint1 = new Paint();
    Paint paint2 = new Paint();
    Paint paint3 = new Paint();
    Paint paint4 = new Paint();

    Paint textPaint = new Paint();
    Paint txtPaintBlue = new Paint();
    public DrawLine(Context context , int color1, int color2, int color3, int color4, int color5) {
        super(context);

        paint.setColor(color5);
        paint1.setColor(color4);
        paint2.setColor(color3);
        paint3.setColor(color2);
        paint4.setColor(color1);

        textPaint.setColor(Color.RED);
        txtPaintBlue.setColor(Color.BLUE);
//        paint.setStrokeWidth(2.0f);
    }
    @Override
    public void onDraw(Canvas canvas) {

        canvas.drawCircle(20,20,5,paint);
        canvas.drawLine(20, 20, 70, 20, paint);
        canvas.drawCircle(70,20,5,paint);
        canvas.drawCircle(80,20,5,paint);
        canvas.drawText("SPP",60,10,txtPaintBlue);
        canvas.drawLine(80, 20, 120, 20, paint);
        canvas.drawCircle(120,20,5,paint);
        canvas.drawCircle(130,20,5,paint);
        canvas.drawText("Drive FLT",110,10,txtPaintBlue);
        canvas.drawLine(130,20,230,20,paint);
        canvas.drawLine(230,20,230,40,paint);
        canvas.drawLine(50,40,230,40,paint);
        canvas.drawLine(50,40,50,60,paint);
        canvas.drawCircle(20,60,5,paint);
        canvas.drawText("SFTOP1",5,50,textPaint);
        canvas.drawLine(20,60,70,60,paint);
        canvas.drawCircle(70,60,5,paint1);
        canvas.drawText("OSGMR",60,50,txtPaintBlue);
        canvas.drawCircle(80,60,5,paint1);
        canvas.drawLine(80,60,120,60,paint1);
        canvas.drawText("CLTOP",110,50,txtPaintBlue);
        canvas.drawCircle(120,60,5,paint1);
        canvas.drawLine(130,60,170,60,paint1);
        canvas.drawCircle(130,60,5,paint1);
        canvas.drawText("FLBT",160,50,txtPaintBlue);
        canvas.drawCircle(170,60,5,paint1);
        canvas.drawLine(180,60,230,60,paint1);
        canvas.drawCircle(180,60,5,paint1);
        canvas.drawLine(230,60,230,80,paint1);
        canvas.drawLine(50,80,230,80,paint1);
        canvas.drawLine(50,80,50,100,paint1);
        canvas.drawCircle(20,100,5,paint1);
        canvas.drawText("FLR",5,90,textPaint);
        canvas.drawLine(20,100,70,100,paint1);
        canvas.drawCircle(70,100,5,paint2);
        canvas.drawText("OSG CT",60,90,txtPaintBlue);
        canvas.drawCircle(80,100,5,paint2);
        canvas.drawLine(80,100,120,100,paint2);
        canvas.drawCircle(120,100,5,paint2);
        canvas.drawText("STOP CT",110,90,txtPaintBlue);
        canvas.drawCircle(130,100,5,paint2);
        canvas.drawLine(130,100,170,100,paint2);
        canvas.drawText("STOP CAR",160,90,txtPaintBlue);
        canvas.drawCircle(170,100,5,paint2);
        canvas.drawCircle(180,100,5,paint2);
        canvas.drawLine(180,100,230,100,paint2);
        canvas.drawLine(230,100,230,120,paint2);
        canvas.drawLine(50,120,230,120,paint2);
        canvas.drawLine(50,120,50,140,paint2);
        canvas.drawCircle(20,140,5,paint2);
        canvas.drawText("CTR1",5,130,textPaint);
        canvas.drawLine(20,140,120,140,paint2);
        canvas.drawText("CAR GATE",110,155,txtPaintBlue);
        canvas.drawCircle(120,140,5,paint3);
        canvas.drawCircle(130,140,5,paint3);
        canvas.drawLine(130,140,230,140,paint3);
        canvas.drawLine(230,140,230,160,paint3);
        canvas.drawLine(50,160,230,160,paint3);
        canvas.drawLine(50,160,50,180,paint3);
        canvas.drawCircle(20,180,5,paint3);
        canvas.drawText("CTR",5,170,textPaint);
        canvas.drawCircle(80,180,5,paint3);
        canvas.drawLine(20,180,80,180,paint3);
        canvas.drawText("TOP FLR",70,190,txtPaintBlue);
        canvas.drawCircle(20,200,5,paint4);
        canvas.drawCircle(90,180,5,paint4);
        canvas.drawLine(90,180,130,180,paint4);
        canvas.drawText("FLR GATE(n-1)",130,190,txtPaintBlue);
        canvas.drawCircle(130,180,5,paint4);
        canvas.drawLine(140,180,230,180,paint4);
        canvas.drawCircle(140,180,5,paint4);
        canvas.drawLine(230,180,230,200,paint4);
        canvas.drawLine(20,200,230,200,paint4);
        canvas.drawCircle(20,200,5,paint4);
        canvas.drawText("FGR",5,190,textPaint);
        canvas.drawText("FLOOR GATES",180,210,txtPaintBlue);
    }

}
