/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

public class TestBurrowsWheeler {

    private static BurrowsWheeler newBurrowsWheeler(BinaryIn in, BinaryOut out) {
        try {
            Constructor<?> constructor = BurrowsWheeler.class.getDeclaredConstructor(BinaryIn.class, BinaryOut.class);
            constructor.setAccessible(true);
            return (BurrowsWheeler)constructor.newInstance(in, out);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static void invokeDoTransform(BurrowsWheeler bw) {
        try {
            Method method = bw.getClass().getDeclaredMethod("doTransform");
            method.setAccessible(true);
            method.invoke(bw);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void invokeDoInverseTransform(BurrowsWheeler bw) {
        try {
            Method method = bw.getClass().getDeclaredMethod("doInverseTransform");
            method.setAccessible(true);
            method.invoke(bw);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
        if (runTestTransform() && runTestInverseTransform() &&
                runTestTransformBinary() && runTestInverseTransformBinary()) {
            System.out.println("TestBurrowsWheeler tests are OK!");
        }
    }

    private static boolean runTestTransform() {
        String testName = "runTestTransform";

        String input = "ABRACADABRA!";

        java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(input.getBytes());

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();

        BinaryIn in = new BinaryIn(inputStream);
        BinaryOut out = new BinaryOut(outputStream);

        BurrowsWheeler bw = newBurrowsWheeler(in, out);
        invokeDoTransform(bw);

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
                0x00, 0x00, 0x00, 0x03,
                0x41, 0x52, 0x44, 0x21,
                0x52, 0x43, 0x41, 0x41,
                0x41, 0x41, 0x42, 0x42
        };

        if (!Arrays.equals(output, expected)) {
            System.out.printf("%s FAIL:unexpected that output equals %s instead of %s\n",
                              testName, Arrays.toString(output), Arrays.toString(expected));
            return false;
        }

        return true;
    }

    private static boolean runTestInverseTransform() {
        String testName = "runTestInverseTransform";

        byte[] encoded = new byte[] {
                0x00, 0x00, 0x00, 0x03,
                0x41, 0x52, 0x44, 0x21,
                0x52, 0x43, 0x41, 0x41,
                0x41, 0x41, 0x42, 0x42
        };

        java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(encoded);

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();

        BinaryIn in = new BinaryIn(inputStream);
        BinaryOut out = new BinaryOut(outputStream);

        BurrowsWheeler bw = newBurrowsWheeler(in, out);
        invokeDoInverseTransform(bw);

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

    private static boolean runTestTransformBinary() {
        String testName = "runTestTransformBinary";

        String inputFileName = "./us.gif";

        BinaryIn fileIn = new BinaryIn(inputFileName);

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        BinaryOut out = new BinaryOut(outputStream);

        BurrowsWheeler bw = newBurrowsWheeler(fileIn, out);
        invokeDoTransform(bw);

        byte[] output = outputStream.toByteArray();

        String expectedOutFileName = "./us.gif.bwt";
        fileIn = new BinaryIn(expectedOutFileName);
        byte[] expected = readAllBytes(fileIn);

        if (!Arrays.equals(output, expected)) {
            System.out.printf("%s FAIL:unexpected that result is not equal to file content\n",
                              testName);
            return false;
        }

        return true;
    }

    private static boolean runTestInverseTransformBinary() {
        String testName = "runTestInverseTransformBinary";

        String inputFileName = "./us.gif.bwt";

        BinaryIn fileIn = new BinaryIn(inputFileName);

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        BinaryOut out = new BinaryOut(outputStream);

        BurrowsWheeler bw = newBurrowsWheeler(fileIn, out);
        invokeDoInverseTransform(bw);

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
