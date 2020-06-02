/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;

public class CircularSuffixArray {

    private static class StringWrapper implements Comparable<StringWrapper> {
        private static final int CUTOFF =  15;   // cutoff to insertion sort

        private String str;
        private StringWrapper wrapper;
        private int shift;

        private StringWrapper(String str) {
            this.str = str;
        }

        private StringWrapper(StringWrapper wrapper, int shift) {
            this.wrapper = wrapper;
            this.shift = shift;
        }

        private String getStr() {
            return str != null ? str : wrapper.str;
        }

        private int at(int d) {
            return (shift + d) % getStr().length();
        }

        private int charAt(int d) {
            return getStr().charAt(at(d));
        }

        private String convertToString() {
            return getStr().substring(shift) + getStr().substring(0, shift);
        }

        // LSD sort
        private static void LSDSort(StringWrapper[] wrappers, int w) {
            int n = wrappers.length;
            int R = 256;   // extend ASCII alphabet size
            StringWrapper[] aux = new StringWrapper[n];

            for (int d = w-1; d >= 0; d--) {
                // sort by key-indexed counting on dth character

                // compute frequency counts
                int[] count = new int[R+1];
                for (int i = 0; i < n; i++)
                    count[wrappers[i].charAt(d) + 1]++;

                // compute cumulates
                for (int r = 0; r < R; r++)
                    count[r+1] += count[r];

                // move data
                for (int i = 0; i < n; i++)
                    aux[count[wrappers[i].charAt(d)]++] = wrappers[i];

                // copy back
                for (int i = 0; i < n; i++)
                    wrappers[i] = aux[i];
            }
        }

        private static void sort(StringWrapper[] wrappers) {
            Arrays.sort(wrappers);
        }

        private static void quick3WaySort(StringWrapper[] wrappers) {
            StdRandom.shuffle(wrappers);
            quick3WaySort(wrappers, 0, wrappers.length-1, 0);
        }

        private static void quick3WaySort(StringWrapper[] wrappers, int lo, int hi, int d) {

            // cutoff to insertion sort for small subarrays
            if (hi <= lo + CUTOFF) {
                insertion(wrappers, lo, hi, d);
                return;
            }

            int lt = lo, gt = hi;
            int v = wrappers[lo].charAt(d);
            int i = lo + 1;
            while (i <= gt) {
                int t = wrappers[i].charAt(d);
                if      (t < v) exch(wrappers, lt++, i++);
                else if (t > v) exch(wrappers, i, gt--);
                else              i++;
            }

            // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
            quick3WaySort(wrappers, lo, lt-1, d);
            if (v >= 0) quick3WaySort(wrappers, lt, gt, d+1);
            quick3WaySort(wrappers, gt+1, hi, d);
        }

        // sort from a[lo] to a[hi], starting at the dth character
        private static void insertion(StringWrapper[] wrappers, int lo, int hi, int d) {
            for (int i = lo; i <= hi; i++)
                for (int j = i; j > lo && less(wrappers[j], wrappers[j-1], d); j--)
                    exch(wrappers, j, j-1);
        }

        // exchange a[i] and a[j]
        private static void exch(StringWrapper[] wrappers, int i, int j) {
            StringWrapper temp = wrappers[i];
            wrappers[i] = wrappers[j];
            wrappers[j] = temp;
        }

        // is v less than w, starting at character d
        private static boolean less(StringWrapper v, StringWrapper w, int d) {
            int vLen = v.getStr().length(),
                wLen = w.getStr().length(),
                minLen = Math.min(v.getStr().length(), w.getStr().length());
            for (int i = d; i < minLen; i++) {
                if (v.charAt(i) < w.charAt(i)) return true;
                if (v.charAt(i) > w.charAt(i)) return false;
            }
            return vLen < wLen;
        }

        private static String arrayToString(StringWrapper[] wrappers) {
            String[] strings = new String[wrappers.length];
            for (int i = 0; i < wrappers.length; i++) {
                StringWrapper w = wrappers[i];
                strings[i] = w.convertToString();
            }
            return Arrays.toString(strings);
        }

        public int compareTo(StringWrapper o) {
            int aLen = this.getStr().length(),
                    bLen = o.getStr().length(),
                    minLen = Math.min(aLen, bLen),
                    d = 0;
            for (int i = 0; i < minLen; i++) {
                d = this.charAt(i) - o.charAt(i);
                if (d != 0) {
                    return d;
                }
            }
            return aLen - bLen;
        }
    }

    private int[] indexes;
    private int len;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }

        len = s.length();
        if (len <= 0) {
            return;
        }

        StringWrapper[] wrappers = new StringWrapper[len];
        wrappers[0] = new StringWrapper(s);
        for (int i = 1; i < len; i++) {
            wrappers[i] = new StringWrapper(wrappers[0], i);
        }

        StringWrapper.quick3WaySort(wrappers);
        //StringWrapper.sort(wrappers);

        indexes = new int[len];
        for (int i = 0; i < len; i++) {
            indexes[i] = wrappers[i].shift;
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

        //long start = System.nanoTime();

        CircularSuffixArray sa = new CircularSuffixArray(data);

        //System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        //System.out.println(System.nanoTime() - start);

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

        Arrays.sort(indexes);
        Arrays.sort(expectedIndexes);

        if (!Arrays.equals(expectedIndexes, indexes)) {
            System.out.printf("%s FAIL: unexpected indexes %s instead of %s\n", name,
                              Arrays.toString(indexes),
                              Arrays.toString(expectedIndexes));
            return false;
        }

        return true;
    }
}
