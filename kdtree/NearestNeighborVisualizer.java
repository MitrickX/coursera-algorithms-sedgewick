import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;

/******************************************************************************
 *  Compilation:  javac NearestNeighborVisualizer.java
 *  Execution:    java NearestNeighborVisualizer input.txt
 *  Dependencies: PointSET.java KdTree.java
 *
 *  Read points from a file (specified as a command-line argument) and
 *  draw to standard draw. Highlight the closest point to the mouse.
 *
 *  The nearest neighbor according to the brute-force algorithm is drawn
 *  in red; the nearest neighbor using the kd-tree algorithm is drawn in blue.
 *
 ******************************************************************************/

public class NearestNeighborVisualizer {

    public static void main(String[] args) {

        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            brute.insert(p);
        }

        // process nearest neighbor queries
        StdDraw.enableDoubleBuffering();

        Point2D lastQuery = null;

        while (true) {

            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            if (lastQuery != null && lastQuery.equals(query)) {
                continue;
            }

            lastQuery = query;

            Point2D nearestBrute = null;
            Point2D nearestKd = null;

                    // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            brute.draw();

            // draw in red the nearest neighbor (using brute-force algorithm)
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.RED);
            nearestBrute = brute.nearest(query);
            nearestBrute.draw();
            StdDraw.setPenRadius(0.02);


            // draw in blue the nearest neighbor (using kd-tree algorithm)
            StdDraw.setPenColor(StdDraw.BLUE);
            nearestKd = kdtree.nearest(query);
            nearestKd.draw();
            StdDraw.show();
            StdDraw.pause(40);

            if (!nearestBrute.equals(nearestKd)) {
                System.out.printf("Nearest (brute force) %s not equals neares (kd-tree) %s\n", nearestBrute, nearestKd);
                System.out.printf("Nearest (brute force) distance with query point is %f\n", nearestBrute.distanceSquaredTo(query));
                System.out.printf("Nearest (kd-tree) distance with query point is %f\n", nearestKd.distanceSquaredTo(query));
                System.out.printf("Query point is %s\n", query);
            }

        }

    }
}
