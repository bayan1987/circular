package steep.circular.util;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Created by Tom Kretzschmar on 09.11.2016.
 *
 */

public class GrahpicHelpersTest extends TestCase{

    @Test
    public void testGetAngleOfPoint(){

        Point center = new Point(0,0);
        Point point = new Point(1,0);

        float angle = GraphicHelpers.getAngleOfPoint(point, center);    // 0.0
        System.out.println(angle);



        center = new Point(0,0);
        point = new Point(1,1);

        angle = GraphicHelpers.getAngleOfPoint(point, center);  // 45

        System.out.println(angle);



        center = new Point(0,0);
        point = new Point(0,1);

        angle = GraphicHelpers.getAngleOfPoint(point, center); // 90

        System.out.println(angle);



        center = new Point(0,0);
        point = new Point(-1,0);

        angle = GraphicHelpers.getAngleOfPoint(point, center);  // -0.0

        System.out.println(angle);



        center = new Point(0,0);
        point = new Point(-1,-1);

        angle = GraphicHelpers.getAngleOfPoint(point, center);  // 45

        System.out.println(angle);



        center = new Point(0,0);
        point = new Point(0,-1);

        angle = GraphicHelpers.getAngleOfPoint(point, center); // - 90

        System.out.println(angle);
    }
}
