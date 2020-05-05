/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {

    private SET<Point2D> set;

    // construct an empty set of points
    public PointSET() {
        set = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return set.size() == 0;
    }

    // number of points in the set
    public int size() {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : set) {
            p.draw();
        }
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new java.lang.IllegalArgumentException();
        }

        SET<Point2D> rectSet = new SET<Point2D>();
        for (Point2D p : set) {
            if (rect == null || rect.contains(p)) {
                rectSet.add(p);
            }
        }
        return rectSet;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }

        Point2D nearestPoint = null;
        double minDistance = 1.1;

        for (Point2D currentPoint : set) {
            double d = p.distanceSquaredTo(currentPoint);
            if (d < minDistance) {
                minDistance = d;
                nearestPoint = currentPoint;
            }
        }

        return nearestPoint;
    }

    public static void main(String[] args) {

        PointSET set = new PointSET();

        String fileName = "input10.txt";
        In in = new In(fileName);

        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            set.insert(new Point2D(x, y));
        }

        StdDraw.setPenRadius(0.005);
        set.draw();

        RectHV rect = new RectHV(0.25, 0.25, 0.75, 0.75);

        StdDraw.setPenColor(StdDraw.BLUE);
        rect.draw();

        StdDraw.setPenColor(StdDraw.RED);
        for (Point2D p : set.range(rect)) {
            p.draw();
        }

        Point2D[] points = new Point2D[]{
            new Point2D(0.25, 0.25),
            new Point2D(0.25, 0.75),
            new Point2D(0.75, 0.75),
            new Point2D(0.75, 0.25)
        };

        for (Point2D currentPoint : points) {
            Point2D nearestPoint = set.nearest(currentPoint);

            StdDraw.setPenRadius(0.01);
            StdDraw.setPenColor(StdDraw.GREEN);
            nearestPoint.draw();

            StdDraw.setPenRadius(0.002);
            StdDraw.line(nearestPoint.x(), nearestPoint.y(), currentPoint.x(), currentPoint.y());

            System.out.printf("Nearest to %s is %s\n", currentPoint, nearestPoint);
        }

        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLACK);

        testGraderTest7();

        System.out.println("Tests finished");

    }

    private static void testGraderTest7()
    {
        String testName = "testGraderFailOnTest7";

        PointSET set = new PointSET();

        Point2D p1 = new Point2D(1.0, 0.0);
        set.insert(p1);

        Point2D q1 = new Point2D(0.0, 0.0);
        Point2D nearest = set.nearest(q1);

        assertEquals(p1, nearest, String.format("Nearest must be %s insteadof %s", p1, nearest), testName);

    }

    private static void assertEquals(Point2D expected, Point2D tested, String message, String testName) {
        if (!expected.equals(tested)) {
            System.out.printf("FAIL (%s): %s\n", testName, message);
        }
    }
}
