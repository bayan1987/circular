package steep.circular.util;


import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Created by Tom Kretzschmar on 02.11.2016.
 *
 */

public class GraphicHelpers {

    public static Point pointOnCircle(float radius, float angle){
        return pointOnCircle(radius, angle, new Point(0,0));
    }

    /**
     *
     * @param radius
     * @param angle Angle in degrees
     * @param center
     * @return Point
     */
    public static Point pointOnCircle(float radius, float angle, Point center){
        float x = center.x + (float) cos(toRadians(angle)) * (radius);
        float y = center.y + (float) sin(toRadians(angle)) * (radius);

        x = x < 1E-15 ? 0 : x;
        y = y < 1E-15 ? 0 : y;

        return new Point(x,y);
    }

    public static float getAngleOfPoint(Point point, Point center){
        float rad = (float) atan2((point.y - center.y), (point.x - center.x));
        float angle = (float) toDegrees(rad);
        if(angle < 0.0f)
            angle += 360.0;
        return angle;
    }

    public static float getDistance(Point point, Point center){
        float x = point.x-center.x;
        float y = point.y-center.y;
        return (float) sqrt((x*x)+(y*y));
    }
}
