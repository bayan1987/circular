package steep.circular.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import steep.circular.R;
import steep.circular.data.Event;
import steep.circular.data.Month;
import steep.circular.data.Weekday;
import steep.circular.util.GraphicHelpers;
import steep.circular.util.Point;
import steep.circular.view.shapes.DonutSegment;
import steep.circular.view.shapes.Ring;

import static steep.circular.util.GraphicHelpers.getAngleOfPoint;
import static steep.circular.view.PaintPool.DATE_PAINT;
import static steep.circular.view.PaintPool.SELECTION_PAINT;
import static steep.circular.view.PaintPool.SELECTION_PAINT_DARK;

/**
 * Created by Tom Kretzschmar on 09.10.2016.
 *
 */

public class CircleCalendarView extends View {

    private static final int RAD_SEASON_IN = 80;
    private static final int RAD_SEASON_OUT = 90;
    private static final int RAD_MONTH_IN = 95;
    private static final int RAD_MONTH_OUT = 125;
    private static final int RAD_SELECTION_IN = 135;
    private static final int RAD_SELECTION_MID = 170;
    private static final int RAD_SELECTION_OUT = 200;

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

    private static final int DAYS_IN_SELECTION = 7;
    private static final int SELECTION_ANGLE = 90;

    private static final float TOUCH_SCALE_FAC = 1f;
    private float lastTouchAngle = 0f;

    private float pointerAngle = 90f;
    private PaintPool paintPool;
    private Ring eventRing;

    boolean touched = false;
    float currentDrawAngle = 0.0f;
    float velocity;

    float lastTime;

    private List<List<Event>> events;


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

        paintPool = new PaintPool(this.getContext().getApplicationContext());
        update(null);
    }

    public void update(List<List<Event>> events){
        this.events = events;

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

        center = new Point(w/2, h/2.5f);

        eventRing = new Ring(radiusSelectionIn, radiusSelectionMid, center);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {  // TODO: Instanziierung aus draw verschieben
        float startAngle = 90;

        drawSeasons(canvas);
        drawMonths(canvas);
        eventRing.draw(canvas, paintPool.getPaint(SELECTION_PAINT));

//        float a = 2.0f;

        if(touched){
//            float dt = System.currentTimeMillis() - lastTime;
//            float diff = lastTouchAngle - currentDrawAngle;

            DonutSegment seg = new DonutSegment(currentDrawAngle -45, 90, radiusSelectionIn, radiusSelectionMid, center, false);
            seg.draw(canvas, paintPool.getPaint(SELECTION_PAINT_DARK));
        }

        drawEventPoints(canvas, null, startAngle, anglePerDay, radiusSelectionIn+5, radiusSelectionMid-5);
        drawPointer(canvas);
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

    private void drawPointer(Canvas canvas){

        float angle = currentDayOfYear * anglePerDay + 90;

        Point start = GraphicHelpers.pointOnCircle(radiusSelectionMid + 5, angle, center);
        Point stop = GraphicHelpers.pointOnCircle(radiusSeasonIn -20, angle, center);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        paint.setAntiAlias(true);
        paint.setShadowLayer(25, 2.5f, 0f, Color.BLACK);
        paint.setStrokeWidth(4);
        paint.setStrokeCap(Paint.Cap.ROUND);

        canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
    }

    private void drawEventPoints(Canvas canvas, Paint paint, float startAngle, float anglePerDay, float inner, float outer) {
        DonutSegment ds = new DonutSegment(0, 360f, inner, outer, center, false);
        ds.draw(canvas, paintPool.getPaint(SELECTION_PAINT));
        int space = (int) ((outer - inner) / 5);
        startAngle = 90f;
        for (int i = 0; i < 365; i++) { // TODO daysinyear
            if (events != null && events.get(i) != null) {
                for (int j = 0; j < events.get(i).size(); j++) {
                    Paint dayPaint = new Paint();
                    dayPaint.setAntiAlias(true);
                    dayPaint.setStrokeCap(Paint.Cap.ROUND);
                    dayPaint.setStrokeWidth(12);

                    dayPaint.setColor(events.get(i).get(j).getColor());

                    Point p = GraphicHelpers.pointOnCircle(inner + (j * space), startAngle, center);
                    canvas.drawPoint(p.x, p.y, dayPaint);
                }
            }
            startAngle = startAngle + anglePerDay;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point touch = new Point(event.getX(), event.getY());

        float a = 2.0f;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(eventRing.intersects(touch)){    // TODO: wenn verschoben, intersect falsch
                    float angle =  GraphicHelpers.getAngleOfPoint(touch, center);
                    touched = true;

                    lastTouchAngle = angle;
//                    currentDrawAngle = angle;
//                    lastTouchAngle = angle;
//                    velocity = 0.0f;
//                    lastTime = System.currentTimeMillis();

                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                lastTouchAngle = getAngleOfPoint(touch, center);
//                float angle = getAngleOfPoint(touch, center);
//                float diff = angle-lastTouchAngle;
//
//                float dt = System.currentTimeMillis() - lastTime;
//
//
//                velocity = diff * 0.2f;
//
//                currentDrawAngle = currentDrawAngle + velocity;
//
//                pointerAngle += diff;
//                lastTouchAngle = angle;
//                lastTime = System.currentTimeMillis();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                pointerAngle = 90f;
                touched = false;
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                pointerAngle = 90f;
                touched = false;
                invalidate();
                return true;
            default:
                return false;
        }
    }

    public void setEvents(List<List<Event>> list) {
        this.events = list;
    }
}
