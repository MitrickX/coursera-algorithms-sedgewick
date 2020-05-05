/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wn;

    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException();
        }
        wn = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null || nouns.length < 2) {
            throw new IllegalArgumentException();
        }

        int[][] distances = calcDistanceMatrix(nouns);
        int n = nouns.length;

        int maxDistance = 0;
        int outcast = 0;

        for (int i = 0; i < n; i++) {
            int distance = 0;
            for (int j = 0; j < n; j++) {
                distance += distances[i][j];
            }
            if (distance > maxDistance) {
                maxDistance = distance;
                outcast = i;
            }
        }

        return nouns[outcast];
    }

    private int[][] calcDistanceMatrix(String[] nouns) {
        int n = nouns.length;

        int[][] distances = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distances[i][j] = 0;
                } else {
                    distances[i][j] = -1;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (distances[j][i] != -1) {
                    distances[i][j] = distances[j][i];
                } else {
                    int d = wn.distance(nouns[i], nouns[j]);
                    distances[i][j] = d;
                }
            }
        }

        return distances;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runClient(args);
        }
        runTests();
    }

    private static void runClient(String[] args)
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }

    private static void runTests()
    {
        String synsetsFN = "./synsets.txt";
        String hypernymsFN = "./hypernyms.txt";
        WordNet wn = new WordNet(synsetsFN, hypernymsFN);

        if ( runOutcastTest(wn, "./outcast5.txt", "table") &&
                runOutcastTest(wn, "./outcast8.txt", "bed") &&
                runOutcastTest(wn, "./outcast11.txt", "potato") ) {
            System.out.println("Tests are OK!");
        }
    }

    private static boolean runOutcastTest(WordNet wn, String fileName, String expected)
    {
        In in = new In(fileName);
        String[] nouns = in.readAllStrings();
        Outcast outcast = new Outcast(wn);
        String result = outcast.outcast(nouns);
        if (!result.equals(expected)) {
            System.out.printf("runOutcast5Test FAIL: unexcpected outcast result \"%s\" for file \"%s\" instread of \"%s\"\n", result, fileName, expected);
            return false;
        }
        return true;
    }
}
