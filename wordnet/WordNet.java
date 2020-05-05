/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Topological;

import java.util.HashMap;

public class WordNet {

    // noun => id
    private HashMap<String, SET<Integer>> nounToIdSet;

    // id => synset
    private HashMap<Integer, String> idToSynset;

    private Digraph G;
    private SAP sap;

    public WordNet(String synsets, String hypernyms)
    {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }

        int maxID = scanSynsetsFile(synsets);
        scanHypernymsFiles(hypernyms, maxID + 1);

        Topological tp = new Topological(G);
        if (!tp.hasOrder()) {
            throw new IllegalArgumentException("Not a DAG");
        }

        int roots = 0;
        for (int v = 0; v < G.V(); v++) {
            if (G.outdegree(v) == 0) {
                roots++;
            }
        }

        if (roots != 1) {
            throw new IllegalArgumentException("Not a rooted DAG");
        }

        sap = new SAP(G);
    }

    public Iterable<String> nouns()
    {
        return nounToIdSet.keySet();
    }

    public boolean isNoun(String noun)
    {
        if (noun == null) {
            throw new IllegalArgumentException();
        }

        if (nounToIdSet.containsKey(noun)) {
            return true;
        }
        noun = noun.replace(' ', '_');
        return nounToIdSet.containsKey(noun);
    }

    public String sap(String nounA, String nounB)
    {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        SET<Integer> setV = nounToIdSet.get(nounA);
        if (setV == null || setV.isEmpty()) {
            throw new IllegalArgumentException();
        }

        SET<Integer> setW = nounToIdSet.get(nounB);
        if (setW == null || setW.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int a = sap.ancestor(setV, setW);
        if (!idToSynset.containsKey(a)) {
            throw new IllegalArgumentException();
        }

        return idToSynset.get(a);
    }

    public int distance(String nounA, String nounB)
    {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        SET<Integer> setV = nounToIdSet.get(nounA);
        if (setV == null || setV.isEmpty()) {
            throw new IllegalArgumentException();
        }

        SET<Integer> setW = nounToIdSet.get(nounB);
        if (setW == null || setW.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return sap.length(setV, setW);
    }


    private int scanSynsetsFile(String fileName)
    {
        int maxID = 0;

        nounToIdSet = new HashMap<>();
        idToSynset = new HashMap<>();

        In in = new In(fileName);
        while (in.hasNextLine()) {
            String row = in.readLine();
            String[] columns = row.split(",");

            int id;
            String numID = columns[0];
            try {
                id = Integer.parseInt(numID);
                if (id > maxID) {
                    maxID = id;
                }
            } catch (NumberFormatException e) {
                continue;
            }

            String synset = columns[1];
            idToSynset.put(id, synset);

            String[] nouns = synset.split(" ");
            for (String nount : nouns) {
                if (!nounToIdSet.containsKey(nount)) {
                    nounToIdSet.put(nount, new SET<>());
                }
                nounToIdSet.get(nount).add(id);
            }
        }

        return maxID;
    }

    private void scanHypernymsFiles(String fileName, int V)
    {
        G = new Digraph(V);
        In in = new In(fileName);
        while (in.hasNextLine()) {
            String row = in.readLine();
            String[] columns = row.split(",");

            int v;
            String numID = columns[0];
            try {
                v = Integer.parseInt(numID);
            } catch (NumberFormatException e) {
                continue;
            }

            for (int i = 1; i < columns.length; i++) {
                int w;
                numID = columns[i];
                try {
                    w = Integer.parseInt(numID);
                } catch (NumberFormatException e) {
                    continue;
                }
                G.addEdge(v, w);
            }
        }
    }

    public static void main(String[] args) {
        runTests();
    }

    private static void runTests()
    {
        String synsetsFN = "./synsets.txt";
        String hypernymsFN = "./hypernyms.txt";
        WordNet wn = new WordNet(synsetsFN, hypernymsFN);

        if ( runWormAndBirdSapTest(wn) &&
                runSomeDistanceChecksTest(wn) &&
                checkNountCountTest(wn) ) {
            System.out.println("Tests are OK!");
        }
    }

    private static boolean runWormAndBirdSapTest(WordNet wn)
    {
        String synset = wn.sap("worm", "bird");
        int distance = wn.distance("worm", "bird");

        boolean isTestOk = true;

        String expectedSynset = "animal animate_being beast brute creature fauna";
        if (!synset.equals(expectedSynset)) {
            System.out.printf("runWormAndBirdSapTest FAIL: unexcpected sysnset \"%s\" instread of \"%s\"\n", synset, expectedSynset);
            isTestOk = false;
        }

        int expectedDistance = 5;
        if (distance != expectedDistance) {
            System.out.printf("runWormAndBirdSapTest FAIL: unexcpected distnace %d instread of %d\n", distance, expectedDistance);
            isTestOk = false;
        }

        if (!isTestOk) {
            return false;
        }

        return true;
    }

    private static boolean runSomeDistanceChecksTest(WordNet wn)
    {
        Object[][] table = {
            {"white_marlin", "mileage", 23},
            {"Black_Plague", "black_marlin", 33},
            {"American_water_spaniel", "histology", 27},
            {"Brown_Swiss", "barrel_roll", 29}
        };
        for (Object[] row : table) {
            String noun1 = (String)row[0];
            String noun2 = (String)row[1];
            int expectedDistance = (Integer)row[2];
            int distance = wn.distance(noun1, noun2);
            if (distance != expectedDistance) {
                System.out.printf("runSomeDistanceChecksTest FAIL: unexcpected distnace %d instread of %d for case (%s, %s)\n", distance, expectedDistance, noun1, noun2);
                return false;
            }
        }

        return true;
    }

    private static boolean checkNountCountTest(WordNet wn)
    {
        int expectedNounCount = 119188;

        int count = 0;
        for (String nount : wn.nouns()) {
            count++;
        }

        if (expectedNounCount != count) {
            System.out.printf("checkNountCountTest FAIL: unexcpected count of nounts %d instread of %d\n", count, expectedNounCount);
            return false;
        }

        return true;
    }
}
