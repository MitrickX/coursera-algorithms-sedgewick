import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    final private byte STATUS_CLOSED = 0;
    final private byte STATUS_OPENED = 1;
    final private byte STATUS_FULL = 2;


    // size of one dimention of grid
    private int sz;

    // sz * sz + 2 - total size of sites with virtual one (top)
    private int SZ;

    // ID of top site
    private int topID;

    // ID of bottom site
    private int bottomID;

    //
    private WeightedQuickUnionUF percolatesUF;

    //
    private WeightedQuickUnionUF fullUF;

    private byte[] statuses;

    //
    private int numberOpened;

    private boolean percolates;  //

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new java.lang.IllegalArgumentException();
        }

        // init sz's
        sz = n;
        SZ = n * n + 2;

        // init top and bottom ids
        topID = 0;

        //
        bottomID = SZ - 1;

        // statuses
        statuses = new byte[SZ];

        numberOpened = 0;

        // init UF
        percolatesUF = new WeightedQuickUnionUF(SZ);
        fullUF = new WeightedQuickUnionUF(SZ);
    }

    // ID of site in cell with row and col
    private int id(int row, int col) {
        return (row - 1) * sz + col;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 1 || row > sz || col < 1 || col > sz) {
            throw new java.lang.IllegalArgumentException();
        }

        int id = id(row, col);

        if (statuses[id] >= STATUS_OPENED) {   // already opened
            return;
        }

        statuses[id] = STATUS_OPENED;
        numberOpened++;

        // join top (0) with current first row site
        if (row == 1) {
            percolatesUF.union(topID, id);
            fullUF.union(topID, id);
            statuses[id] = STATUS_FULL;
        }

        if (row == sz) {
            percolatesUF.union(bottomID, id);
        }

        // connect with opened left neighbour
        if (col > 1) {
            int leftID = id(row, col - 1);
            if (statuses[leftID] >= STATUS_OPENED) {
                percolatesUF.union(id, leftID);
                fullUF.union(id, leftID);
            }
        }

        // connect with opened right neighbour
        if (col < sz) {
            int rightID = id(row, col + 1);
            if (statuses[rightID] >= STATUS_OPENED) {
                percolatesUF.union(id, rightID);
                fullUF.union(id, rightID);
            }
        }

        // connect with opened above neighbour
        if (row > 1) {
            int aboveID = id(row - 1, col);
            if (statuses[aboveID] >= STATUS_OPENED) {
                percolatesUF.union(id, aboveID);
                fullUF.union(id, aboveID);
            }
        }

        // connect with opened below neighbour
        if (row < sz) {
            int belowID = id(row + 1, col);
            if (statuses[belowID] >= STATUS_OPENED) {
                percolatesUF.union(id, belowID);
                fullUF.union(id, belowID);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row < 1 || row > sz || col < 1 || col > sz) {
            throw new java.lang.IllegalArgumentException();
        }
        int id = id(row, col);
        return statuses[id] >= STATUS_OPENED;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row < 1 || row > sz || col < 1 || col > sz) {
            throw new java.lang.IllegalArgumentException();
        }

        int id = id(row, col);

        if (statuses[id] == STATUS_CLOSED) {
            return false;
        }

        if (statuses[id] == STATUS_FULL) {
            return true;
        }

        if (fullUF.connected(id, topID)) {
            statuses[id] = STATUS_FULL;
        }

        return statuses[id] == STATUS_FULL;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numberOpened;
    }

    // does the system percolate?
    public boolean percolates() {
        if (!percolates) {
            percolates = percolatesUF.connected(topID, bottomID);
        }
        return percolates;
    }

    /*
    private void printOpened()
    {
        for (int i = 1; i <= sz; i++) {
            for (int j = 1; j <= sz; j++) {
                int id = id(i, j);
                if (opened[id]) {
                    System.out.print("o");
                } else {
                    System.out.print("x");
                }
            }
            System.out.println();
        }
        System.out.println();
    }*/

    public static void main(String[] args) {

        /*
        int n = StdIn.readInt();
        Percolation p = new Percolation(n);

        while (!StdIn.isEmpty()) {
            int row = StdIn.readInt();
            int col = StdIn.readInt();
            p.open(row + 1, col + 1);
        }


        //p.printOpened();

        System.out.println(p.percolates());*/


    }
}
