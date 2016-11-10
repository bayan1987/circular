package steep.circular.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import steep.circular.R;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;


public class CircleClockView extends View {


    private final int clockRadiusInner = 280;
    private final int clockRadiusOuter = 300;
    private final int clockRadiusScale = 100;

    private Paint paint;
    private Paint paint2;
    private Paint paint3;
    private Paint paint3_blur;
    private Paint paintRed;
    private int radius = 500;
    private Point center;

    private float minute;
    private float second;

    private boolean hours = false;


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
//        Log.d("CIRCLEVIEW", "onDraw");
//        canvas.drawRect(0,0,100,100, paint);

//        int k = 0;
//        for(int i = 1; i<=365; i++){
//            if(k<7) k++;
//            else k = 0;
//            drawDay(canvas, i, (k == 6 || k == 7));
//        }
//        for(int j = 0; j<=12; j++){
//            drawMonth(canvas, j);
//        }
        if(hours){
            for (int i = 0; i < 60; i++) {
                float deltaSec = Math.min(Math.abs(second - i), Math.min(Math.abs(second - i - 60), Math.abs(second - i + 60)));
                if (deltaSec < 0.8) deltaSec = 0.8f;

                float j = i / 60f * 24f;
                float deltaMin = Math.min(Math.abs(minute - j), Math.min(Math.abs(minute - j - 24), Math.abs(minute - j + 24)));
                if (deltaMin < 0.8) deltaMin = 0.8f;

                int lengthIn = (int) ((1 / deltaSec) * clockRadiusScale);
                int lengthOut = (int) ((1 / deltaMin) * clockRadiusScale);


                drawClock(canvas, i, lengthOut, lengthIn);
            }
        } else {
            for (int i = 0; i < 60; i++) {
                float deltaSec = Math.min(Math.abs(second - i), Math.min(Math.abs(second - i - 60), Math.abs(second - i + 60)));
                if (deltaSec < 0.8) deltaSec = 0.8f;

                float deltaMin = Math.min(Math.abs(minute - i), Math.min(Math.abs(minute - i - 60), Math.abs(minute - i + 60)));
                if (deltaMin < 0.8) deltaMin = 0.8f;

                int lengthIn = (int) ((1 / deltaSec) * clockRadiusScale);
                int lengthOut = (int) ((1 / deltaMin) * clockRadiusScale);
//            Log.d("CIRCLEVIEW", "lengthIn " + lengthIn + " | lengthOut " + lengthOut);

//            if(minute == i) {length = 500;}
//            if(minute == i-1 || minute == i+1) {length = 450;}
//            if(minute == i-2 || minute == i+2) {length = 350;}

                drawClock(canvas, i, lengthOut, lengthIn);
//            Log.d("CIRCLEVIEW", "drawClock " + i);
            }
        }


    }

    private void drawMonth(Canvas canvas, int number){
        float angle = 360.0f/12f;

//        float angleStart = (float) Math.toRadians((number-1) * angle);
//        float angleStop = (float) Math.toRadians(number * angle);
        float angleStart = (float) toRadians((number-1) * angle);
        float angleStop = number * angle;

//        Log.d("CIRCLEVIEW", "Start: " + angleStart + " ||| Stop: " + angleStop + " - " + (weekend? "WEEKEND" : ""));

        float startX = center.x + (float) cos(angleStart)*radius/3;
        float startY = center.y + (float) sin(angleStart)*radius/3;
        float stopX = center.x + (float) cos(angleStart)*(radius+50);
        float stopY = center.y + (float) sin(angleStart)*(radius+50);


        canvas.drawLine(startX,startY, stopX, stopY, paint3);
    }

    private void drawDay(Canvas canvas, int number, boolean weekend){
        float angle = 360.0f/365.0f;

        float angleStart_Rad = (float) toRadians((number-1) * angle);
//        float angleStop = (float) Math.toRadians(number * angle);
        float angleStart = (number-1) * angle;
        float angleStop = number * angle;

//        Log.d("CIRCLEVIEW", "Start: " + angleStart + " ||| Stop: " + angleStop + " - " + (weekend? "WEEKEND" : ""));

        float startX = center.x + (float) cos(angleStart_Rad)*radius/2;
        float startY = center.y + (float) sin(angleStart_Rad)*radius/2;
        float stopX = center.x + (float) cos(angleStart_Rad)*radius;
        float stopY = center.y + (float) sin(angleStart_Rad)*radius;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if(weekend) canvas.drawArc(100,100,900,900,angleStart, angle, false, paintRed);
            //else canvas.drawArc(100,100,900,900,angleStart, angle, false, paint);
        }

        canvas.drawLine(startX,startY, stopX, stopY, paint2);
    }

    private void drawClock(Canvas canvas,int number, int lengthOut, int lengthIn){
        float angle = 360.0f/60f;

        float angleStart = (float) toRadians((number * angle)-90);


        float startX = center.x + (float) cos(angleStart)*(clockRadiusInner - lengthIn);
        float startY = center.y + (float) sin(angleStart)*(clockRadiusInner - lengthIn);
        float stopX = center.x + (float) cos(angleStart)*(lengthOut + clockRadiusOuter);
        float stopY = center.y + (float) sin(angleStart)*(lengthOut + clockRadiusOuter);

//        canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);
        if(number != 0 && number != 30 && number != 45 && number != 15) canvas.drawLine(startX,startY, stopX, stopY, paint3);
        else canvas.drawLine(startX,startY, stopX, stopY, paint3_blur);

    }

    private static final float CIRCLE_LIMIT = 359.9999f;
    /**
     * Draws a thick arc between the defined angles, see {@link Canvas#drawArc} for more.
     * This method is equivalent to
     * <pre><code>
     * float rMid = (rInn + rOut) / 2;
     * paint.setStyle(Style.STROKE); // there's nothing to fill
     * paint.setStrokeWidth(rOut - rInn); // thickness
     * canvas.drawArc(new RectF(cx - rMid, cy - rMid, cx + rMid, cy + rMid), startAngle, sweepAngle, false, paint);
     * </code></pre>
     * but supports different fill and stroke paints.
     *
     * @param canvas
     * @param cx horizontal middle point of the oval
     * @param cy vertical middle point of the oval
     * @param rInn inner radius of the arc segment
     * @param rOut outer radius of the arc segment
     * @param startAngle see {@link Canvas#drawArc}
     * @param sweepAngle see {@link Canvas#drawArc}, capped at &plusmn;360
     * @param fill filling paint, can be <code>null</code>
     * @param stroke stroke paint, can be <code>null</code>
     * @see Canvas#drawArc
     */
    public static void drawArcSegment(Canvas canvas, float cx, float cy, float rInn, float rOut, float startAngle,
                                      float sweepAngle, Paint fill, Paint stroke) {
        if (sweepAngle > CIRCLE_LIMIT) {
            sweepAngle = CIRCLE_LIMIT;
        }
        if (sweepAngle < -CIRCLE_LIMIT) {
            sweepAngle = -CIRCLE_LIMIT;
        }

        RectF outerRect = new RectF(cx - rOut, cy - rOut, cx + rOut, cy + rOut);
        RectF innerRect = new RectF(cx - rInn, cy - rInn, cx + rInn, cy + rInn);

        Path segmentPath = new Path();
        double start = toRadians(startAngle);
        segmentPath.moveTo((float)(cx + rInn * cos(start)), (float)(cy + rInn * sin(start)));
        segmentPath.lineTo((float)(cx + rOut * cos(start)), (float)(cy + rOut * sin(start)));
        segmentPath.arcTo(outerRect, startAngle, sweepAngle);
        double end = toRadians(startAngle + sweepAngle);
        segmentPath.lineTo((float)(cx + rInn * cos(end)), (float)(cy + rInn * sin(end)));
        segmentPath.arcTo(innerRect, startAngle + sweepAngle, -sweepAngle);
        if (fill != null) {
            canvas.drawPath(segmentPath, fill);
        }
        if (stroke != null) {
            canvas.drawPath(segmentPath, stroke);
        }
    }

    public void updateClock(int minute, int second, int millisecond) {
        hours = false;
        this.second = second + (millisecond/1000f);
        this.minute = minute + (this.second/60f);

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
