package steep.circular.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;

import steep.circular.R;


/**
 * Created by Tom Kretzschmar on 10.11.2016.
 *
 */

public class PaintPool {

    public static final int LINE_PAINT = 0;
    //    public static final int SWEEP_PAINT = 1;
    public static final int SELECTION_PAINT = 2;


    public static final int WINTER_PAINT = 8;
    public static final int SPRING_PAINT = 9;
    public static final int SUMMER_PAINT = 10;
    public static final int AUTUMN_PAINT = 11;

    public static final int DATE_PAINT = 12;

    public static final int SELECTION_PAINT_DARK = 13;
    public static final int POINTER_LINE_PAINT = 14;
    public static final int POINTER_TEXT_PAINT = 15;

    private HashMap<Integer, Paint> paintMap;

    public PaintPool(Context context) {
        paintMap = new HashMap<>();

        Paint linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(context, R.color.colorBackground));
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(5);
        linePaint.setTextSize(40);
        paintMap.put(LINE_PAINT, linePaint);

        Paint selectionPaint = new Paint();
        selectionPaint.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        selectionPaint.setAntiAlias(true);
        paintMap.put(SELECTION_PAINT, selectionPaint);

        Paint selectionPaintDark = new Paint();
        selectionPaintDark.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        selectionPaintDark.setAntiAlias(true);
        selectionPaintDark.setStrokeWidth(3);
        paintMap.put(SELECTION_PAINT_DARK, selectionPaintDark);


        Paint springPaint = new Paint();
        springPaint.setColor(ContextCompat.getColor(context, R.color.colorSpring));
        springPaint.setAntiAlias(true);
        paintMap.put(SPRING_PAINT, springPaint);

        Paint summerPaint = new Paint();
        summerPaint.setColor(ContextCompat.getColor(context, R.color.colorSummer));
        summerPaint.setAntiAlias(true);
        paintMap.put(SUMMER_PAINT, summerPaint);

        Paint autumnPaint = new Paint();
        autumnPaint.setColor(ContextCompat.getColor(context, R.color.colorAutumn));
        autumnPaint.setAntiAlias(true);
        paintMap.put(AUTUMN_PAINT, autumnPaint);

        Paint winterPaint = new Paint();
        winterPaint.setColor(ContextCompat.getColor(context, R.color.colorWinter));
        winterPaint.setAntiAlias(true);
        paintMap.put(WINTER_PAINT, winterPaint);

        Paint datePaint = new Paint();
        datePaint.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        datePaint.setAntiAlias(true);
        datePaint.setTextSize(60);
        datePaint.setStrokeWidth(3);
        datePaint.setStrokeCap(Paint.Cap.ROUND);
        paintMap.put(DATE_PAINT, datePaint);

        Paint pointerLinePaint = new Paint();
        pointerLinePaint.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        pointerLinePaint.setAntiAlias(true);
        pointerLinePaint.setStrokeWidth(4);
        pointerLinePaint.setTextSize(40);
//        pointerLinePaint.setShadowLayer(10, 0f, 0f, Color.BLACK);
        pointerLinePaint.setStrokeCap(Paint.Cap.ROUND);
        pointerLinePaint.setStyle(Paint.Style.STROKE);
        paintMap.put(POINTER_LINE_PAINT, pointerLinePaint);

        Paint pointerTextPaint = new Paint();
        pointerTextPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        pointerTextPaint.setAntiAlias(true);
        pointerTextPaint.setTextSize(40);
//        pointerTextPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        pointerTextPaint.setStrokeCap(Paint.Cap.ROUND);
        paintMap.put(POINTER_TEXT_PAINT, pointerTextPaint);
    }

    public Paint getPaint(int paintType) {
        return paintMap.get(paintType);
    }
}
