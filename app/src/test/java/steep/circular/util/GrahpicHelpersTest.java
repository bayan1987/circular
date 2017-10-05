package steep.circular.util;

import org.junit.Test;

/**
 * Created by Tom Kretzschmar on 09.11.2016.
 *
 */

public class GrahpicHelpersTest{

//    @Test
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

    @Test
    public void testGetPointOnCircle(){
        System.out.println(GraphicHelpers.pointOnCircle(1, 90, new Point(2,2)));
        System.out.println(GraphicHelpers.pointOnCircle(1, 90));

        System.out.println(GraphicHelpers.pointOnCircle(1, 180));

        System.out.println(GraphicHelpers.pointOnCircle(1, 270));

        System.out.println(GraphicHelpers.pointOnCircle(1, 0));
    }

//    @Test
    public void testGetDistance(){
        Point center = new Point(0,0);
        Point point = new Point(2,0);

        float dist = GraphicHelpers.getDistance(point, center);
        System.out.println("Distance: " + dist);


        center = new Point(0,0);
        point = new Point(1,1);

        dist = GraphicHelpers.getDistance(point, center);
        System.out.println("Distance: " + dist);
    }
}
