/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;

public class KdTree {

    private final boolean MODE_HORIZONTAL = true;   // on even levels: 0, 2, 4 ...
    private final boolean MODE_VERTICAL = false;    // on odd levels: 1, 3, 5

    private Node root;

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node left;
        private Node right;
        public Node(Point2D p, Node left, Node right, RectHV rect) {
            this.p = p;
            this.left = left;
            this.right = right;
            this.rect = rect;
        }
    }

    private int size = 0;

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        root = insert(root, p, MODE_VERTICAL, 0, 0, 1, 1);
    }

    /*
    private Node insert(Node current, Point2D p, boolean mode, RectHV rect) {
        if (current == null) {
            size++;
            return new Node(p, null, null, rect);
        }

        if (p.x() == current.p.x() && p.y() == current.p.y()) {
            // diplicate point
            return current;
        }

        if (mode == MODE_VERTICAL) {
            if (p.x() < current.p.x()) {
                RectHV subRect = new RectHV(rect.xmin(), rect.ymin(), current.p.x(), rect.ymax());
                current.left = insert(current.left, p, MODE_HORIZONTAL, subRect);
            } else {
                RectHV subRect = new RectHV(current.p.x(), rect.ymin(), rect.xmax(), rect.ymax());
                current.right = insert(current.right, p, MODE_HORIZONTAL, subRect);
            }
        } else {
            if (p.y() < current.p.y()) {
                RectHV subRect = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), current.p.y());
                current.left = insert(current.left, p, MODE_VERTICAL, subRect);
            } else {
                RectHV subRect = new RectHV(rect.xmin(), current.p.y(), rect.xmax(), rect.ymax());
                current.right = insert(current.right, p, MODE_VERTICAL, subRect);
            }
        }

        return current;
    }*/

    private Node insert(Node current, Point2D p, boolean mode, double xmin, double ymin, double xmax, double ymax) {
        if (current == null) {
            size++;
            return new Node(p, null, null, new RectHV(xmin, ymin, xmax, ymax));
        }

        if (p.x() == current.p.x() && p.y() == current.p.y()) {
            // diplicate point
            return current;
        }

        if (mode == MODE_VERTICAL) {
            if (p.x() < current.p.x()) {
                current.left = insert(current.left, p, MODE_HORIZONTAL, xmin, ymin, current.p.x(), ymax);
            } else {
                current.right = insert(current.right, p, MODE_HORIZONTAL, current.p.x(), ymin, xmax, ymax);
            }
        } else {
            if (p.y() < current.p.y()) {
                current.left = insert(current.left, p, MODE_VERTICAL, xmin, ymin, xmax, current.p.y());
            } else {
                current.right = insert(current.right, p, MODE_VERTICAL, xmin, current.p.y(), xmax, ymax);
            }
        }

        return current;
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        return contains(root, p, MODE_VERTICAL);
    }

    private boolean contains(Node current, Point2D p, boolean mode) {
        if (current == null) {
            return false;
        }

        if (current.p.equals(p)) {
            return true;
        }

        if (mode == MODE_VERTICAL) {
            if (p.x() < current.p.x()) {
                return contains(current.left, p, MODE_HORIZONTAL);
            } else {
                return contains(current.right, p, MODE_HORIZONTAL);
            }
        } else {
            if (p.y() < current.p.y()) {
                return contains(current.left, p, MODE_VERTICAL);
            } else {
                return contains(current.right, p, MODE_VERTICAL);
            }
        }
    }

    public void draw() {
        if (root == null) {
            return;
        }

        Color color = StdDraw.getPenColor();
        double radius = StdDraw.getPenRadius();

        StdDraw.setPenColor(Color.BLACK);
        root.rect.draw();
        draw(root, MODE_VERTICAL);

        StdDraw.setPenColor(color);
        StdDraw.setPenRadius(radius);

        //printTree();
    }

    private void draw(Node current, boolean mode) {
        if (current == null) {
            return;
        }

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.01);
        current.p.draw();

        StdDraw.setPenRadius();

        if (mode == MODE_VERTICAL) {
            StdDraw.setPenColor(Color.RED);
            StdDraw.line(current.p.x(), current.rect.ymin(), current.p.x(), current.rect.ymax());
        } else {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.line(current.rect.xmin(), current.p.y(), current.rect.xmax(), current.p.y());
        }

        boolean nextMode = mode == MODE_VERTICAL ? MODE_HORIZONTAL : MODE_VERTICAL;
        draw(current.left, nextMode);
        draw(current.right, nextMode);
    }

    private void printTree() {
        printNode(root, MODE_VERTICAL, 0);
    }

    private void printNode(Node current, boolean mode, int indent) {

        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }

        if (current == null) {
            System.out.println("<null>");
            return;
        }

        System.out.printf("%s (%s)\n", current.p, mode == MODE_VERTICAL ? "|" : "-");
        boolean nextMode = mode == MODE_VERTICAL ? MODE_HORIZONTAL : MODE_VERTICAL;
        printNode(current.left, nextMode, indent + 1);
        printNode(current.right, nextMode, indent + 1);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new java.lang.IllegalArgumentException();
        }
        Queue<Point2D> queue = new Queue<Point2D>();
        collect(root, queue, rect);
        return queue;
    }

    private void collect(Node current, Queue<Point2D> queue, RectHV rect) {
        if (current == null) {
            return;
        }
        if (!rect.intersects(current.rect)) {
            return;
        }

        if (rect.contains(current.p)) {
            queue.enqueue(current.p);
        }

        collect(current.left, queue, rect);
        collect(current.right, queue, rect);
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        if (root == null) {
            return null;
        }

        double distance = root.p.distanceSquaredTo(p);
        NearestResult result = new NearestResult(root.p, distance);
        nearest(root, MODE_VERTICAL, p, result);

        return result.p;
    }

    private static class NearestResult {
        Point2D p;
        double distance;
        public NearestResult(Point2D p, double distance) {
            this.p = p;
            this.distance = distance;
        }
    }

    private void nearest(Node current, boolean mode, Point2D query, NearestResult result) {
        if (current == null) {
            return;
        }

        // pruning rule: if the closest point discovered so far is closer than the distance between the query point and
        // the rectangle corresponding to a node,
        // there is no need to explore that node (or its subtrees)
        double distanceRectToCandidate = current.rect.distanceSquaredTo(query);
        if (result.distance < distanceRectToCandidate) {
            return;
        }

        double distanceToCurrent = query.distanceSquaredTo(current.p);
        if (distanceToCurrent < result.distance) {
            result.distance = distanceToCurrent;
            result.p = current.p;
        }

        boolean goLeftFirst;        // left or right subtree to go first
        boolean nextMode;           // next mode

        if (mode == MODE_VERTICAL) {
            // define where to go first: left or right
            goLeftFirst = query.x() < current.p.x();
            nextMode = MODE_HORIZONTAL;
        } else {
            // define where to go first: above or below
            goLeftFirst = query.y() < current.p.y();
            nextMode = MODE_VERTICAL;
        }

        if (goLeftFirst) {
            nearest(current.left, nextMode, query, result);
            nearest(current.right, nextMode, query, result);
        } else {
            nearest(current.right, nextMode, query, result);
            nearest(current.left, nextMode, query, result);
        }

    }

    public static void main(String[] args) {

        KdTree kd = new KdTree();

        String fileName = "input10.txt";
        In in = new In(fileName);

        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            kd.insert(new Point2D(x, y));
        }

        kd.draw();

        testWholeRange();
        testContains();
        testNearest();
        testGraderTest1d();

        System.out.println("Tests finished");

    }

    private static KdTree factoryFromFile(String fileName)
    {
        KdTree kd = new KdTree();
        In in = new In(fileName);
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kd.insert(p);
        }
        return kd;
    }

    private static SET<Point2D> factorySETFromFile(String fileName)
    {
        SET<Point2D> set = new SET<Point2D>();
        In in = new In(fileName);
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            set.add(p);
        }
        return set;
    }

    private static void testWholeRange()
    {
        String testName = "testWholeRange";

        String fileName = "input10.txt";
        KdTree kd = factoryFromFile(fileName);
        SET<Point2D> set = factorySETFromFile(fileName);

        assertEquals(set.size(), kd.size(), String.format("Size of kd-tree (%s) must be %d not %d", fileName, set.size(), kd.size), testName);

        for (Point2D p : kd.range(new RectHV(0.0, 0.0, 1.0, 1.0))) {
            assertTrue(set.contains(p), String.format("%s in range of KD tree (%s) must be from set", fileName, p), testName);
        }
    }

    private static void testContains()
    {
        String testName = "testContains";

        String fileName = "input10.txt";
        KdTree kd = factoryFromFile(fileName);
        SET<Point2D> set = factorySETFromFile(fileName);

        for (Point2D p : set) {
            assertTrue(kd.contains(p), String.format("%s must be in KD tree (%s)", p, fileName), testName);
        }
    }

    private static void testNearest()
    {
        String testName = "testNearest";

        String fileName = "input10.txt";

        KdTree kd = factoryFromFile(fileName);

        Point2D queryPoint = new Point2D(0.55078125, 0.72265625);
        Point2D nearest = kd.nearest(queryPoint);
        Point2D expected = new Point2D(0.32, 0.708);

        Color color = StdDraw.getPenColor();
        double r = StdDraw.getPenRadius();

        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(Color.GREEN);

        queryPoint.draw();

        StdDraw.setPenRadius(r);
        StdDraw.setPenColor(color);


        assertTrue(kd.contains(expected), String.format("%s must be in KD tree (%s)", expected, fileName), testName);

        assertEquals(expected, nearest, String.format("Nearest point in KD tree (%s) must be %s not %s", fileName, expected, nearest), testName);

    }

    private static void testGraderTest1d()
    {
        String testName = "testGraderTest1d";

        KdTree kd = new KdTree();
        kd.insert(new Point2D(0.0, 0.0));
        kd.insert(new Point2D(0.0, 1.0));
        kd.insert(new Point2D(1.0, 1.0));
        kd.insert(new Point2D(1.0, 1.0));

        assertEquals(3, kd.size(), String.format("Size of unique points in kd-tree must be %d insteadof %d", 3, kd.size), testName);
    }

    private static void assertEquals(Point2D expected, Point2D tested, String message, String testName) {
        if (!expected.equals(tested)) {
            System.out.printf("FAIL (%s): %s\n", testName, message);
        }
    }

    private static void assertEquals(int expected, int tested, String message, String testName) {
        if (expected != tested) {
            System.out.printf("FAIL (%s): %s\n", testName, message);
        }
    }

    private static void assertTrue(boolean expr, String message, String testName) {
        if (!expr) {
            System.out.printf("FAIL (%s): %s\n", testName, message);
        }
    }
}
