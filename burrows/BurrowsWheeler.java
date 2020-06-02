/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.HashMap;

public class BurrowsWheeler {

    private BinaryIn in;
    private BinaryOut out;

    private CircularSuffixArray sa;

    private BurrowsWheeler() {
        this(null, null);
    }

    private BurrowsWheeler(BinaryIn in, BinaryOut out) {
        this.in = in;
        this.out = out;
    }

    private void doTransform() {
        String str = readString();
        if (str.length() <= 0) {
            return;
        }

        sa = new CircularSuffixArray(str);

        int len = sa.length();

        int first = 0;
        for (int i = 0; i < len; i++) {
            int pos = sa.index(i);
            if (pos == 0) {
                first = i;
                break;
            }
        }

        write(first);

        for (int i = 0; i < len; i++) {
            int pos = sa.index(i);

            if (pos == 0) {
                pos = len - 1;
            } else {
                pos--;
            }

            byte ch = (byte)str.charAt(pos);

            write(ch);
        }

        flush();
    }

    /**
     * Read form input tail chars vector into map char -> [index1, index2, ..., indexk]
     * and returns N - total number of chars in tails vector
     * @param tails
     */
    private int readTails(HashMap<Byte, Queue<Integer>> tails) {
        int i = 0;
        while (!isEmpty()) {
            byte ch = readByte();

            Queue<Integer> q = tails.get(ch);
            if (q == null) {
                q = new Queue<>();
                tails.put(ch, q);
            }

            q.enqueue(i);

            i++;
        }

        return i;
    }

    private void doInverseTransform() {
        int first = readInt();

        HashMap<Byte, Queue<Integer>> tails = new HashMap<>();
        int n = readTails(tails);

        // init head chars array that is sorted
        int i = 0;
        int[] heads = new int[n];
        for (byte c : tails.keySet()) {
            Queue<Integer> q = tails.get(c);

            // take into account repeated chars
            int sz = q.size();
            for (int j = 0; j < sz; j++) {
                heads[i] = c & 0xff;
                i++;
            }
        }
        Arrays.sort(heads);

        // compute next[] indexed for restore original text
        int[] next = new int[n];
        for (i = 0; i < n; i++) {
            byte c = (byte)heads[i];
            Queue<Integer> q = tails.get(c);
            int nextIndex = q.dequeue();
            next[i] = nextIndex;
        }

        // restore original text and write it right away into out
        int index = first;
        for (i = 0; i < n; i++) {
            write((byte)heads[index]);
            index = next[index];
        }

        flush();
    }

    private boolean isEmpty() {
        if (in == null) {
            return BinaryStdIn.isEmpty();
        }
        return in.isEmpty();
    }

    private String readString() {
        if (in == null) {
            return BinaryStdIn.readString();
        }
        return in.readString();
    }

    private byte readByte() {
        if (in == null) {
            return BinaryStdIn.readByte();
        }
        return in.readByte();
    }

    private int readInt() {
        if (in == null) {
            return BinaryStdIn.readInt();
        }
        return in.readInt();
    }

    private void write(byte x) {
        if (out == null) {
            BinaryStdOut.write(x);
        } else {
            out.write(x);
        }
    }

    private void write(int i) {
        if (out == null) {
            BinaryStdOut.write(i);
        } else {
            out.write(i);
        }
    }

    private void flush() {
        if (out == null) {
            BinaryStdOut.flush();
        } else {
            out.flush();
        }
    }

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        BurrowsWheeler bw = new BurrowsWheeler();
        bw.doTransform();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        BurrowsWheeler bw = new BurrowsWheeler();
        bw.doInverseTransform();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length <= 0) {
            return;
        }

        if (args[0].equals("-")) {
            transform();
        } else if (args[0].equals("+")) {
            inverseTransform();
        }
    }
}
