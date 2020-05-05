import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.Iterator;

public final class Board {

    private char n; // n - size of one dimension
    private char[] tiles;

    private char blankIndex = 0;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {

        if (tiles == null) {
            return;
        }
        
        n = (char)tiles.length;
        this.tiles = intTableToCharArray(tiles);

        for (char i = 0; i < n * n; i++) {
            if (this.tiles[i] == 0) {
                blankIndex = i;
            }
        }

        //printTilesArray();
    }

    private Board cloneBoard() {
        Board b = new Board(null);

        char n = this.n;
        b.tiles = Arrays.copyOf(this.tiles, n * n);

        for (char i = 0; i < n * n; i++) {
            if (b.tiles[i] == 0) {
                b.blankIndex = i;
            }
        }

        b.n = this.n;
        return b;
    }

    private char[] intTableToCharArray(int[][] tiles) {
        int n = tiles.length;
        char[] aTiles = new char[n * n];

        int index;
        for (char i = 0; i < n; i++) {
            for (char j = 0; j < n; j++) {
                index = (i * n) + j;
                char tile = (char)tiles[i][j];
                aTiles[index] = tile;
            }
        }

        return aTiles;
    }

    private void printTilesArray()
    {
        System.out.println((int)n);
        for (char i = 0; i < n * n; i++) {
            System.out.print((int)tiles[i]);
            if (i < n * n - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    // string representation of this board
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append((int)n);
        builder.append("\n");

        // len of number for print tile
        int len = (int)Math.floor(Math.log10((n * n - 1))) + 1;
        if (len <= 1) {
            len++;
        }

        String format = "%" + len + "d";

        int sq_n = n * n;
        for (char i = 0; i < sq_n; i++) {
            char tile = this.tiles[i];
            builder.append(String.format(format, (int)tile));
            if (i % n < n - 1) {
                builder.append(" ");
            } else if (i != sq_n - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int hamming = 0;
        for (char i = 0; i < n * n; i++) {
            // (i + 1) - is goal tile
            char tile = tiles[i];
            if (tile != 0 && tile != (i + 1)) {
                hamming++;
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattan = 0;
        for (char i = 0; i < n * n; i++) {
            // (i + 1) - is goal tile
            char tile = tiles[i];
            if (tile != 0) {
                int tileRow = (tile - 1) / n;
                int tileCol = (tile - 1) % n;
                int goalRow = i / n;
                int goalCol = i % n;
                manhattan += Math.abs(goalRow - tileRow) + Math.abs(goalCol - tileCol);
            }
        }
        return manhattan;
    }

    private boolean canSwapBlankWithAbove() {
        return (blankIndex / n) > 0;
    }

    private boolean canSwapBlankWithBelow() {
        return (blankIndex / n) < n - 1;
    }

    private boolean canSwapBlankWithLeft() {
        return (blankIndex % n) > 0;
    }

    private boolean canSwapBlankWithRight() {
        return (blankIndex % n) < n - 1;
    }

    private void swap(char index1, char index2) {
        char tmp = tiles[index1];
        tiles[index1] = tiles[index2];
        tiles[index2] = tmp;
    }

    private void swapBlankWithAbove() {
        int blankRow = (blankIndex / n);
        int blankCol = (blankIndex % n);
        int aboveRow = blankRow - 1;
        char index = (char)(aboveRow * n + blankCol);
        swap(index, blankIndex);
        blankIndex = index;
    }

    private void swapBlankWithBelow() {
        int blankRow = (blankIndex / n);
        int blankCol = (blankIndex % n);
        int aboveRow = blankRow + 1;
        char index = (char)(aboveRow * n + blankCol);
        swap(index, blankIndex);
        blankIndex = index;
    }

    private void swapBlankWithLeft() {
        char index = (char)(blankIndex - 1);
        swap(index, blankIndex);
        blankIndex = index;
    }

    private void swapBlankWithRight() {
        char index = (char)(blankIndex + 1);
        swap(index, blankIndex);
        blankIndex = index;
    }

    private Board cloneWithSwapedBlankWithAbove() {
        Board clone = this.cloneBoard();
        clone.swapBlankWithAbove();
        return clone;
    }

    private Board cloneWithSwapedBlankWithBelow() {
        Board clone = this.cloneBoard();
        clone.swapBlankWithBelow();
        return clone;
    }

    private Board cloneWithSwapedBlankWithLeft() {
        Board clone = this.cloneBoard();
        clone.swapBlankWithLeft();
        return clone;
    }

    private Board cloneWithSwapedBlankWithRight() {
        Board clone = this.cloneBoard();
        clone.swapBlankWithRight();
        return clone;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (char i = 0; i < n * n; i++) {
            // (i + 1) - is goal tile
            if (tiles[i] != 0 && tiles[i] != (i + 1)) {
                return false;
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }

        if (!(y instanceof Board)) {
            return false;
        }

        Board that = (Board) y;

        if (that == this) {
            return true;
        }

        if (that.n != n) {
            return false;
        }

        for (char i = 0; i < n * n; i++) {
            if (tiles[i] != that.tiles[i]) {
                return false;
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Board that = this;
        return new Iterable<Board>() {
            @Override
            public Iterator<Board> iterator() {
                return new Neighbors(that);
            }
        };
    }

    private class Neighbors implements Iterator<Board> {

        private Board board;
        private char[] cases;    // 1 - above, 2 - right, 3 - below, 4 - left, 0 - none
        private char casesLen;
        private char caseIndex = 0;

        public Neighbors(Board b) {
            board = b;

            cases = new char[]{0, 0, 0, 0};
            casesLen = 0;

            if (board.canSwapBlankWithAbove()) {
                cases[casesLen++] = 1;
            }
            if (board.canSwapBlankWithRight()) {
                cases[casesLen++] = 2;
            }
            if (board.canSwapBlankWithBelow()) {
                cases[casesLen++] = 3;
            }
            if (board.canSwapBlankWithLeft()) {
                cases[casesLen++] = 4;
            }
        }


        @Override
        public boolean hasNext() {
            if (caseIndex < casesLen) {
                return true;
            } else {
                board = null;
                cases = null;
                return false;
            }
        }

        @Override
        public Board next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }

            int caseCode = cases[caseIndex++];
            switch (caseCode) {
                case 1:
                    return board.cloneWithSwapedBlankWithAbove();
                case 2:
                    return board.cloneWithSwapedBlankWithRight();
                case 3:
                    return board.cloneWithSwapedBlankWithBelow();
                case 4:
                    return board.cloneWithSwapedBlankWithLeft();
                default:
                    throw new java.util.NoSuchElementException();

            }
        }
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {

        char index1 = 0;
        char index2 = 0;

        boolean isFirst = false;

        for (char i = 0; i < n * n; i++) {
            if (tiles[i] != 0) {
                if (!isFirst) {
                    index1 = i;
                    isFirst = true;
                } else {
                    index2 = i;
                    break;
                }
            }

        }

        Board twin = this.cloneBoard();
        twin.swap(index1, index2);
        return twin;
    }

    // unit testing (not graded)
    public static void main(String[] args) {

        Board board;
        String filename = "./puzzle3x3-00.txt";

        board = factoryBoardFromFile(filename);
        System.out.printf("TEST for %s\n", filename);

        System.out.println(board);
        if (board.dimension() != 3) {
            System.out.printf("FAIL: dimension must be %d not %d\n", 3, board.dimension());
        }

        if (board.hamming() != 0) {
            System.out.printf("FAIL: hamming must be %d not %d\n", 0, board.hamming());
        }

        if (board.manhattan() != 0) {
            System.out.printf("FAIL: manhattan must be %d not %d\n", 0, board.manhattan());
        }

        if (!board.isGoal()) {
            System.out.printf("FAIL: board %s is goal\n", filename);
        }

        System.out.println("Neighbours: ");
        for (Board next : board.neighbors()) {
            System.out.println(next);
        }

        System.out.println();

        filename = "./puzzle3x3-01.txt";
        board = factoryBoardFromFile(filename);

        System.out.printf("TEST for %s\n", filename);
        System.out.println(board);
        if (board.dimension() != 3) {
            System.out.printf("FAIL: dimension must be %d not %d\n", 3, board.dimension());
        }

        if (board.hamming() != 1) {
            System.out.printf("FAIL: hamming must be %d not %d\n", 1, board.hamming());
        }

        if (board.manhattan() != 1) {
            System.out.printf("FAIL: manhattan must be %d not %d\n", 1, board.manhattan());
        }

        if (board.isGoal()) {
            System.out.printf("FAIL: board %s is not goal\n", filename);
        }

        System.out.println("Neighbours: ");
        for (Board next : board.neighbors()) {
            System.out.println(next);
        }

        System.out.println();

        filename = "./puzzle3x3-18.txt";
        board = factoryBoardFromFile(filename);

        System.out.printf("TEST for %s\n", filename);
        System.out.println(board);
        if (board.dimension() != 3) {
            System.out.printf("FAIL: dimension must be %d not %d\n", 3, board.dimension());
        }

        if (board.hamming() != 6) {
            System.out.printf("FAIL: hamming must be %d not %d\n", 6, board.hamming());
        }

        if (board.manhattan() != 12) {
            System.out.printf("FAIL: manhattan must be %d not %d\n", 12, board.manhattan());
        }

        if (board.isGoal()) {
            System.out.printf("FAIL: board %s is not goal\n", filename);
        }

        System.out.println("Neighbours: ");
        for (Board next : board.neighbors()) {
            System.out.println(next);
        }

        // equality tests
        if (board.equals(null)) {
            System.out.printf("FAIL: board must not be equal to NULL\n");
        }
        if (board.equals("123123")) {
            System.out.printf("FAIL: board must no be equal to string\n");
        }
        if (!board.equals(board)) {
            System.out.printf("FAIL: board must equal to itself\n");
        }
        if (board.equals(factoryBoardFromFile("./puzzle2x2-00.txt"))) {
            System.out.printf("FAIL: board size 3 must not be equal to board size 2\n");
        }
        if (!board.equals(factoryBoardFromFile(filename))) {
            System.out.printf("FAIL: board must equal to board read from the same file\n");
        }
        if (board.equals(factoryBoardFromFile("puzzle3x3-17.txt"))) {
            System.out.printf("FAIL: board must be not equals cause from different files\n");
        }


        filename = "./puzzle02.txt";
        board = factoryBoardFromFile(filename);

        System.out.printf("TEST for %s\n", filename);
        System.out.println(board);
        if (board.dimension() != 9) {
            System.out.printf("FAIL: dimension must be %d not %d\n", 3, board.dimension());
        }

        if (board.hamming() != 2) {
            System.out.printf("FAIL: hamming must be %d not %d\n", 2, board.hamming());
        }

        if (board.manhattan() != 2) {
            System.out.printf("FAIL: manhattan must be %d not %d\n", 2, board.manhattan());
        }

        if (board.isGoal()) {
            System.out.printf("FAIL: board %s is not goal\n", filename);
        }

        int nextIndex = 0;
        System.out.println("Neighbours: ");
        for (Board next : board.neighbors()) {
            System.out.println(next);
            if (next.equals(board)) {
                System.out.printf("FAIL: next (#%d) can't be equal with original board\n", nextIndex);
            }
            if (board.equals(next)) {
                System.out.printf("FAIL: original board can't be equal with next (#%d)\n", nextIndex);
            }
            nextIndex++;
        }

        System.out.println();

        int[][] tiles = new int[3][3];
        tiles[0][0] = 8;
        tiles[0][1] = 1;
        tiles[0][2] = 3;
        tiles[1][0] = 4;
        tiles[1][1] = 0;
        tiles[1][2] = 2;
        tiles[2][0] = 7;
        tiles[2][1] = 6;
        tiles[2][2] = 5;
        board = new Board(tiles);

        if (board.hamming() != 5) {
            System.out.printf("FAIL: hamming must be %d not %d\n", 5, board.hamming());
        }

        if (board.manhattan() != 10) {
            System.out.printf("FAIL: manhattan must be %d not %d\n", 10, board.manhattan());
        }


    }

    private static Board factoryBoardFromFile(String filename)
    {
        In in = new In(filename);
        int n = in.readInt();
        int[][] tiles = new int[n][n];

        OUTER:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (in.isEmpty()) {
                    break OUTER;
                }
                tiles[i][j] = in.readInt();
            }
        }

        return new Board(tiles);
    }

}