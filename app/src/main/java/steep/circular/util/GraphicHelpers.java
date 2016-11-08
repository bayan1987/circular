package steep.circular.util;


import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * Created by Tom Kretzschmar on 02.11.2016.
 *
 */

public class GraphicHelpers {

    public static Point pointOnCircle(float radius, float angle){
        return pointOnCircle(radius, angle, new Point(0,0));
    }

    public static Point pointOnCircle(float radius, float angle, Point center){
        float x = center.x + (float) cos(toRadians(angle)) * (radius);
        float y = center.y + (float) sin(toRadians(angle)) * (radius);

        return new Point(x,y);
    }
}
