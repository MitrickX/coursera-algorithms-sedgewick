/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class TestMoveToFront {

    private static MoveToFront newMoveToFront(BinaryIn in, BinaryOut out) {
        try {
            Constructor<?> constructor = MoveToFront.class.getDeclaredConstructor(BinaryIn.class, BinaryOut.class);

            constructor.setAccessible(true);
            return (MoveToFront)constructor.newInstance(in, out);
        } catch (Exception e) {
            printException(e);
            return null;
        }
    }

    private static void invokeDoEncode(MoveToFront mtf) {
        try {
            Method method = mtf.getClass().getDeclaredMethod("doEncode");
            method.setAccessible(true);
            method.invoke(mtf);
        } catch (Exception e) {
            printException(e);
        }
    }

    private static void invokeDoDecode(MoveToFront mtf) {
        try {
            Method method = mtf.getClass().getDeclaredMethod("doDecode");
            method.setAccessible(true);
            method.invoke(mtf);
        } catch (Exception e) {
            printException(e);
        }
    }

    private static void printException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        System.out.println(e.getMessage());
        System.out.println(sw.toString());
    }

    private static byte[] readAllBytes(BinaryIn in) {
        ArrayList<Byte> list = new ArrayList<>();
        while (!in.isEmpty()) {
            list.add(in.readByte());
        }
        byte[] result = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests()
    {
        if (runTestEncode() && runTestDecode() &&
                runTestEncodeBinary() && runTestDecodeBinary()) {
            System.out.println("TestMoveToFront tests are OK!");
        }
    }

    private static boolean runTestEncode() {
        String testName = "runTestEncode";

        String input = "ABRACADABRA!";

        java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(input.getBytes());

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();

        BinaryIn in = new BinaryIn(inputStream);
        BinaryOut out = new BinaryOut(outputStream);

        MoveToFront mtf = newMoveToFront(in, out);
        invokeDoEncode(mtf);

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

    private static boolean runTestDecode() {
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

        MoveToFront mtf = newMoveToFront(in, out);
        invokeDoDecode(mtf);

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

    private static boolean runTestEncodeBinary() {
        String testName = "runTestEncodeBinary";

        String inputFileName = "./us.gif";

        BinaryIn fileIn = new BinaryIn(inputFileName);

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        BinaryOut out = new BinaryOut(outputStream);

        MoveToFront mtf = newMoveToFront(fileIn, out);
        invokeDoEncode(mtf);

        byte[] output = outputStream.toByteArray();

        String expectedOutFileName = "./us.gif.mtf";
        fileIn = new BinaryIn(expectedOutFileName);
        byte[] expected = readAllBytes(fileIn);

        if (!Arrays.equals(output, expected)) {
            System.out.printf("%s FAIL:unexpected that result is not equal to file content\n",
                              testName);
            return false;
        }

        return true;
    }

    private static boolean runTestDecodeBinary() {
        String testName = "runTestDecodeBinary";

        String inputFileName = "./us.gif.mtf";

        BinaryIn fileIn = new BinaryIn(inputFileName);

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        BinaryOut out = new BinaryOut(outputStream);

        MoveToFront mtf = newMoveToFront(fileIn, out);
        invokeDoDecode(mtf);

        byte[] output = outputStream.toByteArray();

        String expectedOutFileName = "./us.gif";
        fileIn = new BinaryIn(expectedOutFileName);
        byte[] expected = readAllBytes(fileIn);

        if (!Arrays.equals(output, expected)) {
            System.out.printf("%s FAIL:unexpected that result is not equal to file content\n",
                              testName);
            return false;
        }

        return true;
    }
}
