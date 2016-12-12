package steep.circular.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import steep.circular.view.shapes.TextCircle;

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

    private float lastTouchAngle = 0f;

    private float pointerAngle = 90f;
    private PaintPool paintPool;
    private Ring eventRing;

    private Bitmap cache;

    private DonutSegment seg;

    boolean touched = false;
    float currentDrawAngle = 0.0f;
    float velocity;

    double lastTime;

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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setElevation(10.0f);
//        }
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


        if(touched){
            double dt = System.currentTimeMillis() - lastTime;
//            Log.d("dt", "DT:" + dt + "|" + System.currentTimeMillis() + "-" + lastTime);
            float diff = lastTouchAngle - currentDrawAngle;
//            if(diff > 180) diff = 180 - diff;
//            else if(diff < - 180) diff = 180 + diff;
//
            velocity = (float)(diff * dt * 0.008f); //0.008
            currentDrawAngle += velocity ;
//
//
//            if(currentDrawAngle < 0) currentDrawAngle = 360 - currentDrawAngle;
//            if(currentDrawAngle > 360) currentDrawAngle = currentDrawAngle - 360;
            float angle = currentDrawAngle - 45;

            seg = new DonutSegment(angle, 90, radiusSelectionIn, radiusSelectionMid, center, false);
            seg.draw(canvas, paintPool.getPaint(SELECTION_PAINT_DARK));
            lastTime = System.currentTimeMillis();

        }

        drawEventPoints(canvas, null, startAngle, anglePerDay, radiusSelectionIn+10, radiusSelectionMid-10);
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
        float startAngle = ((Month.October.getDayCount() + Month.November.getDayCount()) * anglePerDay) - anglePerDay ;
        int startSeason = PaintPool.WINTER_PAINT;
        for(int i = 0; i<4; i++){
            float sweepAngle = 90;
            DonutSegment segSeason = new DonutSegment(startAngle, sweepAngle, radiusSeasonIn, radiusSeasonOut, center, false);
            segSeason.draw(canvas, paintPool.getPaint(startSeason + i));
            startAngle += sweepAngle;
        }
    }

    private void drawMonths(Canvas canvas){
        float startAngle = 90;

        Ring r = new Ring(radiusMonthIn, radiusMonthOut, center);
        r.draw(canvas, paintPool.getPaint(SELECTION_PAINT));
        for(Month month : Month.values()) {
            float sweepAngle;
            if(month == Month.February){
                int dayCount = isLeapYear ? month.getDayCount() : month.getDayCount() + 1;
                sweepAngle = dayCount * anglePerDay;
            } else {
                sweepAngle = month.getDayCount() * anglePerDay;
            }

            Point start = GraphicHelpers.pointOnCircle(radiusMonthIn+5, startAngle, center);
            Point stop = GraphicHelpers.pointOnCircle(radiusMonthOut-5, startAngle, center);
            canvas.drawLine(start.x, start.y, stop.x, stop.y, paintPool.getPaint(SELECTION_PAINT_DARK));

            DonutSegment segMonth = new DonutSegment(startAngle, sweepAngle, radiusMonthIn, radiusMonthOut, center, false);
//            segMonth.draw(canvas, paintPool.getPaint(month.getSeason()));


            PathMeasure pm = new PathMeasure(segMonth.getTextPath(), false);
            float length = pm.getLength();
            String text = (String.valueOf(month.name().substring(0,3)));
            Paint textPaint = paintPool.getPaint(PaintPool.LINE_PAINT);
            float tWidth = textPaint.measureText(text);
            canvas.drawTextOnPath(text, segMonth.getTextPath(), (length-tWidth)/2f, textPaint.getTextSize(), textPaint);

            startAngle += sweepAngle;
        }
    }

    private void drawPointer(Canvas canvas){

        float angle = currentDayOfYear * anglePerDay + 90;

        Point start = GraphicHelpers.pointOnCircle(radiusSelectionMid + 20, angle, center);
        Point stop = GraphicHelpers.pointOnCircle(radiusSeasonIn -20, angle, center);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setTextSize(40);
//        paint.setShadowLayer(10, 0f, 0f, Color.BLACK);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        Paint paint2 = new Paint();
        paint2.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        paint2.setAntiAlias(true);
        paint2.setTextSize(40);
//        paint2.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        paint2.setStrokeCap(Paint.Cap.ROUND);

        canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);
        float rad = 30;

        TextCircle tc = new TextCircle(angle, radiusSelectionMid + 20 + rad, String.valueOf(currentDayOfMonth), center, rad, paint, paint2);
        tc.draw(canvas);

    }

    // TODO draw in bitmap/etc. to get better performance
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

//                    dayPaint.setColor(events.get(i).get(j).getColor());
                    dayPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

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
                    currentDrawAngle = angle;

                    lastTime = System.currentTimeMillis();

                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                lastTouchAngle = getAngleOfPoint(touch, center);

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
