/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {

    final private boolean DIRECTION_HORIZONTAL = false;
    final private boolean DIRECTION_VERTICAL = true;

    private int[][] pixels;
    private int width;
    private int height;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        this.pixels = pictureToPixelsMatrix(picture);
        this.height = this.pixels.length;
        if (this.height <= 0) {
            throw new IllegalArgumentException();
        }
        this.width = this.pixels[0].length;
    }

    public Picture picture() {
        return pixelsMatrixToPicture(pixels, width, height);
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        int w = width;
        int h = height;

        if (x == 0 || y == 0 || x == w - 1 || y == h - 1) {
            return 1000;
        }

        if (x < 0 || x > w - 1 || y < 0 || y > h - 1) {
            throw new IllegalArgumentException();
        }

        int xsg = calcSquareComponentGradient(pixels[y][x - 1], pixels[y][x + 1]);
        int ysg = calcSquareComponentGradient(pixels[y - 1][x], pixels[y + 1][x]);

        return Math.sqrt(xsg + ysg);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] energyMatrix = calcEnergyMatrix(true);
        return findVerticalSeam(energyMatrix, width(), height());
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energyMatrix = calcEnergyMatrix(false);
        return findVerticalSeam(energyMatrix, height(), width());
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, DIRECTION_HORIZONTAL);

        int w = width();
        int h = height();
        int[][] newPixels = new int[h - 1][w];

        for (int x = 0; x < w; x++) {
            for (int y = 0, newY = 0; y < h; y++) {
                if (y != seam[x]) {
                    newPixels[newY++][x] = pixels[y][x];
                }
            }
        }

        this.pixels = newPixels;
        this.height = h - 1;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, DIRECTION_VERTICAL);

        int w = width();
        int h = height();
        int[][] newPixels = new int[h][w - 1];

        for (int y = 0; y < h; y++) {
            for (int x = 0, newX = 0; x < w; x++) {
                if (x != seam[y]) {
                    newPixels[y][newX++] = pixels[y][x];
                }
            }
        }

        this.pixels = newPixels;
        this.width = w - 1;
    }

    private int calcSquareComponentGradient(int rgb1, int rgb2)
    {
        int r1, g1, b1, r2, g2, b2, rd, gd, bd;

        r1 = (rgb1 & 0x00ff0000) >> 16;
        g1 = (rgb1 & 0x0000ff00) >> 8;
        b1 = rgb1 & 0x000000ff;

        r2 = (rgb2 & 0x00ff0000) >> 16;
        g2 = (rgb2 & 0x0000ff00) >> 8;
        b2 = rgb2 & 0x000000ff;

        rd = r1 - r2;
        gd = g1 - g2;
        bd = b1 - b2;

        return rd * rd + gd * gd + bd * bd;
    }

    private double[][] calcEnergyMatrix(boolean isTransposed) {
        int w = width();
        int h = height();

        if (isTransposed) {
            w = height();
            h = width();
        }

        double[][] energyMatrix = new double[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double e = 0;
                if (isTransposed) {
                    e = energy(y, x);
                } else {
                    e = energy(x, y);
                }
                energyMatrix[y][x] = e;
            }
        }
        return energyMatrix;
    }

    private int[] findVerticalSeam(double[][] energyMatrix, int h, int w) {


        /*
        System.out.println("EnergyMatrix:\n");
        printMatrix(energyMatrix);
        System.out.println();

*/
        double[][] distTo = new double[h][w];
        int[][] edgeTo = new int[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (y == 0) {
                    distTo[y][x] = 1000;
                } else {
                    distTo[y][x] = Double.POSITIVE_INFINITY;
                }
            }
        }
/*
        System.out.println("DistTo:\n");
        printMatrix(distTo);
        System.out.println();
*/
        for (int y = 0; y < h - 1; y++) {
            for (int x = 0; x < w; x++) {
                int nextY = y + 1;

                // move of x coordinate: -1, 0, 1
                for (int mx = -1; mx <= 1; mx++) {
                    int nextX = x + mx;
                    if (nextX < 0 || nextX >= w) {
                        continue;
                    }
                    double e = energyMatrix[nextY][nextX];
                    if (distTo[nextY][nextX] > distTo[y][x] + e) {
                        distTo[nextY][nextX] = distTo[y][x] + e;
                        edgeTo[nextY][nextX] = x;
                    }
                }
            }
        }
/*
        System.out.println("DistTo:\n");
        printMatrix(distTo);
        System.out.println();

        System.out.println("EdgeTo:\n");
        printMatrix(edgeTo);
        System.out.println();*/

        // find x col on last y row that has min energy
        int lastY = h - 1;
        int minX = 0;
        double minEnergy = Double.POSITIVE_INFINITY;;
        for (int x = 0; x < w; x++) {
            if (distTo[lastY][x] < minEnergy) {
                minX = x;
                minEnergy = distTo[lastY][x];
            }
        }
/*
        System.out.println(minEnergy);
        System.out.println(minX);
*/
        // unwrap seam (shortest path of energy)
        int[] seam = new int [h];
        for (int y = lastY, x = minX; y >= 0; y--) {
            seam[y] = x;
            x = edgeTo[y][x];
        }

        return seam;
    }

    private void validateSeam(int[] seam, boolean direction) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }

        int w = width();
        int h = height();

        // Validate length
        if ((direction == DIRECTION_HORIZONTAL && seam.length != w) ||
                (direction == DIRECTION_VERTICAL && seam.length != h)) {
            throw new IllegalArgumentException();
        }

        // Validate seam vector
        for (int i = 0; i < seam.length; i++) {
            int value = seam[i];

            // value is Y coordinate
            if (direction == DIRECTION_HORIZONTAL) {
                if (value < 0 || value > h - 1) {
                    throw new IllegalArgumentException();
                }
            }

            // value is X coordinate
            if (direction == DIRECTION_VERTICAL) {
                if (value < 0 || value > w - 1) {
                    throw new IllegalArgumentException();
                }
            }

            // current and previous values in seam array differ not more than 1
            if (i > 0) {
                int diff = value - seam[i - 1];
                if (diff > 1 || diff < -1) {
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    private int[][] pictureToPixelsMatrix(Picture picture) {
        int w = picture.width();
        int h = picture.height();
        int[][] pixels = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pixels[y][x] = picture.getRGB(x, y);
            }
        }
        return pixels;
    }

    private Picture pixelsMatrixToPicture(int[][] pixels, int width, int height) {
        Picture picture = new Picture(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                picture.setRGB(x, y, pixels[y][x]);
            }
        }
        return picture;
    }

    public static void main(String[] args) {
        runTests();
    }

    private static void runTests() {
        if (!runEnergyTest() ||
                !runVerticalSeamTest() ||
                !runHorizontalSeamTest() ||
                !runRemoveVerticalSeam() ||
                !runRemoveHorizontalSeam()) {
            return;
        }
        System.out.println("Tests are OK!");
    }

    private static boolean runEnergyTest()
    {
        String filename = "./3x4.png";
        Picture picture = new Picture(filename);
        SeamCarver sc = new SeamCarver(picture);

        //printPictureRGB(picture);

        double[][] expectedEnergy = new double[][] {
            {1000, 1000, 1000},
            {1000, Math.sqrt(52225), 1000},
            {1000, Math.sqrt(52024), 1000},
            {1000, 1000, 1000},
        };

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++) {
                double expected = expectedEnergy[y][x];
                double e = sc.energy(x, y);
                double diff = Math.abs(expected - e);
                if (diff > 0.001) {
                    System.out.printf("Unexpected enegry value %.3f for (%d, %d) instread of %.3f (+- 0.001)",
                                      e, x, y, expected);
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean runVerticalSeamTest() {
        String filename = "./6x5.png";
        Picture picture = new Picture(filename);
        SeamCarver sc = new SeamCarver(picture);

        int[][] expectedSeamsTable = new int[][] {
                {3, 4, 3, 2, 1},
                {3, 4, 3, 2, 2},
                {3, 4, 3, 2, 3},

                {4, 4, 3, 2, 1},
                {4, 4, 3, 2, 2},
                {4, 4, 3, 2, 3},

                {5, 4, 3, 2, 1},
                {5, 4, 3, 2, 2},
                {5, 4, 3, 2, 3},
        };

        int[] seam = sc.findVerticalSeam();

        boolean isAnyHit = false;
        for (int[] expectedSeamVariant : expectedSeamsTable) {
            if (Arrays.equals(expectedSeamVariant, seam)) {
                isAnyHit = true;
                break;
            }
        }

        if (!isAnyHit) {
            System.out.printf("Unexpected v-seam `%s` instead one from these variants:\n%s",
                              Arrays.toString(seam),
                              matrixToString(expectedSeamsTable, "\n"));
            return false;
        }

        return true;
    }

    private static boolean runHorizontalSeamTest() {
        String filename = "./6x5.png";
        Picture picture = new Picture(filename);
        SeamCarver sc = new SeamCarver(picture);

        int[][] expectedSeamsTable = new int[][] {
                {1, 2, 1, 2, 1, 0},
                {1, 2, 1, 2, 1, 1},
                {1, 2, 1, 2, 1, 2},

                {2, 2, 1, 2, 1, 0},
                {2, 2, 1, 2, 1, 1},
                {2, 2, 1, 2, 1, 2},

                {3, 2, 1, 2, 1, 0},
                {3, 2, 1, 2, 1, 1},
                {3, 2, 1, 2, 1, 2},
            };

        int[] seam = sc.findHorizontalSeam();

        boolean isAnyHit = false;
        for (int[] expectedSeamVariant : expectedSeamsTable) {
            if (Arrays.equals(expectedSeamVariant, seam)) {
                isAnyHit = true;
                break;
            }
        }

        if (!isAnyHit) {
            System.out.printf("Unexpected h-seam `%s` instead one from these variants:\n%s",
                              Arrays.toString(seam),
                              matrixToString(expectedSeamsTable, "\n"));
            return false;
        }

        return true;
    }

    private static boolean runRemoveVerticalSeam() {
        String filename = "./6x5.png";
        Picture picture = new Picture(filename);
        SeamCarver sc = new SeamCarver(picture);

        sc.removeVerticalSeam(new int[]{3, 4, 3, 2, 2});
        if (sc.width() != 5) {
            System.out.printf("Unexpected width after remove vertical seam %d instead of %d", sc.width(), 5);
            return false;
        }

        if (sc.height() != 5) {
            System.out.printf("Unexpected height after remove vertical seam %d instead of %d", sc.height(), 5);
            return false;
        }

        Picture resultPicture = sc.picture();
        int[][] pixels = sc.pictureToPixelsMatrix(resultPicture);
        int[][] expectedPixels = new int[][] {
                {-11611825, -12617993, -10703009, -2986648,  -236169},
                {-2048074,  -9676462,  -11483930, -9397068,  -7432306},
                {-9060971,  -5511271,  -6970200,  -8885878,  -6050108},
                {-6037884,  -4491849,  -6385841,  -2339874,  -4372010},
                {-2918227,  -4400396,  -6052106,  -11567626, -2897566}
        };

        if (!isMatrixesEquals(pixels, expectedPixels)) {
            System.out.printf("Unexpected picture after remove vertical seam:\n%s\ninstread of:\n%s\n",
                              matrixToString(pixels, "\n"),
                               matrixToString(expectedPixels, "\n"));
            return false;
        }

        return true;
    }

    private static boolean runRemoveHorizontalSeam() {
        String filename = "./6x5.png";
        Picture picture = new Picture(filename);
        SeamCarver sc = new SeamCarver(picture);

        sc.removeHorizontalSeam(new int[]{2, 2, 1, 2, 1, 2});

        if (sc.width() != 6) {
            System.out.printf("Unexpected width after remove horizontal seam %d instead of %d", sc.width(), 6);
            return false;
        }

        if (sc.height() != 4) {
            System.out.printf("Unexpected height after remove horizontal seam %d instead of %d", sc.height(), 4);
            return false;
        }

        Picture resultPicture = sc.picture();
        int[][] pixels = sc.pictureToPixelsMatrix(resultPicture);
        int[][] expectedPixels = new int[][] {
                {-11611825, -12617993, -10703009, -833097,  -2986648,  -236169},
                {-2048074,  -9676462,   -6970200, -9397068, -8885878,  -7432306},
                {-6037884,  -4491849,  -10710715, -6385841, -2339874,  -4372010},
                {-2918227,  -4400396,   -2726076, -6052106, -11567626, -2897566}
        };

        if (!isMatrixesEquals(pixels, expectedPixels)) {
            System.out.printf("Unexpected picture after remove vertical seam:\n%s\ninstread of:\n%s\n",
                              matrixToString(pixels, "\n"),
                              matrixToString(expectedPixels, "\n"));
            return false;
        }

        return true;
    }

    private static void printPictureRGB(Picture picture)
    {
        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                Color c = picture.get(x, y);
                System.out.printf("(%3d, %3d, %3d) ", c.getRed(), c.getGreen(), c.getBlue());
            }
            System.out.println();
        }
    }

    private static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%10.3f ", matrix[i][j]);
            }
            System.out.println();
        }
    }

    private static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%3d ", matrix[i][j]);
            }
            System.out.println();
        }
    }

    private static String matrixToString(int[][] table, String sep) {
        String[] rows = new String[table.length];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                rows[i] = Arrays.toString(table[i]);
            }
        }
        return String.join(sep, rows);
    }

    private static boolean isMatrixesEquals(int[][] m1, int[][] m2) {
        if (m1.length != m2.length) {
            return false;
        }
        for (int y = 0; y < m1.length; y++) {
            if (!Arrays.equals(m1[y], m2[y])) {
                return false;
            }
        }
        return true;
    }
}
