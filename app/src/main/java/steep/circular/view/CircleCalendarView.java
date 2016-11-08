package steep.circular.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import steep.circular.data.Month;
import steep.circular.data.Weekday;
import steep.circular.util.GraphicHelpers;
import steep.circular.util.Point;
import steep.circular.view.shapes.DonutSegment;
import steep.circular.view.shapes.Marker;

import static android.R.attr.angle;
import static android.R.attr.radius;
import static android.R.attr.x;
import static android.R.attr.y;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

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

    private final int center_x = 500;
    private final int center_y = 500;

    private float anglePerDay;

    private int daysInYear;
    private int currentYear;
    private int currentMonth;
    private int currentDayOfYear;
    private int currentDayOfMonth;
    private Weekday currentWeekday;
    private boolean isLeapYear;
    private Weekday firstDayOfYear;

    private static final int DAYS_IN_SELECTION = 7;
    private static final int SELECTION_ANGLE = 90;



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
        linePaint.setColor(0x88202020);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(5);

        sweepPaint = new Paint();
//        sweepPaint.setColor(0xaaffffff);
        sweepPaint.setShader(new SweepGradient(center_x, center_y, sweepGradients, null));

        selectionPaint = new Paint();
        selectionPaint.setColor(0xaaeae3be);

        textPaint = new Paint();
        textPaint.setColor(0xff602020);
        textPaint.setTextSize(25);

        textPaintWhite = new Paint();
        textPaintWhite.setColor(0xffffffff);
        textPaintWhite.setTextSize(60);
        textPaintWhite.setFakeBoldText(true);

        dayPaint = new Paint();
        dayPaint.setColor(0xffff2244);
        dayPaint.setAntiAlias(true);
        dayPaint.setStrokeWidth(15);

        weekendPaint = new Paint();
        weekendPaint.setColor(0xff42d185);

        vacationPaint = new Paint();
        vacationPaint.setColor(0xff428ad1);

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
        drawSelection(canvas);

        float startAngle = 90;
        drawDays(canvas, linePaint, startAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, DAYS_IN_SELECTION, RADIUS_SELECTION_MID+5, RADIUS_SELECTION_MID+100, center_x, center_y);
        drawPointer(canvas);
        drawEvents(canvas, weekendPaint, startAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, DAYS_IN_SELECTION, RADIUS_SELECTION_MID+5);
        drawEvents(canvas, vacationPaint, startAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, DAYS_IN_SELECTION, RADIUS_SELECTION_MID+40);

    }

    private void drawSeasons(Canvas canvas){
        float startAngle = 45;
        for(int i = 0; i<4; i++){
            float sweepAngle = 90;
            DonutSegment segSeason = new DonutSegment(startAngle, sweepAngle, RADIUS_SEASON_IN, RADIUS_SEASON_OUT, center_x, center_y, true);
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
            DonutSegment segMonth = new DonutSegment(startAngle, sweepAngle, RADIUS_MONTH_IN, RADIUS_MONTH_OUT, center_x, center_y, true);
            segMonth.draw(canvas, sweepPaint);
            startAngle += sweepAngle;

            canvas.drawTextOnPath((String.valueOf(month.name().charAt(0))), segMonth.getTextPath(), 22, 60, textPaintWhite);
        }
    }

    private void drawSelection(Canvas canvas){
        float startAngle = 90;
        DonutSegment segInnerSelection = new DonutSegment(startAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, RADIUS_SELECTION_IN, RADIUS_SELECTION_MID-5, center_x, center_y, false);
        DonutSegment segOuterSelection = new DonutSegment(startAngle + currentDayOfYear*anglePerDay, SELECTION_ANGLE, RADIUS_SELECTION_MID+5, RADIUS_SELECTION_OUT, center_x, center_y, false);
        segInnerSelection.draw(canvas, selectionPaint);
        segOuterSelection.draw(canvas, selectionPaint);
    }

    private void drawPointer(Canvas canvas){

        float angle = currentDayOfYear * anglePerDay + 90;

        Point start = GraphicHelpers.pointOnCircle(RADIUS_MONTH_OUT + 5, angle, new Point(center_x, center_y));
        Point stop = GraphicHelpers.pointOnCircle(RADIUS_MONTH_OUT + 20, angle, new Point(center_x, center_y));

        canvas.drawLine(start.x, start.y, stop.x, stop.y, dayPaint);

        start = GraphicHelpers.pointOnCircle(RADIUS_SEASON_IN - 5, angle, new Point(center_x, center_y));
        stop = GraphicHelpers.pointOnCircle(RADIUS_SEASON_IN - 20, angle, new Point(center_x, center_y));

        canvas.drawLine(start.x, start.y, stop.x, stop.y, dayPaint);
    }

    private void drawDays(Canvas canvas, Paint paint, float startAngle, float stretchAngle, int daycount, float inner, float outer, int x, int y) {

        for (int i = 1; i < daycount; i++) {
            startAngle += (stretchAngle / daycount);

            Point start = GraphicHelpers.pointOnCircle(inner, startAngle, new Point(x,y));
            Point stop = GraphicHelpers.pointOnCircle(outer, startAngle, new Point(x,y));

            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        }
    }

    private void drawEvents(Canvas canvas, Paint paint, float startAngle, float stretchAngle, int daycount, float radius){
        float step = stretchAngle / daycount;
        startAngle = startAngle + step/2f;
        for(int i = 0; i < daycount; i++) {

            Marker marker = new Marker(startAngle + (i*step), radius + 5, 40, 30, 3, new Point(center_x, center_y));
            marker.draw(canvas, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }
}
