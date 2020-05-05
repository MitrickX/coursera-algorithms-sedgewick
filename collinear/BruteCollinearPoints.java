import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {
    private Point[] points;
    private ArrayList<LineSegment> segments;

    public BruteCollinearPoints(Point[] points) {
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
        segments = new ArrayList<LineSegment>();
        int N = points.length;
        Point p, q, r, s;

        if (N < 1) {
            return;
        }

        // check for duplicates
        Point lastPoint = points[0];
        for (int index = 1; index < N; index++) {
            if (lastPoint.compareTo(points[index]) == 0) {
                throw new java.lang.IllegalArgumentException(); // duplicate
            }
            lastPoint = points[index];
        }

        Double pqSlope, qrSlope, rsSlope;
        for (int pIndex1 = 0; pIndex1 < N - 3; pIndex1++) {
            for (int pIndex2 = pIndex1 + 1; pIndex2 < N - 2; pIndex2++) {
                for (int pIndex3 = pIndex2 + 1; pIndex3 < N - 1; pIndex3++) {
                    for (int pIndex4 = pIndex3 + 1; pIndex4 < N; pIndex4++) {

                        p = points[pIndex1];
                        q = points[pIndex2];
                        r = points[pIndex3];
                        s = points[pIndex4];

                        pqSlope = p.slopeTo(q);
                        qrSlope = q.slopeTo(r);
                        rsSlope = r.slopeTo(s);

                        if (Double.compare(pqSlope, qrSlope) == 0 && Double.compare(qrSlope, rsSlope) == 0) {
                            segments.add(new LineSegment(p, s));
                        }
                    }
                }
            }
        }
    }

    public int numberOfSegments() {
        return segments.size();
    }

    public LineSegment[] segments() {
        int numberOfSegments = segments.size();

        LineSegment[] lineSegments = new LineSegment[numberOfSegments];

        int i = 0;
        for (LineSegment lineSegment : segments) {
            lineSegments[i] = lineSegment;
            i++;
        }

        return lineSegments;
    }

    public static void main(String[] args) {
        BruteCollinearPoints BF;
        Point[] points;

        points = new Point[4];
        points[0] = new Point(0, 1);
        points[1] = new Point(0, 10);
        points[2] = new Point(0, -3);
        points[3] = new Point(0, 8);

        BF = new BruteCollinearPoints(points);
        if (BF.numberOfSegments() != 1) {
            System.out.printf("FAIL(TEST #1): must be 1 segment found, cause all 4 points on OY axes. Found %d\n", BF.numberOfSegments());
        } else {
            System.out.print("OK(TEST #1):");
            for (LineSegment lineSegment : BF.segments()) {
                System.out.println(lineSegment.toString());
            }
            System.out.println();
        }

        points = new Point[4];
        points[0] = new Point(1, 0);
        points[1] = new Point(-3, 0);
        points[2] = new Point(10, 0);
        points[3] = new Point(9, 0);

        BF = new BruteCollinearPoints(points);
        if (BF.numberOfSegments() != 1) {
            System.out.printf("FAIL(TEST #2): must be 1 segment found, cause all 4 points on OX axes. Found %d\n", BF.numberOfSegments());
        } else {
            System.out.print("OK(TEST #2):");
            for (LineSegment lineSegment : BF.segments()) {
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
            BF = new BruteCollinearPoints(points);
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

        BF = new BruteCollinearPoints(points);
        if (BF.numberOfSegments() != 0) {
            System.out.printf("FAIL(TEST #4): must be 0 segments found. Found %d\n", BF.numberOfSegments());
        } else {
            System.out.printf("OK(TEST #4): Found %d segments\n", BF.segments().length);
        }

        points = new Point[7];
        points[0] = new Point(10, -1);
        points[1] = new Point(7, 13);
        points[2] = new Point(53, 1);
        points[3] = new Point(-41, 0);
        points[4] = new Point(-2, 123);
        points[5] = new Point(-41, 34);
        points[6] = new Point(-412, 127);

        BF = new BruteCollinearPoints(points);
        if (BF.numberOfSegments() != 0) {
            System.out.printf("FAIL(TEST #5): must be 0 segments found. Found %d\n", BF.numberOfSegments());
        } else {
            System.out.printf("OK(TEST #5): Found %d segments\n", BF.segments().length);
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

        BF = new BruteCollinearPoints(points);
        if (BF.numberOfSegments() != 2) {
            System.out.printf("FAIL(TEST #6): must be 2 segments found. Found %d\n", BF.numberOfSegments());
        } else {
            System.out.print("OK(TEST #6):");
            for (LineSegment lineSegment : BF.segments()) {
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
            BF = new BruteCollinearPoints(points);
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
            BF = new BruteCollinearPoints(points);
        } catch (java.lang.IllegalArgumentException e) {
            catched = true;
        }

        if (!catched) {
            System.out.printf("FAIL(TEST #8): must be IllegalArgumentException, cause all 2 points out of 5 are the same\n");
        } else {
            System.out.print("OK(TEST #8) - IllegalArgumentException was be thown\n");
        }

    }
}
