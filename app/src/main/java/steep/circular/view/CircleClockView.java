package steep.circular.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import steep.circular.R;
import steep.circular.TabbedMainActivity;
import steep.circular.util.GraphicHelpers;
import steep.circular.util.Point;


public class CircleClockView extends View {

    private static final int clockRadiusInner = 260;
    private static final int clockRadiusOuter = 310;
    private static final int clockRadiusMiddleIn = 270;
    private static final int clockRadiusMiddleOut = 300;
    private static final int clockRadiusScale = 100;

    private Paint paintStrokes;
    private Paint paintAccent;
    private Point center;

    private float minute;
    private float second;
    private float hour;

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

    private void init() {
        paintStrokes = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStrokes.setStyle(Paint.Style.STROKE);
        paintStrokes.setStrokeWidth(10.0f);
        paintStrokes.setStrokeCap(Paint.Cap.ROUND);
        paintStrokes.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        paintAccent = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintAccent.setStyle(Paint.Style.STROKE);
        paintAccent.setStrokeWidth(10.0f);
        paintAccent.setStrokeCap(Paint.Cap.ROUND);
        paintAccent.setColor(ContextCompat.getColor(getContext(), R.color.colorPointer));
//        paintAccent.setMaskFilter(new BlurMaskFilter(20.0f, BlurMaskFilter.Blur.NORMAL));
//        paintStrokes.setShadowLayer(40.0f, 0.0f, 0.0f, Color.BLACK);

        center = new Point(500, 500);
        center.x = TabbedMainActivity.center_x;

        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float xPad = getPaddingLeft() + getPaddingRight();
        float yPad = getPaddingTop() + getPaddingBottom();


        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 60; i++) {
            float deltaSec = Math.min(Math.abs(minute - i), Math.min(Math.abs(minute - i - 60), Math.abs(minute - i + 60)));
            if (deltaSec < 0.8) deltaSec = 0.8f;

            float temp_hour = hour / 12 * 60;
            float deltaMin = Math.min(Math.abs(temp_hour - i), Math.min(Math.abs(temp_hour - i - 60), Math.abs(temp_hour - i + 60)));
            if (deltaMin < 0.8) deltaMin = 0.8f;

            int lengthIn = (int) ((1 / deltaMin) * clockRadiusScale);
            int lengthOut = (int) ((1 / deltaSec) * clockRadiusScale);

            drawClock(canvas, i, lengthOut, lengthIn, (int) second);
        }
    }

    private void drawClock(Canvas canvas, int number, int lengthOut, int lengthIn, int second) {
        float angle = 360.0f / 60f;
        float angleStart = (number * angle) - 90;

        if (second == 0 && !isSet) {
            filling = !filling;
            isSet = true;
        } else if (second == 1) {
            isSet = false;
        }

        if ((filling && (number < second)) || (!filling && (number > second))) {
            drawLine(canvas, clockRadiusInner - lengthIn, lengthOut + clockRadiusOuter, angleStart, number);//1
        } else {
            drawLine(canvas, clockRadiusInner - lengthIn, clockRadiusMiddleIn, angleStart, number);//2
            drawLine(canvas, clockRadiusMiddleOut, lengthOut + clockRadiusOuter, angleStart, number);//3
        }
    }

    private void drawLine(Canvas canvas, float radiusIn, float radiusOut, float angle, int number) {
        Point start = GraphicHelpers.pointOnCircle(radiusIn, angle, center);
        Point stop = GraphicHelpers.pointOnCircle(radiusOut, angle, center);

        if (number != 0 && number != 30 && number != 45 && number != 15)
            canvas.drawLine(start.x, start.y, stop.x, stop.y, paintStrokes);
        else canvas.drawLine(start.x, start.y, stop.x, stop.y, paintAccent);
    }

    public void updateClock(int hour, int minute, int second, int millisecond) {
        this.second = second + (millisecond / 1000f);
        this.minute = minute + (this.second / 60f);
        this.hour = hour + (this.minute / 60f);
        invalidate();

    }

    public void updateWidgetClock(int hour, int minute, int second) {
        this.second = hour + (minute / 60f);
        this.minute = minute + (this.second / 60f);
        invalidate();
    }
}
