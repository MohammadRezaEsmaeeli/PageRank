import java.util.*;
import java.util.stream.Stream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PageRank {

    // Config
    static String FILE_PATH = "web-graph.txt";
    static int NUMBER_OF_MOST_CENTRAL_NODES = 10;
    static int ITERATIONS_OF_PAGE_RANK = 6;
    // ------

    public static double pagerank[];
    public static ArrayList<Integer>[] adj;

    public static void readLine_saveGraph() {
        Path path = Paths.get(FILE_PATH);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(s -> {
                String[] temp = s.split(" ");
                int src = Integer.parseInt(temp[0]);
                int des = Integer.parseInt(temp[1]);
                if (adj[src] == null) {
                    adj[src] = new ArrayList<Integer>();
                }
                if (adj[des] == null) {
                    adj[des] = new ArrayList<Integer>();
                }
                adj[src].add(des);
            });
        } catch (IOException ex) {
            System.out.println();
        } catch (OutOfMemoryError e) {
            System.err.println("Max JVM memory: " + Runtime.getRuntime().maxMemory());
        }
    }

    public static void walk(double totalNodes) {
        double InitialPageRank;
        double OutgoingLinks = 0;
        double DampingFactor = 0.85;
        double TempPageRank[] = new double[1000000];
        int ExternalNodeNumber;
        int InternalNodeNumber;
        int k = 1; // For Traversing
        int ITERATION_STEP = 1;
        InitialPageRank = 1 / totalNodes;
        for (k = 1; k <= totalNodes; k++) {
            pagerank[k] = InitialPageRank;
        }
        System.out.printf("\nInitial PageRank Values, 0th Step:\n");
        printResult();
        while (ITERATION_STEP <= ITERATIONS_OF_PAGE_RANK) {
            // Store the PageRank for All Nodes in Temporary Array
            for (k = 1; k <= totalNodes; k++) {
                TempPageRank[k] = pagerank[k];
                pagerank[k] = 0;
            }
            for (InternalNodeNumber = 1; InternalNodeNumber <= totalNodes; InternalNodeNumber++) {
                for (ExternalNodeNumber = 1; ExternalNodeNumber <= totalNodes; ExternalNodeNumber++) {
                    if (adj[ExternalNodeNumber] != null && adj[ExternalNodeNumber].contains(InternalNodeNumber)) {
                        k = 1;
                        OutgoingLinks = 0; // Count the Number of Outgoing Links for each ExternalNodeNumber
                        while (k <= totalNodes) {
                            if (adj[ExternalNodeNumber].contains(k)) {
                                OutgoingLinks = OutgoingLinks + 1; // Counter for Outgoing Links
                            }
                            k = k + 1;
                        }
                        // Calculate PageRank
                        pagerank[InternalNodeNumber] += TempPageRank[ExternalNodeNumber] * (1 / OutgoingLinks);
                    }
                }
            }
            System.out.printf("\nAfter " + ITERATION_STEP + "th Step:\n");
            printResult();
            ITERATION_STEP = ITERATION_STEP + 1;
        }
        // Add the Damping Factor to PageRank
        for (k = 1; k <= totalNodes; k++) {
            pagerank[k] = (1 - DampingFactor) + DampingFactor * pagerank[k];
        }
        // Display PageRank
        System.out.println("\nFinal PageRank:");
        printResult();
    }

    private static HashMap<Integer, Double> sortByValue(HashMap<Integer, Double> hm) {
        List<Map.Entry<Integer, Double> > list =
            new LinkedList<Map.Entry<Integer, Double> >(hm.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double> >() {
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private static void printResult() {
        HashMap<Integer,Double> hm = new HashMap<Integer,Double>();
        for (int i = 0; i < pagerank.length; i++) {
            hm.put(i,pagerank[i]);
        }
        hm = sortByValue(hm);
        int index = 0;
        for(Map.Entry<Integer, Double> entry : hm.entrySet()) {
            int key = entry.getKey();
            double value = entry.getValue();
            System.out.println("Node " + key + " => Score: " + value);
            index++;
            if (index >= NUMBER_OF_MOST_CENTRAL_NODES) {
                break;
            }
        }
    }

    public static void main(String args[]) {
        adj = new ArrayList[1000000];
        readLine_saveGraph();
        int numberOfNodes = 0;
        for (int i = 0; i < adj.length; i++) {
            if (adj[i] != null) {
                numberOfNodes ++;
            }
        }
        pagerank = new double[numberOfNodes + 1];
        walk(numberOfNodes);
    }

}
