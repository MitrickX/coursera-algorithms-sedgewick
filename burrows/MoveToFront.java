import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    final private static int R = 256;

    // index => char
    private byte[] chars;

    // char => index
    private byte[] indexes;

    private BinaryIn in;
    private BinaryOut out;

    private MoveToFront() {
        this(null, null);
    }

    private MoveToFront(BinaryIn in, BinaryOut out) {
        chars = new byte[R];
        indexes = new byte[R];

        for (int i = 0; i < R; i++) {
            chars[i] = (byte) i;
            indexes[i] = (byte) i;
        }

        this.in = in;
        this.out = out;
    }

    private void doEncode() {
        while (!isEmpty()) {
            byte ch = readByte();
            byte index = indexes[ch & 0xff];

            write(index);

            for (int idx = index & 0xff; idx > 0; idx--) {
                byte c = chars[idx-1];

                chars[idx] = c;
                indexes[c & 0xff] = (byte)idx;
            }

            chars[0] = ch;
            indexes[ch & 0xff] = 0;
        }

        flush();
    }

    private void doDecode() {
        while (!isEmpty()) {
            int index = readByte() & 0xff;
            byte ch = chars[index];

            write(ch);

            for (int idx = index; idx > 0; idx--) {
                byte c = chars[idx-1];

                chars[idx] = c;
                indexes[c & 0xff] = (byte)idx;
            }

            chars[0] = ch;
            indexes[ch & 0xff] = 0;
        }

        flush();
    }

    private boolean isEmpty() {
        if (in == null) {
            return BinaryStdIn.isEmpty();
        }
        return in.isEmpty();
    }

    private byte readByte() {
        if (in == null) {
            return BinaryStdIn.readByte();
        }
        return in.readByte();
    }

    private void write(byte x) {
        if (out == null) {
            BinaryStdOut.write(x);
        } else {
            out.write(x);
        }
    }

    private void write(char c) {
        if (out == null) {
            BinaryStdOut.write(c);
        } else {
            out.write(c);
        }
    }

    private void flush() {
        if (out == null) {
            BinaryStdOut.flush();
        } else {
            out.flush();
        }
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        MoveToFront mtf = new MoveToFront();
        mtf.doEncode();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        MoveToFront mtf = new MoveToFront();
        mtf.doDecode();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length <= 0) {
            return;
        }

        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        }
    }
}