/* *****************************************************************************
 *  Name:    Alan Turing
 *  NetID:   aturing
 *  Precept: P00
 *
 *  Description:  Prints 'Hello, World' to the terminal window.
 *                By tradition, this is everyone's first program.
 *                Prof. Brian Kernighan initiated this tradition in 1974.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    //
    private int N;

    //
    private int T;

    //
    private double mean;

    //
    private double s;

    //
    private double lo;

    //
    private double ho;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials)
    {
        if (n <= 0 || trials <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        N = n;
        T = trials;

        double[] tm = new double[T];
        for (int t = 0; t < T; t++) {
            tm[t] = runTrail();
        }

        mean = StdStats.mean(tm);
        s = StdStats.stddev(tm);

        double div = (1.96 * s)/Math.sqrt(T);
        lo = mean - div;
        ho = mean + div;

    }

    private double runTrail()
    {
        Percolation p = new Percolation(N);

        while (!p.percolates()) {
            int row = StdRandom.uniform(1, N + 1);
            int col = StdRandom.uniform(1, N + 1);
            p.open(row, col);
        }

        int numberOfOpenSites = p.numberOfOpenSites();
        return numberOfOpenSites / (double)(N * N);
    }

    // sample mean of percolation threshold
    public double mean()
    {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev()
    {
        return s;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo()
    {
        return lo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi()
    {
        return ho;
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);
        PercolationStats ps = new PercolationStats(n, T);

        System.out.print("mean                    = ");
        System.out.print(ps.mean());
        System.out.println();

        System.out.print("stddev                  = ");
        System.out.print(ps.stddev());
        System.out.println();

        System.out.print("95% confidence interval = [");
        System.out.print(ps.confidenceLo());
        System.out.print(", ");
        System.out.print(ps.confidenceHi());
        System.out.print("]");
        System.out.println();
    }
}
