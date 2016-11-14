package steep.circular.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import steep.circular.R;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;


public class CircleClockView extends View {


    private final int clockRadiusInner = 260;
    private final int clockRadiusOuter = 310;
    private final int clockRadiusMiddleIn = 270;
    private final int clockRadiusMiddleOut = 300;
    private final int clockRadiusScale = 100;

    private Paint paint;
    private Paint paint2;
    private Paint paint3;
    private Paint paint3_blur;
    private Paint paintRed;
    private Point center;

    private float minute;
    private float second;
    private float hour;

    private boolean hours = true;

    private boolean filling = true;
    private boolean isSet = false;


    public CircleClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleClockView(Context context) {
        super(context);
        init();
    }

    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(50);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorLine));
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(0xff20ff20);
        paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(10.0f);
        paint3.setStrokeCap(Paint.Cap.ROUND);
        paint3.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//        paint3.setColor(0x66202020);
//        paint3.setMaskFilter(new BlurMaskFilter(20.0f, BlurMaskFilter.Blur.NORMAL));
        paint3_blur = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3_blur.setStyle(Paint.Style.STROKE);
        paint3_blur.setStrokeWidth(10.0f);
        paint3_blur.setStrokeCap(Paint.Cap.ROUND);
        paint3_blur.setColor(0xaaff2020);
//        paint3_blur.setMaskFilter(new BlurMaskFilter(20.0f, BlurMaskFilter.Blur.NORMAL));
//        paint3.setShadowLayer(40.0f, 0.0f, 0.0f, Color.BLACK);
        paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRed.setStyle(Paint.Style.STROKE);
        paintRed.setStrokeWidth(100);
        paintRed.setColor(ContextCompat.getColor(getContext(), R.color.colorPointer));
        center = new Point(500,500);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setWillNotDraw(false);

        if(hours){
            for (int i = 0; i < 60; i++) {
                float deltaSec = Math.min(Math.abs(second - i), Math.min(Math.abs(second - i - 60), Math.abs(second - i + 60)));
                if (deltaSec < 0.8) deltaSec = 0.8f;

                float j = i / 60f * 24f;
                float deltaMin = Math.min(Math.abs(minute - j), Math.min(Math.abs(minute - j - 24), Math.abs(minute - j + 24)));
                if (deltaMin < 0.8) deltaMin = 0.8f;

                int lengthIn = (int) ((1 / deltaSec) * clockRadiusScale);
                int lengthOut = (int) ((1 / deltaMin) * clockRadiusScale);


                drawClock(canvas, i, lengthOut, lengthIn, (int)second);
            }
        } else {
            for (int i = 0; i < 60; i++) {
                float deltaSec = Math.min(Math.abs(minute - i), Math.min(Math.abs(minute - i - 60), Math.abs(minute - i + 60)));
                if (deltaSec < 0.8) deltaSec = 0.8f;

                float temp_hour = hour/12*60;
                float deltaMin = Math.min(Math.abs(temp_hour - i), Math.min(Math.abs(temp_hour - i - 60), Math.abs(temp_hour - i + 60)));
                if (deltaMin < 0.8) deltaMin = 0.8f;

                int lengthIn = (int) ((1 / deltaMin) * clockRadiusScale);
                int lengthOut = (int) ((1 / deltaSec) * clockRadiusScale);
//            Log.d("CIRCLEVIEW", "lengthIn " + lengthIn + " | lengthOut " + lengthOut);

//            if(minute == i) {length = 500;}
//            if(minute == i-1 || minute == i+1) {length = 450;}
//            if(minute == i-2 || minute == i+2) {length = 350;}

                drawClock(canvas, i, lengthOut, lengthIn, (int)second);
//            Log.d("CIRCLEVIEW", "drawClock " + i);
            }
        }


    }

    private void drawClock(Canvas canvas,int number, int lengthOut, int lengthIn, int second){
        float angle = 360.0f/60f;

        float angleStart = (float) toRadians((number * angle)-90);

//        Log.d("clock", "sec:" + second);
        if(second == 0 && !isSet){
            filling = !filling;
            isSet = true;
        } else if(second == 1){
            isSet = false;
        }

        if(filling){
            if(number<second){
                float startX = center.x + (float) cos(angleStart)*(clockRadiusInner - lengthIn);
                float startY = center.y + (float) sin(angleStart)*(clockRadiusInner - lengthIn);
                float stopX = center.x + (float) cos(angleStart)*(lengthOut + clockRadiusOuter);
                float stopY = center.y + (float) sin(angleStart)*(lengthOut + clockRadiusOuter);

//        canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);
                if(number != 0 && number != 30 && number != 45 && number != 15) canvas.drawLine(startX,startY, stopX, stopY, paint3);
                else canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);

            } else {
                float startX = center.x + (float) cos(angleStart)*(clockRadiusInner - lengthIn);
                float startY = center.y + (float) sin(angleStart)*(clockRadiusInner - lengthIn);
                float stopX = center.x + (float) cos(angleStart)*(clockRadiusMiddleIn);
                float stopY = center.y + (float) sin(angleStart)*(clockRadiusMiddleIn);

//        canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);
                if(number != 0 && number != 30 && number != 45 && number != 15) canvas.drawLine(startX,startY, stopX, stopY, paint3);
                else canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);


                startX = center.x + (float) cos(angleStart)*(clockRadiusMiddleOut);
                startY = center.y + (float) sin(angleStart)*(clockRadiusMiddleOut);
                stopX = center.x + (float) cos(angleStart)*(lengthOut + clockRadiusOuter);
                stopY = center.y + (float) sin(angleStart)*(lengthOut + clockRadiusOuter);

//        canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);
                if(number != 0 && number != 30 && number != 45 && number != 15) canvas.drawLine(startX,startY, stopX, stopY, paint3);
                else canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);
            }
        } else {
            if(number>second){
                float startX = center.x + (float) cos(angleStart)*(clockRadiusInner - lengthIn);
                float startY = center.y + (float) sin(angleStart)*(clockRadiusInner - lengthIn);
                float stopX = center.x + (float) cos(angleStart)*(lengthOut + clockRadiusOuter);
                float stopY = center.y + (float) sin(angleStart)*(lengthOut + clockRadiusOuter);

//        canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);
                if(number != 0 && number != 30 && number != 45 && number != 15) canvas.drawLine(startX,startY, stopX, stopY, paint3);
                else canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);

            } else {
                float startX = center.x + (float) cos(angleStart)*(clockRadiusInner - lengthIn);
                float startY = center.y + (float) sin(angleStart)*(clockRadiusInner - lengthIn);
                float stopX = center.x + (float) cos(angleStart)*(clockRadiusMiddleIn);
                float stopY = center.y + (float) sin(angleStart)*(clockRadiusMiddleIn);

//        canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);
                if(number != 0 && number != 30 && number != 45 && number != 15) canvas.drawLine(startX,startY, stopX, stopY, paint3);
                else canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);


                startX = center.x + (float) cos(angleStart)*(clockRadiusMiddleOut);
                startY = center.y + (float) sin(angleStart)*(clockRadiusMiddleOut);
                stopX = center.x + (float) cos(angleStart)*(lengthOut + clockRadiusOuter);
                stopY = center.y + (float) sin(angleStart)*(lengthOut + clockRadiusOuter);

//        canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);
                if(number != 0 && number != 30 && number != 45 && number != 15) canvas.drawLine(startX,startY, stopX, stopY, paint3);
                else canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);
            }
        }


    }



    public void updateClock(int hour, int minute, int second, int millisecond) {
        hours = false;
        this.second = second + (millisecond/1000f);
        this.minute = minute + (this.second/60f);
        this.hour = hour + (this.minute/60f);
        invalidate();
//        requestLayout();
//        Log.d("CIRCLEVIEW", "updateClock " + this.second);
    }

    public void updateWidgetClock(int hour, int minute, int second){
        hours = true;
        this.second = hour + (minute/60f);
        this.minute = minute + (this.second/60f);

        invalidate();
    }

}
