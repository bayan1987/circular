package steep.circular.view.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;

import steep.circular.util.GraphicHelpers;
import steep.circular.util.Point;




/**
 * Created by Tom Kretzschmar on 07.11.2016.
 *
 */

public class Marker extends Shape{

    private float angle;
    private float radius;
    private float height;
    private float width;
    private float cornerRadius;
    private Point center;

    private float l;
    private float t;
    private float r;
    private float b;


    public Marker(float angle, float radius, float height, float width, float cornerRadius, Point center) {
        this.angle = angle;
        this.radius = radius;
        this.height = height;
        this.width = width;
        this.center = center;
        this.cornerRadius = cornerRadius;

        l = center.x + radius;
        r = center.x + radius + width;
        t = center.y - (height/2);
        b = center.y + (height/2);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle, center.x, center.y);
        canvas.drawRoundRect(l,t,r,b, cornerRadius, cornerRadius, paint);
        canvas.restore();
    }
}
