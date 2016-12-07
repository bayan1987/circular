package steep.circular.view.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

import steep.circular.util.GraphicHelpers;
import steep.circular.util.Point;

/**
 * Created by Tom Kretzschmar on 07.12.2016.
 *
 */

public class Ring extends Shape{

    private float inner;
    private float outer;
    private Point center;

    public Ring(float inner, float outer, Point center) {
        this.inner = inner;
        this.outer = outer;
        this.center = center;
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
        Path donut = new Path();
        donut.addOval(center.x - inner, center.y - inner, center.x + inner, center.y + inner, Path.Direction.CW);
        donut.addOval(center.x - outer, center.y - outer, center.x + outer, center.y + outer, Path.Direction.CCW);
        canvas.drawPath(donut, paint);
    }

    public boolean intersects(Point point){
        float radius = GraphicHelpers.getDistance(point, center);

        if(inner < radius && radius < outer){
            return true;
        } else {
            return false;
        }
    }
}
