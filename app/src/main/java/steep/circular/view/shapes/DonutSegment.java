package steep.circular.view.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

import steep.circular.util.GraphicHelpers;
import steep.circular.util.Point;

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
    private Point center;

    public DonutSegment(float startAngle, float sweepAngle, float inner, float outer, Point center, boolean correction) {
        this.inner = inner;
        this.outer = outer;
        this.center = center;

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

    /**
     * Draw this shape into the provided Canvas, with the provided Paint.
     * Before calling this, you must call {@link #resize(float, float)}.
     *
     * @param canvas the Canvas within which this shape should be drawn
     * @param paint  the Paint object that defines this shape's characteristics
     */
    @Override
    public void draw(Canvas canvas, Paint paint) {
        Path segment = new Path();
        segment.arcTo(center.x-inner, center.y-inner, center.x+inner, center.y+inner, innerStartAngle, innerSweepAngle, false);
        segment.arcTo(center.x-outer, center.y-outer, center.x+outer, center.y+outer, outerStartAngle+outerSweepAngle, -outerSweepAngle, false);
        canvas.drawPath(segment, paint);
    }

    public Path getTextPath(){
        Path textPath = new Path();
        textPath.arcTo(center.x-outer, center.y-outer, center.x+outer, center.y+outer, outerStartAngle, outerSweepAngle, false);
        return textPath;
    }

    public boolean intersects(Point point){
        float angle = GraphicHelpers.getAngleOfPoint(point, center);
        float radius = GraphicHelpers.getDistance(point, center);

        if((outerStartAngle%360) < angle && angle < (outerSweepAngle%360) && inner < radius && radius < outer){
            return true;
        } else {
            return false;
        }
    }

    public void setAngle(float angle){
        outerStartAngle = angle;
        innerStartAngle = angle;
    }
}
