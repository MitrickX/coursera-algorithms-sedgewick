/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;

import java.util.Arrays;

public class CircularSuffixArray {
    private int[] indexes;
    private int len;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        
        len = s.length();

        ST<String, Queue<Integer>>symbolTable = new ST<>();
        for (int i = 0; i < len; i++) {
            String substr = s.substring(i) + s.substring(0, i);
            Queue<Integer> q = symbolTable.get(substr);
            if (q == null) {
                q = new Queue<>();
                symbolTable.put(substr, q);
            }
            q.enqueue(i);
        }

        indexes = new int[len];

        int i = 0;
        for (String str : symbolTable.keys()) {
            Queue<Integer> q = symbolTable.get(str);
            while (!q.isEmpty()) {
                indexes[i] = q.dequeue();
                i++;
            }
        }
    }

    // length of s
    public int length() {
        return len;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= len) {
            throw new IllegalArgumentException();
        }
        return indexes[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        runTests();
    }

    private static void runTests() {
        if (runLenTest() && runIndexesTest() && runNullConstruct() && runOutOfBoundIndex()
                && runFromFileAmendments() && runStarsTest()) {
            System.out.println("CircularSuffixArray tests are OK!");
        }
    }

    private static boolean runLenTest() {
        String name = "runLenTest";
        CircularSuffixArray sa = new CircularSuffixArray("ABCDEF");
        if (sa.length() != 6) {
            System.out.printf("%s FAIL: unexpected len %d instead of %d\n", name, sa.length(), 6);
            return false;
        }
        return true;
    }

    private static boolean runIndexesTest() {
        String name = "runIndexesTest";

        CircularSuffixArray sa = new CircularSuffixArray("ABRACADABRA!");

        int[] expected = new int[] {
                11, 10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2
        };

        int[] indexes = new int[sa.length()];
        for (int i = 0; i < sa.length(); i++) {
            indexes[i] = sa.index(i);
        }

        if (!Arrays.equals(expected, indexes)) {
            System.out.printf("%s FAIL: unexpected indexes %s instead of %s\n", name,
                              Arrays.toString(indexes),
                              Arrays.toString(expected));
            return false;
        }

        return true;
    }

    private static boolean runNullConstruct() {
        String name = "runNullConstruct";

        boolean expected = false;

        try {
            CircularSuffixArray sa = new CircularSuffixArray(null);
        } catch (IllegalArgumentException e) {
            expected = true;
        }

        if (!expected) {
            System.out.printf("%s FAIL: unexpected that there was no IllegalArgumentException\n", name);
            return false;
        }
        return true;
    }

    private static boolean runOutOfBoundIndex() {
        String name = "runOutOfBoundIndex";

        CircularSuffixArray sa = new CircularSuffixArray("XXYEUW");

        boolean expected = false;

        try {
            sa.index(1000);
        } catch (IllegalArgumentException e) {
            expected = true;
        }

        if (!expected) {
            System.out.printf("%s FAIL: unexpected that there was no IllegalArgumentException when call index(1000)\n", name);
            return false;
        }

        expected = false;

        try {
            sa.index(-10);
        } catch (IllegalArgumentException e) {
            expected = true;
        }

        if (!expected) {
            System.out.printf("%s FAIL: unexpected that there was no IllegalArgumentException when call index(-10)\n", name);
            return false;
        }

        return true;
    }

    private static boolean runFromFileAmendments() {
        String name = "runFromFileAmendments";
        String fileName = "./amendments.txt";

        In in = new In(fileName);
        String data = in.readAll();

        CircularSuffixArray sa = new CircularSuffixArray(data);

        int expectedLen = 18369;
        if (sa.length() != expectedLen) {
            System.out.printf("%s FAIL: unexpected len %d instead of %d for file %s\n", name, sa.length(), expectedLen, fileName);
            return false;

        }

        return true;
    }

    private static boolean runStarsTest() {
        String name = "*************";
        CircularSuffixArray sa = new CircularSuffixArray(name);

        int expectedLen = 13;
        if (sa.length() != expectedLen) {
            System.out.printf("%s FAIL: unexpected len %d instead of %d\n", name, sa.length(), expectedLen);
        }

        int[] expectedIndexes = new int[] {
                0,1,2,3,4,5,6,7,8,9,10,11,12
        };

        int[] indexes = new int[sa.length()];
        for (int i = 0; i < sa.length(); i++) {
            indexes[i] = sa.index(i);
        }

        if (!Arrays.equals(expectedIndexes, indexes)) {
            System.out.printf("%s FAIL: unexpected indexes %s instead of %s\n", name,
                              Arrays.toString(indexes),
                              Arrays.toString(expectedIndexes));
            return false;
        }

        return true;
    }
}
