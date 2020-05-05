import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Iterator;

final public class Solver {

    private class SearchNode implements Comparable<SearchNode> {
        private Board board;
        private int moves;
        private SearchNode prevNode;
        private int manhattan;
        private boolean isTwin;

        public SearchNode(Board board, int moves, SearchNode prevNode, boolean isTwin) {
            this.board = board;
            this.moves = moves;
            this.prevNode = prevNode;
            this.manhattan = board.manhattan();
            this.isTwin = isTwin;
        }

        @Override
        public int compareTo(SearchNode that) {
            int manhattanWithMoves = manhattan + moves;
            int thatManhattanWithMoves = that.manhattan + that.moves;
            if (manhattanWithMoves < thatManhattanWithMoves) {
                return -1;
            } else if (manhattanWithMoves > thatManhattanWithMoves) {
                return +1;
            } else if (manhattan < that.manhattan) {
                return -1;
            } else if (manhattan > that.manhattan) {
                return +1;
            } else {
                return 0;
            }

            /*
            if (manhattan < o.manhattan) {
                return -1;
            } else if (manhattan > o.manhattan) {
                return +1;
            } else if (hamming < o.hamming) {
                return -1;
            } else if (hamming > o.hamming) {
                return +1;
            } else {
                return 0;
            }*/
        }
    }

    private SearchNode solutionNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new java.lang.IllegalArgumentException();
        }

        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
        pq.insert(new SearchNode(initial, 0, null, false));
        pq.insert(new SearchNode(initial.twin(), 0, null, true));

/*
        int maxSteps = 1000000;
        int step = 0;*/

        while (!pq.isEmpty()) {
            SearchNode node = pq.delMin();

            /*if (step > maxSteps) {
                break;
            }*/
/*
            System.out.printf("Step #%d\n", step);
            System.out.printf("manhattan = %d\n", node.manhattan);
            System.out.printf("hamming = %d\n", node.hamming);
            System.out.println(node.board);
            System.out.printf("is goal? %b\n", node.board.isGoal());
            System.out.printf("Has prev node? %b\n", node.prevNode != null);
            System.out.printf("is twin? %b\n", node.isTwin);
            System.out.println();

            step++;
*/

            if (node.board.isGoal()) {
                if (!node.isTwin) {
                    solutionNode = node;
                }
                break;
            }

            //int nodeIndex = 0;
            for (Board next : node.board.neighbors()) {
                SearchNode prevNode = node.prevNode;

                //System.out.printf("Node #%d ", nodeIndex);
                //nodeIndex++;

                if (prevNode != null && prevNode.board.equals(next)) {
                    /*
                    System.out.println("not inserted:");
                    System.out.println("next:");
                    System.out.println(next);
                    System.out.println("prevBoard:");
                    System.out.println(prevNode.board);*/
                    continue;
                }

                //System.out.println("inserted");

                pq.insert(new SearchNode(next, node.moves + 1, node, node.isTwin));
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solutionNode != null;
    }

    // min number of moves to solve initial board
    public int moves() {
        if (solutionNode != null) {
            return solutionNode.moves;
        }
        return -1;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (solutionNode == null) {
            return null;
        }
        Stack<Board> boards = new Stack<Board>();
        SearchNode node = solutionNode;
        while (node != null) {
            boards.push(node.board);
            node = node.prevNode;
        }
        return new Iterable<Board>() {
            @Override
            public Iterator<Board> iterator() {
                return boards.iterator();
            }
        };
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}