/******************************************************************************
 *  Compilation:  javac-algs4 PuzzleChecker.java
 *  Execution:    java-algs4 PuzzleChecker filename1.txt filename2.txt ...
 *  Dependencies: Board.java Solver.java
 *
 *  This program creates an initial board from each filename specified
 *  on the command line and finds the minimum number of moves to
 *  reach the goal state.
 *
 *  % java-algs4 PuzzleChecker puzzle*.txt
 *  puzzle00.txt: 0
 *  puzzle01.txt: 1
 *  puzzle02.txt: 2
 *  puzzle03.txt: 3
 *  puzzle04.txt: 4
 *  puzzle05.txt: 5
 *  puzzle06.txt: 6
 *  ...
 *  puzzle3x3-impossible: -1
 *  ...
 *  puzzle42.txt: 42
 *  puzzle43.txt: 43
 *  puzzle44.txt: 44
 *  puzzle45.txt: 45
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

import java.io.File;
import java.util.ArrayList;

public class PuzzleChecker {
    public static void main(String[] args) {

        ArrayList<String> filenames = new ArrayList<>();

        File dir = new File("./");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String name = child.getName();
                if (name.length() > 4 && name.substring(name.length() - 4).equals(".txt")) {
                    if (name.equals("puzzle49.txt")) {
                        break;
                    }
                    filenames.add(name);
                }
            }
        }

        // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            // solve the slider puzzle
            Board initial = new Board(tiles);
            Solver solver = new Solver(initial);

            boolean isSolvable = solver.isSolvable();

            StdOut.printf("%s: %d (%s)\n", filename, solver.moves(), isSolvable ? "solvable" : "not solvable");

            if (isSolvable) {
                Iterable<Board> iterable = solver.solution();
                if (iterable == null) {
                    System.out.println("FAIL: iterable of solvable must not null");
                }
                Board first = null, last = null;
                for (Board b : iterable) {
                    if (first == null) {
                        first = b;
                    }
                    last = b;
                }
                if (!first.equals(initial)) {
                    System.out.println("FAIL: first item in solution must be initial");
                }
                if (!last.isGoal()) {
                    System.out.println("FAIL: last item in solution must be goal");
                }
            } else {
                if (solver.moves() != -1) {
                    System.out.println("FAIL: moves of not solvable must be -1");
                }
                Iterable<Board> iterable = solver.solution();
                if (iterable != null) {
                    System.out.println("FAIL: iterable of not solvable must null");
                }
            }

            //solution

        }
    }
}
