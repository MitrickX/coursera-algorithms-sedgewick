/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class BaseballElimination {

    /**
     * Number of teams
     */
    private int N;

    /**
     * Names of teams => ID of team
     */
    private HashMap<String, Integer> nameToID;

    /**
     * Names of teams
     */
    private String[] names;

    /**
     * Wins vector
     */
    private int[] wins;

    /**
     * Loss vector
     */
    private int[] loss;

    /**
     * Left games vector
     */
    private int[] left;

    /**
     * Games left to play between teams
     */
    private int[][] games;

    /**
     * Vertexes of flow graph [i][j] == ID of vertex
     */
    private int[][] vertexes;

    /**
     * Flow network
     */
    private FlowNetwork network;

    /**
     * Last ff run for current team?
     */
    private String ffRun;

    /**
     * FF algorithm instance
     */
    private FordFulkerson ff;

    private boolean travialEliminated;
    private int travialEliminatedBy;

    public BaseballElimination(String filename) {
        if (filename == null || filename.length() <= 0) {
            throw new IllegalArgumentException();
        }
        In in = new In(filename);

        N = in.readInt();
        if (N <= 0) {
            throw new IllegalArgumentException();
        }

        nameToID = new HashMap<String, Integer>();
        names = new String[N];
        wins = new int[N];
        loss = new int[N];
        left = new int[N];
        games = new int[N][N];

        for (int i = 0; i < N; i++) {
            String name = in.readString();
            nameToID.put(name, i);
            names[i] = name;
            wins[i] = in.readInt();
            loss[i] = in.readInt();
            left[i] = in.readInt();
            for (int j = 0; j < N; j++) {
                games[i][j] = in.readInt();
            }
        }
    }

    public int numberOfTeams() {
        return N;
    }

    public Iterable<String> teams()
    {
        return nameToID.keySet();
    }

    public int wins(String name) {
        return wins[getIndexByName(name)];
    }

    public int losses(String name) {
        return loss[getIndexByName(name)];
    }

    public int remaining(String name) {
        return left[getIndexByName(name)];
    }

    public int against(String name1, String name2) {
        return games[getIndexByName(name1)][getIndexByName(name2)];
    }

    public boolean isEliminated(String team) {
        ff(team);

        if (travialEliminated) {
            return true;
        }

        boolean allFull = true;
        for (FlowEdge e : network.adj(0)) {
            if (e.flow() < e.capacity()) {
                allFull = false;
                break;
            }
        }

        return !allFull;
    }

    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team)) {
            return null;
        }

        ArrayList<String> R = new ArrayList<String>();

        if (travialEliminated) {
            R.add(names[travialEliminatedBy]);
            return R;
        }

        int x = getIndexByName(team);
        for (int i = 0; i < N; i++) {
            if (i != x && ff.inCut(vertexes[i][i])) {
                R.add(names[i]);
            }
        }
        return R;
    }

    private void ff(String team) {
        if (team == null) {
            throw new IllegalArgumentException();
        }
        if (ffRun != null && ffRun.equals(team)) {
            return;
        }

        travialEliminated = false;

        int x = getIndexByName(team);
        buildFlowNetwork(x);

        if (!travialEliminated) {
            ff = new FordFulkerson(network, 0, network.V() - 1);
        }

        ffRun = team;
    }

    private void buildFlowNetwork(int x) {
        int signleV = N - 1;
        int pairsV = signleV * (signleV - 1) / 2;

        // total vertexes = pairsV vertexes for paris + signleV signle vertexes + source and target
        int V = pairsV + signleV + 2;

        // s - is 0
        // t - is V - 1
        int s = 0;
        int t = V - 1;

        network = new FlowNetwork(V);

        if (vertexes == null) {
            vertexes = new int[N][N];
        }

        for (int team1 = 0; team1 < N; team1++) {
            for (int team2 = 0; team2 < N; team2++) {
                vertexes[team1][team2] = 0;
            }
        }

        int vID = 1;

        // enumerate single vertexes and connect each single vertex with target vertex
        for (int team = 0; team < N; team++) {
            if (team != x) {
                vertexes[team][team] = vID;
                int c = wins[x] + left[x] - wins[team];
                if (c < 0) {
                    travialEliminated = true;
                    travialEliminatedBy = team;
                    return;
                }
                network.addEdge(new FlowEdge(vID, t, c));

                vID++;
            }
        }

        // enumerate pair vertexes and connect source with each pair vertex and connect pairs with team
        for (int team1 = 0; team1 < N; team1++) {
            for (int team2 = team1; team2 < N; team2++) {
                if (team1 == team2 || team1 == x || team2 == x) {
                    continue;
                }

                vertexes[team1][team2] = vID;
                vertexes[team2][team1] = vID;

                int g = games[team1][team2];
                network.addEdge(new FlowEdge(s, vID, g));

                int t1VID = vertexes[team1][team1];
                int t2VID = vertexes[team2][team2];
                network.addEdge(new FlowEdge(vID, t1VID, Double.POSITIVE_INFINITY));
                network.addEdge(new FlowEdge(vID, t2VID, Double.POSITIVE_INFINITY));

                vID++;
            }
        }
    }

    private int getIndexByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!nameToID.containsKey(name)) {
            throw new IllegalArgumentException();
        }
        return nameToID.get(name);
    }

    private void printDebugInfo()
    {
        int i = 0;
        String[] teamNames = new String[N];
        for (String name : nameToID.keySet()) {
            teamNames[i++] = name;
        }

        System.out.printf("N = %d\n", N);
        System.out.printf("Names = %s\n", Arrays.toString(teamNames));
        System.out.printf("Wins = %s\n", Arrays.toString(wins));
        System.out.printf("Loss = %s\n", Arrays.toString(loss));
        System.out.printf("Left = %s\n", Arrays.toString(left));
        System.out.printf("Games:\n%s\n", matrixToString(games));
    }

    private void printFlowNetwork()
    {
        for (int v = 0; v < network.V(); v++) {
            for (FlowEdge edge : network.adj(v)) {
                String from = getVertexName(edge.from());
                String to = getVertexName(edge.to());
                System.out.printf("%s -> %s (%f)\n", from, to, edge.capacity());
            }
        }
    }

    private String flowNetworkToString(boolean withEdgeFlow)
    {
        ArrayList<String> lines = new ArrayList<>();
        for (int v = 0; v < network.V(); v++) {
            for (FlowEdge edge : network.adj(v)) {
                if (edge.to() == v) {
                    continue;
                }
                String from = getVertexName(edge.from());
                String to = getVertexName(edge.to());
                String cap = "∞";
                if (edge.capacity() != Double.POSITIVE_INFINITY) {
                    cap = String.format("%d", (int)edge.capacity());
                }
                if (!withEdgeFlow) {
                    lines.add(String.format("%s -> %s (%s)", from, to, cap));
                } else {
                    int flow = (int)edge.flow();
                    lines.add(String.format("%s -> %s (%d/%s)", from, to, flow, cap));
                }
            }
        }
        Collections.sort(lines);
        return String.join("\n", lines);
    }

    private String minCutToString()
    {
        ArrayList<String> lines = new ArrayList<>();
        for (int v = 0; v < network.V(); v++) {
            if (ff.inCut(v)) {
                lines.add(getVertexName(v));
            }
        }
        Collections.sort(lines);
        return String.join("\n", lines);
    }

    private String getVertexName(int v) {
        if (v == 0) {
            return "s";
        } else if (v == network.V() - 1) {
            return "t";
        }

        for (int team1 = 0; team1 < N; team1++) {
            for (int team2 = 0; team2 < N; team2++) {
                if (vertexes[team1][team2] != v) {
                    continue;
                }

                if (team1 == team2) {
                    return String.format("%d", team1);
                } else if (team1 < team2) {
                    return String.format("%d:%d", team1, team2);
                } else {
                    return String.format("%d:%d", team2, team1);
                }
            }
        }

        return "unknown";
    }

    public static void main(String[] args) {
        if (args.length <= 0) {
            if (runTests()) {
                System.out.println("Tests are OK!");
            }
        }
    }

    private static boolean runTests()
    {
        return runTeam5Getters() &&
                runTeam5NeworkBuilding() &&
                    runTeam5IsEliminated() &&
                        runTeam5CertificateOfElimination() &&
                            runTeam4IsEliminated() &&
                                runTeam4CertificateOfElimination();
    }

    private static boolean runDebugTest()
    {
        String fileName = "./teams5.txt";
        BaseballElimination division = new BaseballElimination(fileName);
        division.printDebugInfo();
        return true;
    }

    private static boolean runTeam5Getters()
    {
        String fileName = "./teams5.txt";
        BaseballElimination division = new BaseballElimination(fileName);

        int N = 5;
        return runTeam5GettersCheckN(division, N) &&
                runTeam5GettersCheckTeams(division, N) &&
                    runTeam5GettersCheckWins(division) &&
                        runTeam5GettersCheckLosses(division) &&
                            runTeam5GettersCheckRemaining(division) &&
                                runTeam5GettersCheckAgains(division);
    }

    private static boolean runTeam5GettersCheckN(BaseballElimination division, int N)
    {
        String testName = "runTeam5GettersCheckN";
        if (division.numberOfTeams() != N) {
            System.out.printf("%s FAIL: unexpected number of teams %d instread of %d", testName,
                              division.numberOfTeams(), N);
            return false;
        }
        return true;
    }

    private static boolean runTeam5GettersCheckTeams(BaseballElimination division, int N)
    {
        String testName = "runTeam5GettersCheckTeams";

        String[] expectedTeams = new String[]{ "Baltimore", "Boston", "Detroit", "New_York", "Toronto" };

        // read teams
        int i = 0;
        String[] teams = new String[N];
        for (String team : division.teams()) {
            teams[i++] = team;
        }

        Arrays.sort(teams);

        if (!Arrays.equals(expectedTeams, teams)) {
            System.out.printf("%s FAIL: unexpected teams list %s instread of %s", testName,
                              Arrays.toString(teams),
                              Arrays.toString(expectedTeams));
            return false;
        }

        return true;
    }

    private static boolean runTeam5GettersCheckWins(BaseballElimination division)
    {
        String testName = "runTeam5GettersCheckWinds";

        HashMap<String, Integer> expected = new HashMap<>();
        expected.put("New_York", 75);
        expected.put("Baltimore", 71);
        expected.put("Boston", 69);
        expected.put("Toronto", 63);
        expected.put("Detroit", 49);

        for (String team : division.teams()) {
            int w = division.wins(team);
            int e = expected.get(team);
            if (w != e) {
                System.out.printf("%s FAIL: unexpected number of wins %d for team %s instread of %d", testName,
                                  w, team, e);
                return false;
            }
        }

        return true;
    }

    private static boolean runTeam5GettersCheckLosses(BaseballElimination division)
    {
        String testName = "runTeam5GettersCheckLosses";

        HashMap<String, Integer> expected = new HashMap<>();
        expected.put("New_York", 59);
        expected.put("Baltimore", 63);
        expected.put("Boston", 66);
        expected.put("Toronto", 72);
        expected.put("Detroit", 86);

        for (String team : division.teams()) {
            int l = division.losses(team);
            int e = expected.get(team);
            if (l != e) {
                System.out.printf("%s FAIL: unexpected number of losses %d for team %s instread of %d", testName,
                                  l, team, e);
                return false;
            }
        }

        return true;
    }

    private static boolean runTeam5GettersCheckRemaining(BaseballElimination division)
    {
        String testName = "runTeam5GettersCheckRemaining";

        HashMap<String, Integer> expected = new HashMap<>();
        expected.put("New_York", 28);
        expected.put("Baltimore", 28);
        expected.put("Boston", 27);
        expected.put("Toronto", 27);
        expected.put("Detroit", 27);

        for (String team : division.teams()) {
            int r = division.remaining(team);
            int e = expected.get(team);
            if (r != e) {
                System.out.printf("%s FAIL: unexpected number of left (remainings) %d for team %s instread of %d", testName,
                                  r, team, e);
                return false;
            }
        }

        return true;
    }

    private static boolean runTeam5GettersCheckAgains(BaseballElimination division)
    {
        String testName = "runTeam5GettersCheckRemaining";

        HashMap<String, Integer> expected = new HashMap<>();
        expected.put("New_York:New_York", 0);
        expected.put("New_York:Baltimore", 3);
        expected.put("New_York:Boston", 8);
        expected.put("New_York:Toronto", 7);
        expected.put("New_York:Detroit", 3);

        expected.put("Baltimore:New_York", 3);
        expected.put("Baltimore:Baltimore", 0);
        expected.put("Baltimore:Boston", 2);
        expected.put("Baltimore:Toronto", 7);
        expected.put("Baltimore:Detroit", 7);

        expected.put("Boston:New_York", 8);
        expected.put("Boston:Baltimore", 2);
        expected.put("Boston:Boston", 0);
        expected.put("Boston:Toronto", 0);
        expected.put("Boston:Detroit", 3);

        expected.put("Toronto:New_York", 7);
        expected.put("Toronto:Baltimore", 7);
        expected.put("Toronto:Boston", 0);
        expected.put("Toronto:Toronto", 0);
        expected.put("Toronto:Detroit", 3);

        expected.put("Detroit:New_York", 3);
        expected.put("Detroit:Baltimore", 7);
        expected.put("Detroit:Boston", 3);
        expected.put("Detroit:Toronto", 3);
        expected.put("Detroit:Detroit", 0);

        for (String team1 : division.teams()) {
            for (String team2 : division.teams()) {
                String pair = team1 + ":" + team2;
                int a = division.against(team1, team2);
                int e = expected.get(pair);
                if (a != e) {
                    System.out.printf("%s FAIL: unexpected number of againts %d for pair %s instread of %d", testName,
                                      a, pair, e);
                    return false;
                }
            }

        }

        return true;
    }

    private static boolean runTeam5NeworkBuilding()
    {
        String testName = "runTeam5NeworkBuilding";

        String fileName = "./teams5.txt";
        BaseballElimination division = new BaseballElimination(fileName);
        division.buildFlowNetwork(4);
        String flowNetworkString = division.flowNetworkToString(false);

        String[] expectedFlowNetworkStringParts = new String[]{
                "s -> 0:1 (3)",
                "s -> 0:2 (8)",
                "s -> 0:3 (7)",
                "s -> 1:2 (2)",
                "s -> 1:3 (7)",
                "s -> 2:3 (0)",
                "0:1 -> 0 (∞)",
                "0:1 -> 1 (∞)",
                "0:2 -> 0 (∞)",
                "0:2 -> 2 (∞)",
                "0:3 -> 0 (∞)",
                "0:3 -> 3 (∞)",
                "1:2 -> 1 (∞)",
                "1:2 -> 2 (∞)",
                "1:3 -> 1 (∞)",
                "1:3 -> 3 (∞)",
                "2:3 -> 2 (∞)",
                "2:3 -> 3 (∞)",
                "0 -> t (1)",
                "1 -> t (5)",
                "2 -> t (7)",
                "3 -> t (13)"
        };

        Arrays.sort(expectedFlowNetworkStringParts);
        String expectedFlowNetworkString = String.join("\n", expectedFlowNetworkStringParts);

        if (!flowNetworkString.equals(expectedFlowNetworkString)) {
            System.out.printf("%s FAIL: unexpected flow graph\n%s\ninstread of\n%s\n",
                              testName, flowNetworkString, expectedFlowNetworkString);
            return false;
        }
        return true;
    }

    private static boolean runTeam5IsEliminated()
    {
        return runTeam5IsNew_YorkEliminated() &&
                runTeam5IsBaltimoreEliminated() &&
                    runTeam5IsBostonEliminated() &&
                        runTeam5IsTorontoEliminated() &&
                            runTeam5IsDetroitEliminated();
    }

    private static boolean runTeam5CertificateOfElimination()
    {
        return runTeam5CertificateOfNew_YorkElimination() &&
                runTeam5CertificateOfBaltimoreElimination() &&
                    runTeam5CertificateOfBostonElimination() &&
                        runTeam5CertificateOfTorontoElimination() &&
                            runTeam5CertificateOfDetroitElimination();
    }

    private static boolean runTeam5IsDetroitEliminated()
    {
        String testName = "runTeam5IsDetroitEliminated";

        String fileName = "./teams5.txt";
        BaseballElimination division = new BaseballElimination(fileName);
        if (!division.isEliminated("Detroit")) {
            System.out.printf("%s FAIL: unexpected FALSE from isEliminated(\"Detroit\")", testName);
            return false;
        }

        return true;
    }

    private static boolean runTeam5CertificateOfDetroitElimination()
    {
        String testName = "runTeam5CertificateOfDetroitElimination";

        String fileName = "./teams5.txt";
        BaseballElimination division = new BaseballElimination(fileName);

        String[] expected = new String[] {
            "New_York", "Baltimore", "Boston", "Toronto"
        };

        ArrayList<String> RList = new ArrayList<>();
        Iterable<String> R = division.certificateOfElimination("Detroit");
        for (String team : R) {
            RList.add(team);
        }

        String[] RArray = new String[RList.size()];

        int i = 0;
        for (String team : RList) {
            RArray[i++] = team;
        }

        if (!Arrays.equals(expected, RArray)) {
            System.out.printf("%s FAIL: unexpected R be %s for \"Detroit\" instread of %s", testName,
                              Arrays.toString(RArray),
                              Arrays.toString(expected));
            return false;
        }

        /*
        System.out.println("MIN CUT:");
        System.out.println(division.minCutToString());

        System.out.println("FLOW GRAPH:");
        System.out.println(division.flowNetworkToString(true));*/

        return true;
    }

    private static boolean runTeam5IsNew_YorkEliminated()
    {
        return runTeam5IsEliminatedExpectIsNot("New_York");
    }

    private static boolean runTeam5CertificateOfNew_YorkElimination()
    {
        return runTeam5CertificateOfEliminationExpectNull("New_York");
    }

    private static boolean runTeam5IsBaltimoreEliminated()
    {
        return runTeam5IsEliminatedExpectIsNot("Baltimore");
    }

    private static boolean runTeam5CertificateOfBaltimoreElimination()
    {
        return runTeam5CertificateOfEliminationExpectNull("Baltimore");
    }

    private static boolean runTeam5IsBostonEliminated()
    {
        return runTeam5IsEliminatedExpectIsNot("Boston");
    }

    private static boolean runTeam5CertificateOfBostonElimination()
    {
        return runTeam5CertificateOfEliminationExpectNull("Boston");
    }

    private static boolean runTeam5IsTorontoEliminated()
    {
        return runTeam5IsEliminatedExpectIsNot("Toronto");
    }

    private static boolean runTeam5CertificateOfTorontoElimination()
    {
        return runTeam5CertificateOfEliminationExpectNull("Toronto");
    }

    private static boolean runTeam5IsEliminatedExpectIsNot(String teamName)
    {
        String testName = "runTeam5IsEliminatedExpectIsNot";

        String fileName = "./teams5.txt";
        BaseballElimination division = new BaseballElimination(fileName);
        if (division.isEliminated(teamName)) {
            System.out.printf("%s FAIL: unexpected TRUE from isEliminated(\"%s\")", testName, teamName);
            return false;
        }
        return true;
    }

    private static boolean runTeam5CertificateOfEliminationExpectNull(String teamName)
    {
        String testName = "runTeam5CertificateOfEliminationExpectNull";
        String fileName = "./teams5.txt";
        BaseballElimination division = new BaseballElimination(fileName);
        if (division.certificateOfElimination(teamName) != null) {
            System.out.printf("%s FAIL: unexpected NOT NULL R from certificateOfElimination(\"%s\")", testName, teamName);
            return false;
        }
        return true;
    }

    private static boolean runTeam4IsEliminated()
    {
        return runTeam4IsAtlantaEliminated() &&
                runTeam4IsPhiladelphiaEliminated() &&
                runTeam4IsNew_YorkEliminated() &&
                runTeam4IsMontrealEliminated();
    }

    private static boolean runTeam4CertificateOfElimination()
    {
        return runTeam4CertificateOfAtlantaElimination() &&
                runTeam4CertificateOfPhiladelphiaElimination() &&
                runTeam4CertificateOfNew_YorkElimination() &&
                runTeam4CertificateOfMontrealElimination();
    }

    private static boolean runTeam4IsAtlantaEliminated()
    {
        return runTeam4IsEliminatedExpectIsNot("Atlanta");
    }

    private static boolean runTeam4IsPhiladelphiaEliminated()
    {
        return runTeam4IsEliminatedExpectIsYes("Philadelphia");
    }

    private static boolean runTeam4IsNew_YorkEliminated()
    {
        return runTeam4IsEliminatedExpectIsNot("New_York");
    }

    private static boolean runTeam4IsMontrealEliminated()
    {
        return runTeam4IsEliminatedExpectIsYes("Montreal");
    }

    private static boolean runTeam4IsEliminatedExpectIsNot(String teamName)
    {
        String testName = "runTeam4IsEliminatedExpectIsNot";

        String fileName = "./teams4.txt";
        BaseballElimination division = new BaseballElimination(fileName);
        if (division.isEliminated(teamName)) {
            System.out.printf("%s FAIL: unexpected TRUE from isEliminated(\"%s\")", testName, teamName);
            return false;
        }
        return true;
    }

    private static boolean runTeam4IsEliminatedExpectIsYes(String teamName)
    {
        String testName = "runTeam4IsEliminatedExpectIsYes";

        String fileName = "./teams4.txt";
        BaseballElimination division = new BaseballElimination(fileName);

        if (!division.isEliminated(teamName)) {
            System.out.printf("%s FAIL: unexpected FALSE from isEliminated(\"%s\")", testName, teamName);
            return false;
        }
        return true;
    }

    private static boolean runTeam4CertificateOfAtlantaElimination()
    {
        return runTeam4CertificateOfEliminationExpectNull("Atlanta");
    }

    private static boolean runTeam4CertificateOfPhiladelphiaElimination()
    {
        return runTeam4CertificateOfEliminationExpectNotNull("Philadelphia", new String[]{ "Atlanta", "New_York" });
    }

    private static boolean runTeam4CertificateOfNew_YorkElimination()
    {
        return runTeam4CertificateOfEliminationExpectNull("New_York");
    }

    private static boolean runTeam4CertificateOfMontrealElimination()
    {
        return runTeam4CertificateOfEliminationExpectNotNull("Montreal", new String[]{ "Atlanta" });
    }

    private static boolean runTeam4CertificateOfEliminationExpectNull(String teamName)
    {
        String testName = "runTeam4CertificateOfEliminationExpectNull";
        String fileName = "./teams4.txt";
        BaseballElimination division = new BaseballElimination(fileName);
        if (division.certificateOfElimination(teamName) != null) {
            System.out.printf("%s FAIL: unexpected NOT NULL R from certificateOfElimination(\"%s\")", testName, teamName);
            return false;
        }
        return true;
    }

    private static boolean runTeam4CertificateOfEliminationExpectNotNull(String teamName, String[] expected)
    {
        String testName = "runTeam4CertificateOfEliminationExpectNotNull";
        String fileName = "./teams4.txt";
        BaseballElimination division = new BaseballElimination(fileName);

        ArrayList<String> RList = new ArrayList<>();
        Iterable<String> R = division.certificateOfElimination(teamName);
        for (String team : R) {
            RList.add(team);
        }

        String[] RArray = new String[RList.size()];

        int i = 0;
        for (String team : RList) {
            RArray[i++] = team;
        }

        if (!Arrays.equals(expected, RArray)) {
            System.out.printf("%s FAIL: unexpected R be %s for \"%s\" instread of %s", testName,
                              teamName,
                              Arrays.toString(RArray),
                              Arrays.toString(expected));
            return false;
        }

        return true;
    }

    private static String matrixToString(int[][] matrix) {
        String[] lines = new String[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            lines[i] = Arrays.toString(matrix[i]);
        }
        return String.join("\n", lines);
    }
}
