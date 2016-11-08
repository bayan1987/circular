package steep.circular.view.shapes;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by Tom  Kretzschmar on 26.10.2016.
 *
 */

public class DonutSegment extends Shape {

    private float outerSweepAngle;
    private float innerSweepAngle;
    private float outerStartAngle;
    private float innerStartAngle;
    private float inner;
    private float outer;
    private int x;
    private int y;

    public DonutSegment(float startAngle, float sweepAngle, float inner, float outer, int x, int y, boolean correction) {
        this.inner = inner;
        this.outer = outer;
        this.x = x;
        this.y = y;

        if(correction) {
            float correctionValueI = 4.0f;
            float correctionValueO = (correctionValueI * inner) / outer;
            outerSweepAngle = sweepAngle - correctionValueO;
            innerSweepAngle = sweepAngle - correctionValueI;
            outerStartAngle = startAngle + correctionValueO+1f;
            innerStartAngle = startAngle + correctionValueI; // - 4;
        } else {
            outerSweepAngle = sweepAngle;
            innerSweepAngle = sweepAngle;
            outerStartAngle = startAngle;
            innerStartAngle = startAngle;
        }
    }

    @Override
    protected void onResize(float width, float height) {
        super.onResize(width, height);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Path segment = new Path();
        segment.arcTo(x-inner, y-inner, x+inner, y+inner, innerStartAngle, innerSweepAngle, false);
        segment.arcTo(x-outer, y-outer, x+outer, y+outer, outerStartAngle+outerSweepAngle, -outerSweepAngle, false);
        canvas.drawPath(segment, paint);
    }

    public Path getTextPath(){
        Path textPath = new Path();
        textPath.arcTo(x-outer, y-outer, x+outer, y+outer, outerStartAngle, outerSweepAngle, false);
        return textPath;
    }
}
