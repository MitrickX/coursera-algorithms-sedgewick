/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class SAP {
    private Digraph G;

    // min lengths of reachable vertext from source set of vertext. If -1 means vertext not marked yet
    private int[] blueLengths;
    private int[] redLengths;

    public SAP(Digraph G)
    {
        validateNotNull(G);
        this.G = new Digraph(G);

        blueLengths = new int[G.V()];
        redLengths = new int[G.V()];
    }

    private void clearSearchState() {
        for (int x = 0; x < G.V(); x++) {
            blueLengths[x] = -1;
            redLengths[x] = -1;
        }
    }

    public int length(int v, int w) {
        Integer[] vs = new Integer[]{v};
        Integer[] ws = new Integer[]{w};
        return length(Arrays.asList(vs), Arrays.asList(ws));
    }

    public int ancestor(int v, int w) {
        Integer[] vs = new Integer[]{v};
        Integer[] ws = new Integer[]{w};
        return ancestor(Arrays.asList(vs), Arrays.asList(ws));
    }

    public int length(Iterable<Integer> setV, Iterable<Integer> setW) {
        return search(setV, setW, false);
    }

    public int ancestor(Iterable<Integer> setV, Iterable<Integer> setW) {
        return search(setV, setW, true);
    }

    private int search(Iterable<Integer> setV, Iterable<Integer> setW, boolean isAncestor) {
        validateSetVertext(setV);
        validateSetVertext(setW);

        clearSearchState();

        bfs(setV, blueLengths);
        bfs(setW, redLengths);

        int minLength = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < G.V(); i++) {
            if (blueLengths[i] >= 0 && redLengths[i] >= 0) {
                int len = blueLengths[i] + redLengths[i];
                if (len < minLength) {
                    ancestor = i;
                    minLength = len;
                }
            }
        }

        if (isAncestor) {
            return ancestor;
        }

        if (ancestor < 0) {
            return -1;
        } else {
            return minLength;
        }
    }

    // BFS from single source
    private void bfs(Iterable<Integer> setS, int[] lengths) {
        Queue<Integer> q = new Queue<Integer>();

        for (int s : setS) {
            lengths[s] = 0;
            q.enqueue(s);
        }

        while (!q.isEmpty()) {
            int v = q.dequeue();
            int len = lengths[v];
            for (int w : G.adj(v)) {
                if (lengths[w] >= 0) {
                    continue;
                }
                lengths[w] = len + 1;
                q.enqueue(w);
            }
        }
    }

    private void validateNotNull(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= G.V())
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (G.V()-1));
    }

    private void validateSetVertext(Iterable<Integer> setV) {
        validateNotNull(setV);
        for (Integer v : setV) {
            validateNotNull(v);
            validateVertex(v);
        }
    }

    public static void main(String[] args) {
        runTests();
        if (args.length > 0) {
            runClient(args[0]);
        }
    }

    private static void runClient(String fileName)
    {
        In in = new In(fileName);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private static void runTests()
    {
        if ( runForSignleVertexes1() &&
                runForSignleVertexeSets1() &&
                runForSignleVertexeSets25() ) {
            System.out.println("Tests are OK!");
        }
    }

    private static boolean runForSignleVertexes1()
    {
        String fileName = "./digraph1.txt";
        In in = new In(fileName);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        // v, w, length, ancestor
        int[][] testsTable = {
                {3, 11, 4, 1},
                {9, 12, 3, 5},
                {7, 2, 4, 0},
                {1, 6, -1, -1}
        };

        for (int[] testRow : testsTable) {
            int v = testRow[0];
            int w = testRow[1];
            int length = testRow[2];
            int ancestor = testRow[3];

            boolean isTestOk = true;

            int resLength = sap.length(v, w);
            if (resLength != length) {
                System.out.printf("unexcpected length %d for pair %d %d instread of %d\n", resLength, v, w, length);
                isTestOk = false;
            }

            int resAncestor = sap.ancestor(v, w);
            if (resAncestor != ancestor) {
                System.out.printf("unexcpected ancestor %d for pair %d %d instread of %d\n", resAncestor, v, w, ancestor);
                isTestOk = false;
            }

            if (!isTestOk) {
                return false;
            }
        }

        return true;
    }

    private static boolean runForSignleVertexeSets1()
    {
        String fileName = "./digraph1.txt";
        In in = new In(fileName);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        // v, w, length, ancestor
        int[][] testsTable = {
                {3, 11, 4, 1},
                {9, 12, 3, 5},
                {7, 2, 4, 0},
                {1, 6, -1, -1}
        };

        for (int[] testRow : testsTable) {
            int v = testRow[0];
            int w = testRow[1];
            int length = testRow[2];
            int ancestor = testRow[3];

            SET<Integer> setV = new SET<>();
            setV.add(v);

            SET<Integer> setW = new SET<>();
            setW.add(w);

            int resLength = sap.length(setV, setW);
            if (resLength != length) {
                System.out.printf("unexcpected length %d for sets <%d> <%d> instread of %d\n", resLength, v, w, length);
                return false;
            }

            int resAncestor = sap.ancestor(v, w);
            if (resAncestor != ancestor) {
                System.out.printf("unexcpected ancestor %d for sets <%d> <%d> instread of %d\n", resAncestor, v, w, ancestor);
                return false;
            }
        }

        return true;
    }

    private static boolean runForSignleVertexeSets25()
    {
        String fileName = "./digraph25.txt";
        In in = new In(fileName);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        // v, w, length, ancestor
        Object[][] testsTable = {
                { toSET(13, 23, 24), toSET(6, 16, 17), 4, 3 },
        };

        for (Object[] testRow : testsTable) {
            SET<Integer> setV = (SET<Integer>) testRow[0];
            SET<Integer> setW = (SET<Integer>) testRow[1];
            int length = (Integer) testRow[2];
            int ancestor = (Integer) testRow[3];

            boolean isTestOk = true;

            int resLength = sap.length(setV, setW);
            if (resLength != length) {
                System.out.printf("unexcpected length %d for sets %s %s instread of %d\n", resLength, setV, setW, length);
                isTestOk = false;
            }

            int resAncestor = sap.ancestor(setV, setW);
            if (resAncestor != ancestor) {
                System.out.printf("unexcpected ancestor %d for sets %s %s instread of %d\n", resAncestor, setV, setW, ancestor);
                isTestOk = false;
            }

            if (!isTestOk) {
                return false;
            }
        }

        return true;
    }

    private static SET<Integer> toSET(Integer... values)
    {
        SET<Integer> set = new SET<>();
        for (int v : values) {
            set.add(v);
        }
        return set;
    }
}
