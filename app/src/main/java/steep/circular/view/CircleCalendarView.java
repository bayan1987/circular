package steep.circular.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.GregorianCalendar;

import steep.circular.R;
import steep.circular.data.Month;
import steep.circular.data.Weekday;
import steep.circular.util.GraphicHelpers;
import steep.circular.util.Point;
import steep.circular.view.shapes.DonutSegment;
import steep.circular.view.shapes.Marker;

/**
 * Created by Tom Kretzschmar on 09.10.2016.
 *
 */

public class CircleCalendarView extends View {

    private int[] sweepGradients = {0xFFed4264, 0xFF3a6186, 0xFF3a6186, 0xFF64f38c, 0xFFffedbc, 0xFFf8b500, 0xFFf8b500, 0xFFed4264};
    private Paint linePaint;
    private Paint textPaint;
    private Paint textPaintWhite;
    private Paint dayPaint;
    private Paint weekendPaint;
    private Paint vacationPaint;
    private Paint selectionPaint;
    private Paint sweepPaint;

    private static final int RADIUS_SEASON_IN = 70;
    private static final int RADIUS_SEASON_OUT = 90;
    private static final int RADIUS_MONTH_IN = 100;
    private static final int RADIUS_MONTH_OUT = 175;
    private static final int RADIUS_SELECTION_IN = 200;
    private static final int RADIUS_SELECTION_MID = 275;
    private static final int RADIUS_SELECTION_OUT = 475;

    private final Point center = new Point(500,500);

    private float anglePerDay;

    private int daysInYear;
    private int currentYear;
    private int currentMonth;
    private int currentDayOfYear;
    private int currentDayOfMonth;
    private Weekday currentWeekday;
    private boolean isLeapYear;
    private Weekday firstDayOfYear;

    private DonutSegment selection;

    private static final int DAYS_IN_SELECTION = 7;
    private static final int SELECTION_ANGLE = 90;

    private static final float TOUCH_SCALE_FAC = 1f;
    private float lastTouchAngle = 0f;
    private boolean movingSelection = false;

    private float pointerAngle = 90f;


    public CircleCalendarView(Context context) {
        super(context);
        init();
    }

    public CircleCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Weekday calcWeekdayFromDayOfYear(int dayOfYear, Weekday firstDayOfYear){
        int day = dayOfYear % 7;
        day = day + firstDayOfYear.getValue();
        return Weekday.valueOf(day);
    }

    private void init(){
        linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorLine));
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(5);

        sweepPaint = new Paint();
        sweepPaint.setShader(new SweepGradient(center.x, center.y, sweepGradients, null));

        selectionPaint = new Paint();
        selectionPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorSelection));

        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorText));
        textPaint.setTextSize(25);

        textPaintWhite = new Paint();
        textPaintWhite.setColor(ContextCompat.getColor(getContext(), R.color.colorTextW));
        textPaintWhite.setTextSize(60);
        textPaintWhite.setFakeBoldText(true);

        dayPaint = new Paint();
        dayPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPointer));
        dayPaint.setAntiAlias(true);
        dayPaint.setStrokeWidth(15);

        weekendPaint = new Paint();
        weekendPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorWeekend));

        vacationPaint = new Paint();
        vacationPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorVacation));

        update();
    }

    public void update(){
        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH);
        currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        isLeapYear = new GregorianCalendar().isLeapYear(currentYear);
//        if(isLeapYear) countDaysOfMonth.put(Month.February, 29);
//        else countDaysOfMonth.put(Month.February, 28);

        cal.set(currentYear, 1, 1);
        firstDayOfYear = Weekday.valueOf(cal.get(Calendar.DAY_OF_WEEK));

        currentWeekday = calcWeekdayFromDayOfYear(currentDayOfYear, firstDayOfYear);

        daysInYear = isLeapYear ? 366 : 365;
        anglePerDay = 360f / daysInYear;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawSeasons(canvas);
        drawMonths(canvas);

        selection = drawSelection(canvas, pointerAngle);

        drawDays(canvas, linePaint, pointerAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, DAYS_IN_SELECTION, RADIUS_SELECTION_MID+5, RADIUS_SELECTION_MID+100, center);
        drawPointer(canvas);
        drawEvents(canvas, weekendPaint, pointerAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, DAYS_IN_SELECTION, RADIUS_SELECTION_MID+5);
        drawEvents(canvas, vacationPaint, pointerAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, DAYS_IN_SELECTION, RADIUS_SELECTION_MID+40);
    }

    private void drawSeasons(Canvas canvas){
        float startAngle = 45;
        for(int i = 0; i<4; i++){
            float sweepAngle = 90;
            DonutSegment segSeason = new DonutSegment(startAngle, sweepAngle, RADIUS_SEASON_IN, RADIUS_SEASON_OUT, center, true);
            segSeason.draw(canvas, sweepPaint);
            startAngle += sweepAngle;
        }
    }

    private void drawMonths(Canvas canvas){
        float startAngle = 90;
        for(Month month : Month.values()) {
            float sweepAngle;
            if(month == Month.February){
                int dayCount = isLeapYear ? month.getDayCount() : month.getDayCount() + 1;
                sweepAngle = dayCount * anglePerDay;
            } else {
                sweepAngle = month.getDayCount() * anglePerDay;
            }
            DonutSegment segMonth = new DonutSegment(startAngle, sweepAngle, RADIUS_MONTH_IN, RADIUS_MONTH_OUT, center, true);
            segMonth.draw(canvas, sweepPaint);
            startAngle += sweepAngle;

            canvas.drawTextOnPath((String.valueOf(month.name().charAt(0))), segMonth.getTextPath(), 22, 60, textPaintWhite);
        }
    }

    private DonutSegment drawSelection(Canvas canvas, float startAngle){
        DonutSegment segInnerSelection = new DonutSegment(startAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, RADIUS_SELECTION_IN, RADIUS_SELECTION_MID-5, center, false);
        DonutSegment segOuterSelection = new DonutSegment(startAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, RADIUS_SELECTION_MID+5, RADIUS_SELECTION_OUT, center, false);
        segInnerSelection.draw(canvas, selectionPaint);
        segOuterSelection.draw(canvas, selectionPaint);
        return segInnerSelection;
    }

    private void drawPointer(Canvas canvas){

        float angle = currentDayOfYear * anglePerDay + 90;

        Point start = GraphicHelpers.pointOnCircle(RADIUS_MONTH_OUT + 5, angle, center);
        Point stop = GraphicHelpers.pointOnCircle(RADIUS_MONTH_OUT + 20, angle, center);

        canvas.drawLine(start.x, start.y, stop.x, stop.y, dayPaint);

        start = GraphicHelpers.pointOnCircle(RADIUS_SEASON_IN - 5, angle, center);
        stop = GraphicHelpers.pointOnCircle(RADIUS_SEASON_IN - 20, angle, center);

        canvas.drawLine(start.x, start.y, stop.x, stop.y, dayPaint);
    }

    private void drawDays(Canvas canvas, Paint paint, float startAngle, float stretchAngle, int daycount, float inner, float outer, Point c) {

        for (int i = 1; i < daycount; i++) {
            startAngle += (stretchAngle / daycount);

            Point start = GraphicHelpers.pointOnCircle(inner, startAngle, c);
            Point stop = GraphicHelpers.pointOnCircle(outer, startAngle, c);

            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        }
    }

    private void drawEvents(Canvas canvas, Paint paint, float startAngle, float stretchAngle, int daycount, float radius){
        float step = stretchAngle / daycount;
        startAngle = startAngle + step/2f;
        for(int i = 0; i < daycount; i++) {

            Marker marker = new Marker(startAngle + (i*step), radius + 5, 40, 30, 3, center);
            marker.draw(canvas, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        Point touch = new Point(event.getX(), event.getY());
        Log.d("Touch", "onTouchEvent - " + touch);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("Touch", "ACTION_DOWN");
                if(selection.intersects(touch)){
                    Log.d("Touch", "Intersecting Selection");
                    movingSelection = true;
                }
                lastTouchAngle = GraphicHelpers.getAngleOfPoint(touch, center);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("Touch", "ACTION_MOVE ------------------------- ");
                float angle = GraphicHelpers.getAngleOfPoint(touch, center);
                float diff = angle-lastTouchAngle;
                pointerAngle += diff;
                Log.d("Touch", "angle: " + angle);
                Log.d("Touch", "pointer: " + pointerAngle);

                lastTouchAngle = angle;

                break;

            case MotionEvent.ACTION_UP:
                Log.d("Touch", "ACTION_UP");
                pointerAngle = 90f;
                movingSelection = false;
                break;
        }
        invalidate();
        return true;
    }
}
