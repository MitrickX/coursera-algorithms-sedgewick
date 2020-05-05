/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;

public class Point implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        if (this.x == that.x && this.y == that.y) {
            return Double.NEGATIVE_INFINITY;
        } else if (this.x == that.x) {
            return Double.POSITIVE_INFINITY;
        } else if (this.y == that.y){
            return +0.0;
        } else {
            return ((double)(that.y - this.y)) / (that.x - this.x);
        }
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that) {
        if (this.x == that.x && this.y == that.y) {
            return 0;
        } else if (this.y < that.y || (this.y == that.y && this.x < that.x)) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        Point self = this;
        return new Comparator<Point>() {
            public int compare(Point o1, Point o2) {
                double slope1 = self.slopeTo(o1);
                double slope2 = self.slopeTo(o2);
                return Double.compare(slope1, slope2);
            }
        };
    }


    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {

        Point origin, p1, p2;
        Double slope;

        origin = new Point(0, 0);
        p1 = new Point(1, 0);
        p2 = new Point(1, 0);

        if (p1.compareTo(p2) != 0) {
            System.out.printf("FAIL: %s, %s are must be equal\n", p1, p2);
        }

        if (p1.slopeTo(p2) != Double.NEGATIVE_INFINITY) {
            System.out.printf("FAIL:%s, %s, slope must be NEGATIVE_INFINITY\n", p1, p2);
        }

        if (origin.slopeOrder().compare(p1, p2) != 0) {
            System.out.printf("FAIL: %s, %s are must be slopeOrder(%s) equal\n", p1, p2, origin);
        }

        p1 = new Point(1, 2);
        p2 = new Point(1, 34);

        if (p1.compareTo(p2) != -1) {
            System.out.printf("FAIL: %s, %s first point below second\n", p1, p2);
        }

        if (p1.slopeTo(p2) != Double.POSITIVE_INFINITY) {
            System.out.printf("FAIL:%s, %s, slope must be POSITIVE_INFINITY\n", p1, p2);
        }

        if (origin.slopeOrder().compare(p1, p2) != -1) {
            System.out.printf("FAIL: %s, %s, first point must slopeOrder(%s) lower\n", p1, p2, origin);
        }

        p1 = new Point(15, 12);
        p2 = new Point(2, 12);

        if (p1.compareTo(p2) != 1) {
            System.out.printf("FAIL: %s, %s first point upper second\n", p1, p2);
        }

        if (p1.slopeTo(p2) != +0.0) {
            System.out.printf("FAIL:%s, %s, slope must be +0.0 (horizontal)\n", p1, p2);
        }

        if (origin.slopeOrder().compare(p1, p2) != -1) {
            System.out.printf("FAIL: %s, %s, first point must slopeOrder(%s) lower\n", p1, p2, origin);
        }

        p1 = new Point(0, 1);
        p2 = new Point(0, 10);

        if (p1.compareTo(p2) != -1) {
            System.out.printf("FAIL: %s, %s first point lower second\n", p1, p2);
        }

        if (p1.slopeTo(p2) != Double.POSITIVE_INFINITY) {
            System.out.printf("FAIL:%s, %s, slope must be POSITIVE_INFINITY (vertical)\n", p1, p2);
        }

        if (origin.slopeOrder().compare(p1, p2) != 0) {
            System.out.printf("FAIL: %s, %s are must be slopeOrder(%s) equal (cause they are all on OY axis)\n", p1, p2, origin);
        }

        p1 = new Point(2, 0);
        p2 = new Point(1, 0);

        if (p1.compareTo(p2) != 1) {
            System.out.printf("FAIL: %s, %s first point lower (righter) than second\n", p1, p2);
        }

        if (p1.slopeTo(p2) != +0.0) {
            System.out.printf("FAIL:%s, %s, slope must be +0.0 (horizontal)\n", p1, p2);
        }

        if (origin.slopeOrder().compare(p1, p2) != 0) {
            System.out.printf("FAIL: %s, %s are must be slopeOrder(%s) equal (cause they are all on OX axis)\n", p1, p2, origin);
        }

        p1 = new Point(2, 5);
        p2 = new Point(-1, -7);

        if (p1.compareTo(p2) != 1) {
            System.out.printf("FAIL: %s, %s first point upper than second\n", p1, p2);
        }

        slope = p1.slopeTo(p2);
        if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY || slope == +0.0) {
            System.out.printf("FAIL:%s, %s, slope not horizontal, not vertical, and points are not the same\n", p1, p2);
        }

        if (slope <= 0) {
            System.out.printf("FAIL:%s, %s, slope must be positive - cause angle between in (0, pi/2)) \n", p1, p2);
        }

        p1 = new Point(2, 5);
        p2 = new Point(-1, 7);

        if (p1.compareTo(p2) != -1) {
            System.out.printf("FAIL: %s, %s first point below than second\n", p1, p2);
        }

        slope = p1.slopeTo(p2);
        if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY || slope == +0.0) {
            System.out.printf("FAIL:%s, %s, slope %f must not horizontal, not vertical, and points are not the same\n", p1, p2, slope);
        }

        if (slope >= 0) {
            System.out.printf("FAIL:%s, %s, slope %f must be negative - cause angle between in (3pi/2, 2pi)) \n", p1, p2, slope);
        }

        p1 = new Point(2, 5);
        p2 = new Point(3, -1);

        if (p1.compareTo(p2) != 1) {
            System.out.printf("FAIL: %s, %s first point upper than second\n", p1, p2);
        }

        slope = p1.slopeTo(p2);
        if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY || slope == +0.0) {
            System.out.printf("FAIL:%s, %s, slope not horizontal, not vertical, and points are not the same\n", p1, p2);
        }

        if (slope >= 0) {
            System.out.printf("FAIL:%s, %s, slope must be negative - cause angle between in (pi/2, pi)) \n", p1, p2);
        }

        p1 = new Point(2, 5);
        p2 = new Point(7, 10);

        if (p1.compareTo(p2) != -1) {
            System.out.printf("FAIL: %s, %s first point lower than second\n", p1, p2);
        }

        slope = p1.slopeTo(p2);
        if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY || slope == +0.0) {
            System.out.printf("FAIL:%s, %s, slope not horizontal, not vertical, and points are not the same\n", p1, p2);
        }

        if (slope <= 0) {
            System.out.printf("FAIL:%s, %s, slope must be positive - cause angle between in (pi, 3pi/2)) \n", p1, p2);
        }

    }
}
