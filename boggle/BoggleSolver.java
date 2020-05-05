/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Knuth;
import edu.princeton.cs.algs4.SET;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BoggleSolver {

    final private static int R = 26;    // A-Z symbols: A=1,B=2,...Z=27
    final private static int R1 = R + 1;   // R + 1

    private Node[] roots;

    private BoggleBoard board;
    private int[][] marked;
    private SET<String> words;

    private static class Node {
        // Symbols: A=1,B=2,...Z=27
        private byte symbol;
        private Node left, mid, right;  // left, middle, and right subtries
        private boolean contains;
    }

    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException();
        }

        if (dictionary.length <= 0) {
            return;
        }

        roots = new Node[R1];

        int n = dictionary.length;
        for (int i = 0; i < n; i++) {
            put(dictionary[i]);
        }

    }

    private Node getRoot(byte symbol) {
        return roots[symbol];
    }

    private void setRoot(Node node, byte symbol) {
        roots[symbol] = node;
    }

    private boolean contains(String key) {
        if (key == null || key.length() < 1) {
            throw new IllegalArgumentException("key must string that has length >= 1");
        }

        byte symbol = (byte)(key.charAt(0) - 'A' + 1);
        return contains(getRoot(symbol), key, 0);
    }

    private boolean contains(Node node, String key, int d) {
        if (node == null) {
            return false;
        }
        char ch = key.charAt(d);
        byte symbol = (byte) (ch - 'A' + 1);
        if (symbol < node.symbol) {
            return contains(node.left, key, d);
        } else if (symbol > node.symbol) {
            return contains(node.right, key, d);
        } else if (d < key.length() - 1) {
            return contains(node.mid, key, d+1);
        }
        return node.contains;
    }

    private void put(String key) {
        if (key == null || key.length() < 1) {
            throw new IllegalArgumentException("key must string that has length >= 1");
        }

        byte symbol = (byte)(key.charAt(0) - 'A' + 1);

        Node root = getRoot(symbol);

        root = put(root, key, 0);

        setRoot(root, symbol);
    }

    private Node put(Node node, String key, int d) {
        byte symbol = (byte)(key.charAt(d) - 'A' + 1);

        if (node == null) {
            node = new Node();
            node.symbol = symbol;
        }

        if (symbol < node.symbol) {
            node.left = put(node.left, key, d);
        } else if (symbol > node.symbol) {
            node.right = put(node.right, key, d);
        } else if (d < key.length() - 1) {
            node.mid = put(node.mid, key, d+1);
        } else {
            node.contains = true;
        }

        return node;
    }

    public Iterable<String> getAllValidWords(BoggleBoard board)
    {
        words = new SET<>();

        this.board = board;
        marked = new int[board.rows()][board.cols()];

        StringBuilder charList = new StringBuilder();

        int rows = board.rows();
        int cols = board.cols();

        int mark = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                charList.setLength(0);
                byte symbol = (byte) (board.getLetter(i, j) - 'A' + 1);
                dfs(charList, getRoot(symbol), i, j, mark++, (char)0);
            }
        }

        return words;
    }

    private void dfs(StringBuilder charList, Node node, int i, int j, int mark, char c)
    {
        if (node == null) {
            return;
        }

        // letter of current cell or letter that will be passed explicitly (special case for 'U' after 'Q')
        if (c == 0) {
            c = board.getLetter(i, j);
        }

        // define next node
        byte symbol = (byte) (c - 'A' + 1);
        if (symbol < node.symbol) {
            dfs(charList, node.left, i, j, mark, c);
            return;
        } else if (symbol > node.symbol) {
            dfs(charList, node.right, i, j, mark, c);
            return;
        }

        // mark as visited cell
        marked[i][j] = mark;

        charList.append(c);

        if (c == 'Q') {
            dfs(charList, node.mid, i, j, mark, 'U');
            marked[i][j] = 0;
            charList.setLength(charList.length() - 1);
            return;
        }

        if (node.contains && charList.length() > 2) {
            String word = charList.toString();
            words.add(word);
        }

        node = node.mid;

        int leftI = i;
        int leftJ = j - 1;

        int rightI = i;
        int rightJ = j + 1;

        int upI = i - 1;
        int upJ = j;

        int downI = i + 1;
        int downJ = j;

        boolean canLeft = leftJ >= 0;
        boolean canRight = rightJ < board.cols();
        boolean canUp = upI >= 0;
        boolean canDown = downI < board.rows();

        // left
        if (canLeft && marked[leftI][leftJ] != mark) {
            dfs(charList, node, leftI, leftJ, mark, (char)0);
        }

        // right
        if (canRight && marked[rightI][rightJ] != mark) {
            dfs(charList, node, rightI, rightJ, mark, (char)0);
        }

        // up
        if (canUp && marked[upI][upJ] != mark) {
            dfs(charList, node, upI, upJ, mark, (char)0);
        }

        // down
        if (canDown && marked[downI][downJ] != mark) {
            dfs(charList, node, downI, downJ, mark, (char)0);
        }

        // left-up diagonal
        if (canLeft && canUp && marked[upI][leftJ] != mark) {
            dfs(charList, node, upI, leftJ, mark, (char)0);
        }

        // left-down diagonal
        if (canLeft && canDown && marked[downI][leftJ] != mark) {
            dfs(charList, node, downI, leftJ, mark, (char)0);
        }

        // right-up diagonal
        if (canRight && canUp && marked[upI][rightJ] != mark) {
            dfs(charList, node, upI, rightJ, mark, (char)0);
        }

        // right-down diagonal
        if (canRight && canDown && marked[downI][rightJ] != mark) {
            dfs(charList, node, downI, rightJ, mark, (char)0);
        }

        // this cell done - unmark
        marked[i][j] = 0;
        int len = charList.length();

        if (len > 1) {
            charList.setLength(len - 1);
        }
    }


    public int scoreOf(String word)
    {
        if (!contains(word)) {
            return 0;
        }

        int len = word.length();
        if (len < 3) {
            return 0;
        }

        switch (len) {
            case 3:
                return 1;
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:    // 8+
                return 11;
        }
    }

    public static void main(String[] args) {
        if (
            runBuildTestBySmallLenWordsTest() &&
            runBuildTest() &&
            runBuildSuffixesTest() &&
            runScoreTest() &&
            runSolveTest1() &&
            runSolveTest2() &&
            runSolveTest3() &&
            runSolveTestYawls() &&
            runSolveDict16QBoard16Q() &&
            runSolveQwerty()
        )
        {
            System.out.println("Tests are OK");
        }

        if (stressTest()) {
            System.out.println("Stress tests are OK");
        }

        if (testTimingConstructorYawl()) {
            System.out.println("Timing tests are OK");
        }
    }

    private static boolean runBuildTestBySmallLenWordsTest()
    {
        String testName = "runBuildTestBySmallLenWordsTest";

        String[] dictionary = new String[] {
                "Q",
                "OP",
                "ZYA",
                "DPO",
                "AAA"
        };

        BoggleSolver solver = new BoggleSolver(dictionary);

        String[] missedWords = new String[] {
                "X",
                "ZY",
                "ONS",
                "HELLO",
                "NEL",
                "BBBB",
                "BBB"
        };

        for (String word : missedWords) {
            if (solver.contains(word)) {
                System.out.printf("FAIL %s: unexpected that solver SET contains `%s`", testName, word);
                return false;
            }
        }

        for (String word : dictionary) {
            if (!solver.contains(word)) {
                System.out.printf("FAIL %s: unexpected that solver SET NOT contains word from dictionary `%s`", testName, word);
                return false;
            }
        }

        return true;
    }

    private static boolean runBuildTest()
    {
        String testName = "runBuildTest";

        String[] dictionary = new String[] {
                "ZYA",
                "DPO",
                "AAA",
                "ZYAONEW",
                "DMITRY",
                "JAVA",
                "PROGRAMMING",
                "BOGGLE"
        };

        BoggleSolver solver = new BoggleSolver(dictionary);

        String[] missedWords = new String[] {
                "ONS",
                "HELLO",
                "NEL",
                "BBBB",
                "BBB",
                "PROGRAMMER",
                "JAVASCRIPT",
        };

        for (String word : missedWords) {
            if (solver.contains(word)) {
                System.out.printf("FAIL %s: unexpected that solver SET contains `%s`", testName, word);
                return false;
            }
        }

        for (String word : dictionary) {
            if (!solver.contains(word)) {
                System.out.printf("FAIL %s: unexpected that solver SET NOT contains word from dictionary `%s`", testName, word);
                return false;
            }
        }

        return true;
    }

    private static boolean runBuildSuffixesTest()
    {
        String testName = "runBuildSuffixesTest";

        String[] dictionary = new String[] {
                "X",
                "XY",
                "XYZ",
                "XYZA",
                "XYZAB",
                "XYZABC"
        };

        BoggleSolver solver = new BoggleSolver(dictionary);

        String[] missedWords = new String[] {
                "Y",
                "XA",
                "XYA",
                "XYZB",
                "XYZAC",
                "XYZABD",
            };

        for (String word : missedWords) {
            if (solver.contains(word)) {
                System.out.printf("FAIL %s: unexpected that solver SET contains `%s`", testName, word);
                return false;
            }
        }

        for (String word : dictionary) {
            if (!solver.contains(word)) {
                System.out.printf("FAIL %s: unexpected that solver SET NOT contains word from dictionary `%s`", testName, word);
                return false;
            }
        }

        return true;
    }

    private static boolean runScoreTest()
    {
        String testName = "runScoreTest";

        HashMap<String, Integer> expectedScores = new HashMap<>();
        expectedScores.put("ZYA", 1);
        expectedScores.put("JAVA", 1);
        expectedScores.put("HELLO", 2);
        expectedScores.put("DMITRY", 3);
        expectedScores.put("ZYAONEW", 5);
        expectedScores.put("PROGRAMM", 11);
        expectedScores.put("PROGRAMMING", 11);

        String[] dictionary = new String[expectedScores.size()];

        int i = 0;
        for (String word : expectedScores.keySet()) {
            dictionary[i++] = word;
        }

        BoggleSolver solver = new BoggleSolver(dictionary);

        String[] missedWords = new String[] {
                "ONS",
                "HELLOM",
                "NEL",
                "BBBB",
                "BBB",
                "PROGRAMMER",
                "JAVASCRIPT",
        };

        for (String word : missedWords) {
            int score = solver.scoreOf(word);
            if (score != 0) {
                System.out.printf("FAIL %s: unexpected that scoreOf of %s is %d instread of %d", testName, word, score, 0);
                return false;
            }
        }

        for (String word : dictionary) {
            int score = solver.scoreOf(word);
            int expected = expectedScores.get(word);
            if (score != expected) {
                System.out.printf("FAIL %s: unexpected that scoreOf of %s is %d instread of %d", testName, word, score, expected);
                return false;
            }
        }

        return true;
    }

    private static boolean runSolveTest1()
    {
        String testName = "runSolveTest1";

        String boardFileName = "./board4x4.txt";
        String dictFileName = "./dictionary-algs4.txt";

        BoggleBoard board = new BoggleBoard(boardFileName);
        String[] dictionary = readDictionary(dictFileName);

        BoggleSolver solver = new BoggleSolver(dictionary);

        int totalScore = 0;
        Iterable<String> words = solver.getAllValidWords(board);
        for (String word : words) {
            totalScore += solver.scoreOf(word);
        }

        int expectedScore = 33;
        if (expectedScore != totalScore) {
            System.out.printf("FAIL %s: unexpected total score %d instread of %d", testName, totalScore, expectedScore);
            return false;
        }

        return true;
    }

    private static boolean runSolveTest2()
    {
        String testName = "runSolveTest2";

        String boardFileName = "./board-q.txt";
        String dictFileName = "./dictionary-algs4.txt";

        BoggleBoard board = new BoggleBoard(boardFileName);

        String[] dictionary = readDictionary(dictFileName);

        BoggleSolver solver = new BoggleSolver(dictionary);

        int totalScore = 0;

        ArrayList<String> wordList = new ArrayList<>();
        Iterable<String> words = solver.getAllValidWords(board);
        for (String word : words) {
            wordList.add(word);
            totalScore += solver.scoreOf(word);
        }

        String[] wordArray = new String[wordList.size()];
        int i = 0;
        for (String word : words) {
            wordArray[i++] = word;
        }
        Arrays.sort(wordArray);

        String[] expectedWords = new String[] {
                "EQUATION",
                "EQUATIONS",
                "ITS",
                "LET",
                "LETS",
                "NET",
                "ONE",
                "QUERIES",
                "QUESTION",
                "QUESTIONS",
                "QUITE",
                "REQUEST",
                "REQUIRE",
                "RES",
                "REST",
                "SER",
                "SIN",
                "SINE",
                "SIT",
                "SITE",
                "SITS",
                "STATE",
                "TAT",
                "TEN",
                "TENS",
                "TIE",
                "TIES",
                "TIN",
                "TRIES",
        };

        if (!Arrays.equals(wordArray, expectedWords)) {
            System.out.printf("FAIL %s: unexpected worlds\n%s\n instead of\n%s\n", testName, Arrays.toString(wordArray), Arrays.toString(expectedWords));
            return false;
        }

        int expectedTotalScore = 84;
        if (totalScore != expectedTotalScore) {
            System.out.printf("FAIL %s: unexpected total score %d instead of %d", testName, totalScore, expectedTotalScore);
            return false;
        }

        return true;
    }

    private static boolean runSolveTest3()
    {
        String testName = "runSolveTest3";

        String dictFileName = "./dictionary-yawl.txt";

        BoggleBoard board = new BoggleBoard(new char[][]{
                {'V', 'V', 'F', 'A'},
                {'N', 'I', 'U', 'S'},
                {'I', 'T', 'Q', 'T'},
                {'A', 'A', 'A', 'L'}
        });

        String[] dictionary = readDictionary(dictFileName);

        BoggleSolver solver = new BoggleSolver(dictionary);

        int len = 0;

        Iterable<String> words = solver.getAllValidWords(board);
        for (String word : words) {
            len++;
        }

        int expectedLen = 68;
        if (len !=  expectedLen) {
            System.out.printf("FAIL %s: unexpected list length %d instead of %d", testName, len, expectedLen);
            return false;
        }

        return true;
    }

    private static boolean runSolveTestYawls()
    {
        File path = new File("./");

        File [] files = path.listFiles();
        for (int i = 0; i < files.length; i++){
            if (!files[i].isFile()){
                continue;
            }
            String name = files[i].getName();
            if (name.length() > 12 && name.substring(0, 12).equals("board-points")) {
                String number = name.substring(12, name.length() - 4);
                int expectedScore = Integer.parseInt(number);
                if (!runSolveTestYawl(name, expectedScore)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean runSolveTestYawl(String boardFileName, int expectedTotalScore)
    {
        String testName = String.format("runSolveTestYawls (%s)", boardFileName);

        String dictFileName = "./dictionary-yawl.txt";

        BoggleBoard board = new BoggleBoard(boardFileName);

        String[] dictionary = readDictionary(dictFileName);

        BoggleSolver solver = new BoggleSolver(dictionary);

        int totalScore = 0;
        Iterable<String> words = solver.getAllValidWords(board);
        for (String word : words) {
            totalScore += solver.scoreOf(word);
        }

        if (totalScore != expectedTotalScore) {
            System.out.printf("FAIL %s: unexpected total score %d instead of %d", testName, totalScore, expectedTotalScore);
            return false;
        }

        return true;
    }

    private static boolean runSolveQwerty()
    {
        String testName = "runSolveQwerty";

        String boardFileName = "./board-qwerty.txt";
        String dictFileName = "./dictionary-yawl.txt";

        BoggleBoard board = new BoggleBoard(boardFileName);

        String[] dictionary = readDictionary(dictFileName);

        BoggleSolver solver = new BoggleSolver(dictionary);

        int totalScore = 0;
        int len = 0;
        Iterable<String> words = solver.getAllValidWords(board);

        for (String word : words) {
            totalScore += solver.scoreOf(word);
            len++;
        }

        int expectedLen = 22;
        int expectedTotalScore = 27;

        if (totalScore != expectedTotalScore) {
            System.out.printf("FAIL %s: unexpected total score %d instead of %d", testName, totalScore, expectedTotalScore);
            return false;
        }

        if (len != expectedLen) {
            System.out.printf("FAIL %s: unexpected total count of entries %d instead of %d", testName, len, expectedLen);
            return false;
        }

        return true;
    }

    private static boolean runSolveDict16QBoard16Q()
    {
        String testName = "runSolveDict16QBoard16Q";

        String boardFileName = "./board-16q.txt";
        String dictFileName = "./dictionary-16q.txt";

        BoggleBoard board = new BoggleBoard(boardFileName);

        String[] dictionary = readDictionary(dictFileName);

        BoggleSolver solver = new BoggleSolver(dictionary);

        int totalScore = 0;
        int len = 0;
        Iterable<String> words = solver.getAllValidWords(board);

        for (String word : words) {
            totalScore += solver.scoreOf(word);
            len++;
        }

        if (totalScore <= 0 || len <= 0) {
            System.out.printf("FAIL %s: totalScore <= 0 OR len <= 0\n", testName);
            return false;
        }

        return true;
    }


    private static boolean stressTest()
    {
        String dictFileName = "./dictionary-yawl.txt";

        String[] dictionary = readDictionary(dictFileName);

        BoggleSolver solver = new BoggleSolver(dictionary);

        long start = java.lang.System.nanoTime();
        for (int i = 0; i < 5000; i++) {
            BoggleBoard board = new BoggleBoard();
            solver.getAllValidWords(board);
        }
        long end = java.lang.System.nanoTime();
        long elapsed = end - start;
        long oneSecond = (long)Math.pow(10, 9);
        System.out.println((double)elapsed / (double)oneSecond);
        if (elapsed > oneSecond) {
            System.out.printf("unexpected that elapsed time > 1s %f", (double)elapsed / (double)oneSecond);
            return false;
        }
        return true;
    }

    private static boolean testTimingConstructorYawl()
    {
        String dictFileName = "./dictionary-yawl.txt";

        String[] dictionary = readDictionary(dictFileName);

        long start = java.lang.System.nanoTime();

        BoggleSolver solver = new BoggleSolver(dictionary);

        long end = java.lang.System.nanoTime();
        long elapsed = end - start;
        long oneSecond = (long)Math.pow(10, 9);

        double inSeconds = (double)elapsed / (double)oneSecond;
        System.out.println(inSeconds);

        double expected = 0.07;
        if (inSeconds > expected) {
            System.out.printf("Unexpectedly long %f, when limit is %f\n", inSeconds, expected);
            return false;
        }

        return true;
    }

    private static String[] readDictionary(String fileName) {
        In in = new In(fileName);
        return in.readAllStrings();
    }
}
