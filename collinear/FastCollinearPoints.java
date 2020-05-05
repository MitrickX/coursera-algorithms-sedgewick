import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class FastCollinearPoints {
    private Point[] points;
    private LineSegment[] segments;

    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }

        int N = points.length;
        for (int i = 0; i < N; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException();
            }
        }

        this.points = new Point[N];
        this.points = Arrays.copyOf(points, N);

        Arrays.sort(this.points);

        searchLineSegments();
    }

    private void searchLineSegments() {
        // array list of point Pair, 0 element is minPoint, 1 element is maxPoint
        ArrayList<Point[]> tempLineSegments = new ArrayList<Point[]>();

        int N = points.length;

        // check for duplicates
        Point lastPoint = points[0];
        for (int index = 1; index < N; index++) {
            if (lastPoint.compareTo(points[index]) == 0) {
                throw new java.lang.IllegalArgumentException(); // duplicate
            }
            lastPoint = points[index];
        }

        Point[] copy;
        copy = Arrays.copyOf(points, N);

        //System.out.println("Initial points:");
        //printPoints(copy, null);

        for (int pIndex = 0; pIndex < N; pIndex++) {
            Point pivot = points[pIndex];

            // need to get min and max of line segment
            Arrays.sort(copy);

            // order by slope made with this pivot point
            Arrays.sort(copy, pivot.slopeOrder());

            // on the 0-th place is pivot point (itself point) with slope -Infinity
            // so start with the
            //printPoints(copy, pivot);

            // last slope in loop
            double lastSlope = Double.NEGATIVE_INFINITY;

            // index of min point with the same slope,
            int minIndex = 0;
            // index of max point with the same slope,
            int maxIndex = 0;
            // n number of points in segment
            int n = 0;

            for (int index = 1; index < N + 1; index++) {

                double slope = 0.0;
                boolean slopeEquals = false;

                if (index < N) {
                    slope = pivot.slopeTo(copy[index]);
                    slopeEquals = Double.compare(slope, lastSlope) == 0;
                }

                if (index < N && slopeEquals) {
                    n++;
                    maxIndex = index;
                    continue;
                }

                // index == N || slopes are not equal

                //System.out.printf("Slope with pivot: slope = %f, n = %d\n", lastSlope, n);
                if (n >= 3) {
                    if (pivot.compareTo(copy[minIndex]) < 0) {
                        minIndex = 0;
                    } else if (pivot.compareTo(copy[maxIndex]) > 0) {
                        maxIndex = 0;
                    }

                    Point[] lineSegment = new Point[2];
                    lineSegment[0] = copy[minIndex];
                    lineSegment[1] = copy[maxIndex];

                    //System.out.printf("Add segment %s\n", new LineSegment(copy[minIndex], copy[maxIndex]));
                    tempLineSegments.add(lineSegment);
                }
                minIndex = index;
                maxIndex = index;
                n = 1;
                lastSlope = slope;
            }
        }

        // duplicate points eliminate - sort line segments (by custom comparator), than prevent insert duplicates in result array

        // comparator for lines sort
        Comparator<Point[]> comparator = new Comparator<Point[]>() {
            public int compare(Point[] s1, Point[] s2) {
                int compareByMinPoints = s1[0].compareTo(s2[0]);
                if (compareByMinPoints < 0) {
                    return -1;
                } else if (compareByMinPoints > 0) {
                    return +1;
                } else {
                    return s1[1].compareTo(s2[1]);
                }
            }
        };

        // sort
        Collections.sort(tempLineSegments, comparator);

        // loop over line segments and mark as duplicate (with null) and count all distinct items
        int distinctCount = 0;
        Point[] prevLineSegment = null;

        int size = tempLineSegments.size();
        for (int i = 0; i < size; i++) {
            Point[] lineSegment = tempLineSegments.get(i);

            if (prevLineSegment == null) {
                prevLineSegment = lineSegment;
                distinctCount++;
                continue;
            }

            boolean isDuplicate = prevLineSegment[0].compareTo(lineSegment[0]) == 0 && prevLineSegment[1].compareTo(lineSegment[1]) == 0;
            if (isDuplicate) {
                tempLineSegments.set(i, null); // mark as not unique
                continue;
            }

            prevLineSegment = lineSegment;
            distinctCount++;
        }

        // populate endpoint array with distinct line segments
        segments = new LineSegment[distinctCount];
        int i = 0;
        for (Point[] lineSegment : tempLineSegments) {
            if (lineSegment != null) {
                segments[i] = new LineSegment(lineSegment[0], lineSegment[1]);
                i++;
            }
        }
    }

    private void printPoints(Point[] points, Point pivot)
    {
        if (pivot != null) {
            System.out.printf("Pivot: %s (%f)\nOther points: \n", pivot, pivot.slopeTo(pivot));
        }
        for (Point p : points) {
            if (pivot != null) {
                System.out.printf("%s (%f)\n", p, pivot.slopeTo(p));
            } else {
                System.out.printf("%s\n", p);
            }
        }
    }

    public int numberOfSegments() {
        return segments.length;
    }

    public LineSegment[] segments() {
        LineSegment[] copy;
        copy = Arrays.copyOf(segments, segments.length);
        return copy;
    }

    public static void main(String[] args) {

        FastCollinearPoints F;
        Point[] points;

        points = new Point[4];
        points[0] = new Point(0, 1);
        points[1] = new Point(0, 10);
        points[2] = new Point(0, -3);
        points[3] = new Point(0, 8);

        F = new FastCollinearPoints(points);
        if (F.numberOfSegments() != 1) {
            System.out.printf("FAIL(TEST #1): must be 1 segment found, cause all 4 points on OY axes. Found %d\n", F.numberOfSegments());
        } else {
            System.out.print("OK(TEST #1):");
            for (LineSegment lineSegment : F.segments()) {
                System.out.println(lineSegment.toString());
            }
            System.out.println();
        }

        points = new Point[4];
        points[0] = new Point(1, 0);
        points[1] = new Point(-3, 0);
        points[2] = new Point(10, 0);
        points[3] = new Point(9, 0);

        F = new FastCollinearPoints(points);
        if (F.numberOfSegments() != 1) {
            System.out.printf("FAIL(TEST #2): must be 1 segment found, cause all 4 points on OX axes. Found %d\n", F.numberOfSegments());
        } else {
            System.out.print("OK(TEST #2):");
            for (LineSegment lineSegment : F.segments()) {
                System.out.println(lineSegment.toString());
            }
            System.out.println();
        }

        points = new Point[4];
        points[0] = new Point(1, 1);
        points[1] = new Point(1, 1);
        points[2] = new Point(1, 1);
        points[3] = new Point(1, 1);

        boolean catched = false;
        try {
            F = new FastCollinearPoints(points);
        } catch (java.lang.IllegalArgumentException e) {
            catched = true;
        }

        if (!catched) {
            System.out.printf("FAIL(TEST #3): must be IllegalArgumentException, cause all 4 points are the same\n");
        } else {
            System.out.print("OK(TEST #3) - IllegalArgumentException was be thown\n");
        }

        points = new Point[4];
        points[0] = new Point(10, -1);
        points[1] = new Point(7, 13);
        points[2] = new Point(53, 1);
        points[3] = new Point(-41, 0);

        F = new FastCollinearPoints(points);
        if (F.numberOfSegments() != 0) {
            System.out.printf("FAIL(TEST #4): must be 0 segments found. Found %d\n", F.numberOfSegments());
        } else {
            System.out.printf("OK(TEST #4): Found %d segments\n", F.segments().length);
        }

        points = new Point[7];
        points[0] = new Point(10, -1);
        points[1] = new Point(7, 13);
        points[2] = new Point(53, 1);
        points[3] = new Point(-41, 0);
        points[4] = new Point(-2, 123);
        points[5] = new Point(-41, 34);
        points[6] = new Point(-412, 127);

        F = new FastCollinearPoints(points);
        if (F.numberOfSegments() != 0) {
            System.out.printf("FAIL(TEST #5): must be 0 segments found. Found %d\n", F.numberOfSegments());
        } else {
            System.out.printf("OK(TEST #5): Found %d segments\n", F.segments().length);
        }

        points = new Point[8];
        points[0] = new Point(10000, 0);
        points[1] = new Point(0, 10000);
        points[2] = new Point(3000, 7000);
        points[3] = new Point(7000, 3000);
        points[4] = new Point(20000, 21000);
        points[5] = new Point(3000, 4000);
        points[6] = new Point(14000, 15000);
        points[7] = new Point(6000, 7000);

        F = new FastCollinearPoints(points);
        if (F.numberOfSegments() != 2) {
            System.out.printf("FAIL(TEST #6): must be 2 segments found. Found %d\n", F.numberOfSegments());
        } else {
            System.out.print("OK(TEST #6):");
            for (LineSegment lineSegment : F.segments()) {
                System.out.println(lineSegment.toString());
            }
            System.out.println();
        }

        points = new Point[3];
        points[0] = new Point(28652, 7787);
        points[1] = new Point(28652, 7787);
        points[2] = new Point(21694, 12931);

        catched = false;
        try {
            F = new FastCollinearPoints(points);
        } catch (java.lang.IllegalArgumentException e) {
            catched = true;
        }

        if (!catched) {
            System.out.printf("FAIL(TEST #7): must be IllegalArgumentException, cause all 2 points out of 3 are the same\n");
        } else {
            System.out.print("OK(TEST #7) - IllegalArgumentException was be thown\n");
        }

        points = new Point[5];
        points[0] = new Point(620, 13268);
        points[1] = new Point(25398,2703);
        points[2] = new Point(4227,19721);
        points[3] = new Point(4227,19721);
        points[4] = new Point(26692,27875);


        catched = false;
        try {
            F = new FastCollinearPoints(points);
        } catch (java.lang.IllegalArgumentException e) {
            catched = true;
        }

        if (!catched) {
            System.out.printf("FAIL(TEST #8): must be IllegalArgumentException, cause all 2 points out of 5 are the same\n");
        } else {
            System.out.print("OK(TEST #8) - IllegalArgumentException was be thown\n");
        }

        System.out.println();
        System.out.println("TESTS FROM INPUT FILES");
        System.out.println();

        In in = new In("./input50.txt");
        int N = in.readInt();

        points = new Point[N];

        int i = 0;
        while (!in.isEmpty()) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
            i++;
        }

        F = new FastCollinearPoints(points);

        System.out.printf("Segments (%d): \n", F.numberOfSegments());
        for (LineSegment lineSegment : F.segments()) {
            System.out.println(lineSegment);
        }
        System.out.println();
    }
}
