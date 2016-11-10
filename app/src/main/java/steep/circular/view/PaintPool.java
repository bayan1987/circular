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
    public static final int TEXT_PAINT = 3;
    public static final int TEXTW_PAINT = 4;
    public static final int DAY_PAINT = 5;
    public static final int WEEKEND_PAINT = 6;
    public static final int VACATION_PAINT = 7;

    private HashMap<Integer, Paint> paintMap;

    public PaintPool(Context context) {
        paintMap = new HashMap<>();

        Paint linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(context, R.color.colorLine));
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(5);
        paintMap.put(LINE_PAINT, linePaint);

        Paint selectionPaint = new Paint();
        selectionPaint.setColor(ContextCompat.getColor(context, R.color.colorSelection));
        paintMap.put(SELECTION_PAINT, selectionPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(context, R.color.colorText));
        textPaint.setTextSize(25);
        paintMap.put(TEXT_PAINT, textPaint);

        Paint textPaintWhite = new Paint();
        textPaintWhite.setColor(ContextCompat.getColor(context, R.color.colorTextW));
        textPaintWhite.setTextSize(60);
        textPaintWhite.setFakeBoldText(true);
        paintMap.put(TEXTW_PAINT, textPaintWhite);

        Paint dayPaint = new Paint();
        dayPaint.setColor(ContextCompat.getColor(context, R.color.colorPointer));
        dayPaint.setAntiAlias(true);
        dayPaint.setStrokeWidth(15);
        paintMap.put(DAY_PAINT, dayPaint);

        Paint weekendPaint = new Paint();
        weekendPaint.setColor(ContextCompat.getColor(context, R.color.colorWeekend));
        paintMap.put(WEEKEND_PAINT, weekendPaint);

        Paint vacationPaint = new Paint();
        vacationPaint.setColor(ContextCompat.getColor(context, R.color.colorVacation));
        paintMap.put(VACATION_PAINT, vacationPaint);
    }

    public Paint getPaint(int paintType){
        return paintMap.get(paintType);
    }
}
