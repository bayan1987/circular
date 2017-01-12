package steep.circular.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
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
import steep.circular.view.shapes.TextCircle;

import static steep.circular.view.PaintPool.DATE_PAINT;
import static steep.circular.view.PaintPool.DOT_PAINT;
import static steep.circular.view.PaintPool.POINTER_LINE_PAINT;
import static steep.circular.view.PaintPool.POINTER_TEXT_PAINT;
import static steep.circular.view.PaintPool.SELECTION_PAINT;

/**
 * Created by Tom Kretzschmar on 09.10.2016.
 *
 */

public class CircleCalendarView extends View {

    private static final int RAD_SEASON_IN = 70;
    private static final int RAD_SEASON_OUT = 80;
    private static final int RAD_MONTH_IN = 85;
    private static final int RAD_MONTH_OUT = 110;
    private static final int RAD_SELECTION_IN = 120;
    private static final int RAD_SELECTION_OUT = 150;

    private int radiusSeasonIn;
    private int radiusSeasonOut;
    private int radiusMonthIn;
    private int radiusMonthOut;
    private int radiusSelectionIn;
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

    private DonutSegment touchMarkerSegment;
    private DonutSegment[] seasonSegments;

    boolean touched = false;
    float currentDrawAngle = 0.0f;

    double lastTime;

    private List<List<Event>> events;

    Drawable calendar_circle;
    Drawable pointer;
    Drawable pointer_small;


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

    private Weekday calcWeekdayFromDayOfYear(int dayOfYear, Weekday firstDayOfYear) {
        int day = dayOfYear % 7;
        day = day + firstDayOfYear.getValue();
        return Weekday.valueOf(day);
    }

    private void init() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setElevation(10.0f);
//        }
        final float scale = getResources().getDisplayMetrics().density;
        radiusSeasonIn = (int) (RAD_SEASON_IN * scale + 0.5f);
        radiusSeasonOut = (int) (RAD_SEASON_OUT * scale + 0.5f);
        radiusMonthIn = (int) (RAD_MONTH_IN * scale + 0.5f);
        radiusMonthOut = (int) (RAD_MONTH_OUT * scale + 0.5f);
        radiusSelectionIn = (int) (RAD_SELECTION_IN * scale + 0.5f);
        radiusSelectionOut = (int) (RAD_SELECTION_OUT * scale + 0.5f);
        radiusSelectionOut = (int) (RAD_SELECTION_OUT * scale + 0.5f);

        paintPool = new PaintPool(this.getContext().getApplicationContext());

        calendar_circle = ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_circle);
        pointer = ContextCompat.getDrawable(getContext(), R.drawable.ic_pointer);
        pointer_small = ContextCompat.getDrawable(getContext(), R.drawable.ic_pointer_small);


        update(null); // must be called before initDrawingObjects
    }

    private void initDrawingObjects() {
        float angle = 0;
        touchMarkerSegment = new DonutSegment(angle, 90, radiusSelectionIn, radiusSelectionOut, center, false);

        float startAngle = ((Month.October.getDayCount() + Month.November.getDayCount()) * anglePerDay) - anglePerDay;
        seasonSegments = new DonutSegment[4];
        for (int i = 0; i < 4; i++) {
            float sweepAngle = 90;
            seasonSegments[i] = new DonutSegment(startAngle, sweepAngle, radiusSeasonIn, radiusSeasonOut, center, false);
            startAngle += sweepAngle;
        }

    }

    public void update(List<List<Event>> events) {
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

        center = new Point(w / 2, h / 2);

        initDrawingObjects();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMonths(canvas);

//        if (touched) {
//            double dt = System.currentTimeMillis() - lastTime;
//
//            float diff = lastTouchAngle - currentDrawAngle;
//
//            if(diff > 180) diff = diff-360;
//            else if(diff < - 180) diff = diff+360;
//
//            velocity = (float) (diff * dt * 0.008f); //0.008
//            currentDrawAngle += velocity;
//
//            float angle = currentDrawAngle - 45;
//
//            touchMarkerSegment.setAngle(angle);
//            touchMarkerSegment.draw(canvas, paintPool.getPaint(SELECTION_PAINT_DARK));
//            lastTime = System.currentTimeMillis();
//        }

        drawEventPoints(canvas, radiusSelectionIn + 10, radiusSelectionOut - 10);
        drawPointer(canvas);
        drawDate(canvas);
    }

    private void drawDate(Canvas canvas) {
        Paint textPaint = paintPool.getPaint(DATE_PAINT);
        String text1 = "" + currentDayOfMonth;
        String text2 = "" + (currentMonth + 1);
        String text3 = "" + currentYear;

        float xStart1 = center.x - (textPaint.measureText(text1) / 2);
        float xStart2 = center.x - (textPaint.measureText(text2) / 2);
        float xStart3 = center.x - (textPaint.measureText(text3) / 2);
        canvas.drawText(text1, xStart1, center.y - textPaint.getTextSize(), textPaint);
        canvas.drawText(text2, xStart2, center.y, textPaint);
        canvas.drawText(text3, xStart3, center.y + textPaint.getTextSize(), textPaint);
    }

    private void drawMonths(Canvas canvas) {
        float startAngle = 90;

        calendar_circle.setBounds((int) center.x-radiusSelectionOut, (int) center.y-radiusSelectionOut, (int) center.x+radiusSelectionOut,  (int) center.y+radiusSelectionOut);
        calendar_circle.draw(canvas);

        for (Month month : Month.values()) {
            float sweepAngle;
            if (month == Month.February) {
                int dayCount = isLeapYear ? month.getDayCount() : month.getDayCount() + 1;
                sweepAngle = dayCount * anglePerDay;
            } else {
                sweepAngle = month.getDayCount() * anglePerDay;
            }

            DonutSegment segMonth = new DonutSegment(startAngle, sweepAngle, radiusMonthIn, radiusMonthOut, center, false);

            PathMeasure pm = new PathMeasure(segMonth.getTextPath(), false);
            float length = pm.getLength();
            String text = (String.valueOf(month.name().substring(0, 3)));
            Paint textPaint = paintPool.getPaint(PaintPool.LINE_PAINT);
            float tWidth = textPaint.measureText(text);
            textPaint.setColor(Color.BLACK);
            canvas.drawTextOnPath(text, segMonth.getTextPath(), (length - tWidth) / 2f, textPaint.getTextSize(), textPaint);

            startAngle += sweepAngle;
        }


    }

    private void drawPointer(Canvas canvas) {
        float angle = currentDayOfYear * anglePerDay + 90;

        Point start = GraphicHelpers.pointOnCircle(radiusSelectionOut + 40, angle, center);
        float rad = 30;

        TextCircle tc = new TextCircle(angle, radiusSelectionOut + 30 + rad, String.valueOf(currentDayOfMonth), center, rad, paintPool.getPaint(POINTER_LINE_PAINT), paintPool.getPaint(POINTER_TEXT_PAINT));



        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle, pointer.getBounds().centerX(), pointer.getBounds().centerY());
        pointer.setBounds((int)start.x-70, (int)start.y-70, (int)start.x+70, (int)start.y+70);
        pointer.draw(canvas);
        canvas.restore();

        tc.draw(canvas);

        angle = angle - anglePerDay * 14;
        start = GraphicHelpers.pointOnCircle(radiusSelectionOut + 3, angle, center);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle, pointer_small.getBounds().centerX(), pointer_small.getBounds().centerY());
        pointer_small.setBounds((int)start.x-30, (int)start.y-30, (int)start.x+30, (int)start.y+30);
        pointer_small.draw(canvas);
        canvas.restore();

    }

    // TODO draw in bitmap/etc. to get better performance
    private void drawEventPoints(Canvas canvas, float inner, float outer) {
        DonutSegment ds = new DonutSegment(0, 360f, inner, outer, center, false);
        ds.draw(canvas, paintPool.getPaint(SELECTION_PAINT));
        int space = (int) ((outer - inner) / 5);
        float startAngle = 90f;
        for (int i = 0; i < 365; i++) { // TODO daysinyear
            if (events != null && events.get(i) != null) {
                for (int j = 0; j < events.get(i).size(); j++) {
                    Paint dotPaint = paintPool.getPaint(DOT_PAINT);
//                    dotPaint.setColor(events.get(i).get(j).getColor());

                    Point p = GraphicHelpers.pointOnCircle(inner + (j * space), startAngle, center);
                    canvas.drawPoint(p.x, p.y, dotPaint);
                }
            }
            startAngle = startAngle + anglePerDay;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point touch = new Point(event.getX(), event.getY());

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (eventRing.intersects(touch)) {
//                    float angle = GraphicHelpers.getAngleOfPoint(touch, center);
//                    touched = true;
//
//                    lastTouchAngle = angle;
//                    currentDrawAngle = angle;
//
//                    lastTime = System.currentTimeMillis();
//
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                    return true;
//                } else {
//                    return false;
//                }
//            case MotionEvent.ACTION_MOVE:
//                lastTouchAngle = getAngleOfPoint(touch, center);
//
//                invalidate();
//                return true;
//            case MotionEvent.ACTION_UP:
//                pointerAngle = 90f;
//                touched = false;
//                invalidate();
//
//                return true;
//            case MotionEvent.ACTION_CANCEL:
//                getParent().requestDisallowInterceptTouchEvent(false);
//                pointerAngle = 90f;
//                touched = false;
//                invalidate();
//                return true;
//            default:
//                return false;
//        }
        return false;
    }

    public void setEvents(List<List<Event>> list) {
        this.events = list;
    }
}
