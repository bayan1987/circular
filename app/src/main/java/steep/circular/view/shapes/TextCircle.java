package steep.circular.view.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import steep.circular.util.GraphicHelpers;
import steep.circular.util.Point;

/**
 * Created by Tom Kretzschmar on 12.12.2016.
 *
 */

public class TextCircle {


    private float angle;
    private float radius;
    private String text;
    private Point center;
    private float circleRadius;
    private Paint paintC;
    private Paint paintT;
    private Point centerText;
    private Point centerCircle;

    public TextCircle(float angle, float radius, String text, Point center, float circleRadius, Paint paintC, Paint paintT) {
        this.angle = angle;
        this.radius = radius;
        this.text = text;
        this.center = center;
        this.circleRadius = circleRadius;
        this.paintC = paintC;
        this.paintT = paintT;

        Rect bounds = new Rect();
        paintT.getTextBounds(text, 0, text.length(), bounds);

        centerText = new Point(-(bounds.width()/1.8f), (bounds.height()/2f));
        centerCircle = GraphicHelpers.pointOnCircle(radius, angle, center);
    }

    public void draw(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(centerCircle.x, centerCircle.y);
        canvas.drawCircle(0, 0, circleRadius, paintC);
        canvas.drawText(text, centerText.x, centerText.y, paintT);
        canvas.restore();
    }
}
