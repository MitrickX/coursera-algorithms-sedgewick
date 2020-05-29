import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;

import java.util.Arrays;

public class MoveToFront {
    final private static int R = 256;

    // index => char
    private byte[] chars;

    // char => index
    private byte[] indexes;

    private BinaryIn in;
    private BinaryOut out;

    private static MoveToFront mtf;

    private MoveToFront(BinaryIn in, BinaryOut out) {
        chars = new byte[R];
        indexes = new byte[R];
        this.in = in;
        this.out = out;
    }

    private void resetChars() {
        for (int i = 0; i < R; i++) {
            chars[i] = (byte)i;
            indexes[i] = (byte)i;
        }
    }

    private void doEncode() {
        resetChars();

        while (!in.isEmpty()) {
            byte ch = (byte)in.readChar();
            byte index = indexes[ch];

            out.write(index);

            for (byte idx = index; idx > 0; idx--) {
                byte c = chars[idx-1];

                chars[idx] = c;
                indexes[c] = idx;
            }

            chars[0] = ch;
            indexes[ch] = 0;
        }

        out.flush();
    }

    private void doDecode() {
        resetChars();

        while (!in.isEmpty()) {
            byte index = in.readByte();
            byte ch = chars[index];

            out.write((char)ch);

            for (byte idx = index; idx > 0; idx--) {
                byte c = chars[idx-1];

                chars[idx] = c;
                indexes[c] = idx;
            }

            chars[0] = ch;
            indexes[ch] = 0;
        }

        out.flush();
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        if (mtf == null) {
            mtf = new MoveToFront(new BinaryIn(), new BinaryOut());
        }
        mtf.doEncode();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        if (mtf == null) {
            mtf = new MoveToFront(new BinaryIn(), new BinaryOut());
        }
        mtf.doDecode();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length <= 0) {
            return;
        }

        if (args[0] == "-") {
            encode();
        } else if (args[0] == "+") {
            decode();
        } else if (args[0].equals("tests")) {
            runTests();
        }
    }

    public static void runTests()
    {
        if (runTestEncode() && runTestDecode()) {
            System.out.println("Tests are OK!");
        }
    }

    public static boolean runTestEncode() {
        String testName = "runTestEncode";

        String input = "ABRACADABRA!";

        java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(input.getBytes());

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();

        BinaryIn in = new BinaryIn(inputStream);
        BinaryOut out = new BinaryOut(outputStream);

        MoveToFront mtf = new MoveToFront(in, out);
        mtf.doEncode();

        byte[] output;

        try {
            outputStream.flush();
            output = outputStream.toByteArray();
        } catch (java.io.IOException e) {
            System.out.printf("%s FAIL:", testName);
            System.out.println(e.getMessage());
            return false;
        }

        byte[] expected = new byte[] {
                0x41, 0x42, 0x52, 0x02,
                0x44, 0x01, 0x45, 0x01,
                0x04, 0x04, 0x02, 0x26,
        };

        if (!Arrays.equals(output, expected)) {
            System.out.printf("%s FAIL:unexpected that output equals %s instead of %s\n",
                              testName, Arrays.toString(output), Arrays.toString(expected));
            return false;
        }

        return true;
    }

    public static boolean runTestDecode() {
        String testName = "runTestDecode";

        byte[] input = new byte[] {
                0x41, 0x42, 0x52, 0x02,
                0x44, 0x01, 0x45, 0x01,
                0x04, 0x04, 0x02, 0x26,
        };

        java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(input);

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();

        BinaryIn in = new BinaryIn(inputStream);
        BinaryOut out = new BinaryOut(outputStream);

        MoveToFront mtf = new MoveToFront(in, out);
        mtf.doDecode();

        String output;

        try {
            outputStream.flush();
            output = outputStream.toString();
        } catch (java.io.IOException e) {
            System.out.printf("%s FAIL:", testName);
            System.out.println(e.getMessage());
            return false;
        }

        String expected = "ABRACADABRA!";

        if (!output.equals(expected)) {
            System.out.printf("%s FAIL:unexpected that output equals %s instead of %s\n",
                              testName, output, expected);
            return false;
        }

        return true;
    }
}