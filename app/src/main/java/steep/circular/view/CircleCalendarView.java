package steep.circular.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PathMeasure;
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

import static steep.circular.view.PaintPool.DATE_PAINT;

/**
 * Created by Tom Kretzschmar on 09.10.2016.
 *
 */

public class CircleCalendarView extends View {

    private static final int RAD_SEASON_IN = 40;
    private static final int RAD_SEASON_OUT = 50;
    private static final int RAD_MONTH_IN = 55;
    private static final int RAD_MONTH_OUT = 80;
    private static final int RAD_SELECTION_IN = 90;
    private static final int RAD_SELECTION_MID = 110;
    private static final int RAD_SELECTION_OUT = 190;

    private int radiusSeasonIn;
    private int radiusSeasonOut;
    private int radiusMonthIn;
    private int radiusMonthOut;
    private int radiusSelectionIn;
    private int radiusSelectionMid;
    private int radiusSelectionOut;

    private Point center;
    private int width;
    private int height;

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

    private float pointerAngle = 90f;
    private PaintPool paintPool;


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
        final float scale = getResources().getDisplayMetrics().density;
        radiusSeasonIn = (int)(RAD_SEASON_IN * scale + 0.5f);
        radiusSeasonOut = (int)(RAD_SEASON_OUT * scale + 0.5f);
        radiusMonthIn = (int)(RAD_MONTH_IN * scale + 0.5f);
        radiusMonthOut = (int)(RAD_MONTH_OUT * scale + 0.5f);
        radiusSelectionIn = (int)(RAD_SELECTION_IN * scale + 0.5f);
        radiusSelectionMid = (int)(RAD_SELECTION_MID * scale + 0.5f);
        radiusSelectionOut = (int)(RAD_SELECTION_OUT * scale + 0.5f);

        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));
        paintPool = new PaintPool(this.getContext().getApplicationContext());
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
        int xPad = getPaddingLeft() + getPaddingRight();
        int yPad = getPaddingTop() + getPaddingBottom();

        width = w - xPad;
        height = h - yPad;

        center = new Point(w/2, h/3);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {  // TODO: Instanziierung aus draw verschieben
        Log.d("View", "DRAW");
        drawSeasons(canvas);
        drawMonths(canvas);

        selection = drawSelection(canvas, pointerAngle);

        drawDays(canvas, paintPool.getPaint(PaintPool.LINE_PAINT), pointerAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, DAYS_IN_SELECTION, radiusSelectionMid +5, radiusSelectionOut, center);
        drawPointer(canvas);
        drawEvents(canvas, paintPool.getPaint(PaintPool.WEEKEND_PAINT), pointerAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, DAYS_IN_SELECTION, radiusSelectionMid +5);
        drawEvents(canvas, paintPool.getPaint(PaintPool.VACATION_PAINT), pointerAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, DAYS_IN_SELECTION, radiusSelectionMid +40);

        drawDate(canvas);
    }

    private void drawDate(Canvas canvas){
        Paint textPaint = paintPool.getPaint(DATE_PAINT);
        String text1 = "" + currentDayOfMonth;
        String text2 = "" + (currentMonth+1);
        String text3 = "" + currentYear;

        float xStart1 = center.x - (textPaint.measureText(text1)/2);
        float xStart2 = center.x - (textPaint.measureText(text2)/2);
        float xStart3 = center.x - (textPaint.measureText(text3)/2);
        canvas.drawText(text1, xStart1, center.y-textPaint.getTextSize(), textPaint);
        canvas.drawText(text2, xStart2, center.y, textPaint);
        canvas.drawText(text3, xStart3, center.y+textPaint.getTextSize(), textPaint);
    }

    private void drawSeasons(Canvas canvas){
        float startAngle = 57;
        int startSeason = PaintPool.WINTER_PAINT;
        for(int i = 0; i<4; i++){
            float sweepAngle = 90;
            DonutSegment segSeason = new DonutSegment(startAngle, sweepAngle, radiusSeasonIn, radiusSeasonOut, center, true);
            segSeason.draw(canvas, paintPool.getPaint(startSeason + i));
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
            DonutSegment segMonth = new DonutSegment(startAngle, sweepAngle, radiusMonthIn, radiusMonthOut, center, true);
            segMonth.draw(canvas, paintPool.getPaint(month.getSeason()));
            startAngle += sweepAngle;

            PathMeasure pm = new PathMeasure(segMonth.getTextPath(), false);
            float length = pm.getLength();
            String text = (String.valueOf(month.name().substring(0,3)));
            Paint textPaint = paintPool.getPaint(PaintPool.TEXTW_PAINT);
            float tWidth = textPaint.measureText(text);
            canvas.drawTextOnPath(text, segMonth.getTextPath(), (length-tWidth)/2f, textPaint.getTextSize(), textPaint);
        }
    }

    private DonutSegment drawSelection(Canvas canvas, float startAngle){
        DonutSegment segInnerSelection = new DonutSegment(startAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, radiusSelectionIn, radiusSelectionMid -5, center, false);
        DonutSegment segOuterSelection = new DonutSegment(startAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, radiusSelectionMid +5, radiusSelectionOut, center, false);
        segInnerSelection.draw(canvas, paintPool.getPaint(PaintPool.SELECTION_PAINT));
        segOuterSelection.draw(canvas, paintPool.getPaint(PaintPool.SELECTION_PAINT));
        return segOuterSelection;
    }

    private void drawPointer(Canvas canvas){

        float angle = currentDayOfYear * anglePerDay + 90;

        Point start = GraphicHelpers.pointOnCircle(radiusMonthOut + 5, angle, center);
        Point stop = GraphicHelpers.pointOnCircle(radiusMonthOut + 20, angle, center);

        canvas.drawLine(start.x, start.y, stop.x, stop.y, paintPool.getPaint(PaintPool.DAY_PAINT));

        start = GraphicHelpers.pointOnCircle(radiusSeasonIn - 5, angle, center);
        stop = GraphicHelpers.pointOnCircle(radiusSeasonIn - 20, angle, center);

        canvas.drawLine(start.x, start.y, stop.x, stop.y, paintPool.getPaint(PaintPool.DAY_PAINT));
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

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(selection.intersects(touch)){    // TODO: wenn verschoben, intersect falsch
                    getParent().requestDisallowInterceptTouchEvent(true);
                    lastTouchAngle = GraphicHelpers.getAngleOfPoint(touch, center);
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                float angle = GraphicHelpers.getAngleOfPoint(touch, center);
                float diff = angle-lastTouchAngle;
                pointerAngle += diff;
                lastTouchAngle = angle;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                pointerAngle = 90f;
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                pointerAngle = 90f;
                invalidate();
                return true;
            default:
                return false;
        }
    }
}
