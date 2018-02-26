import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    HashMap<Long, GraphNode> graphMap = new HashMap<>();
    HashMap<String, LinkedList<Location>> locationMap = new HashMap<>();
    HashMap<String, Way> wayMap = new HashMap<>();
    HashMap<String, LinkedList<String>> cleanStringMap = new HashMap<>();
    PrefixTree trie = new PrefixTree();
    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }



    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Long[] key = graphMap.keySet().toArray(new Long[1]);
        for (Long x: key) {
            GraphNode temp = graphMap.get(x);
            if (!temp.connected()) {
                graphMap.remove(x);
            }
        }
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        Iterable<Long> iter = new Iterable<Long>() {
            @Override
            public Iterator<Long> iterator() {
                return graphMap.keySet().iterator();
            }
        };
        return iter;
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        Iterable<Long> iter = new Iterable<Long>() {
            @Override
            public Iterator<Long> iterator() {
                return graphMap.get(v).adjacencyList.iterator();
            }
        };
        return iter;
    }

    /** Returns the distance in units of longitude between vertices v and w. */
    double distance(long v, long w) {
        GraphNode node1 = graphMap.get(v);
        GraphNode node2 = graphMap.get(w);
        return Math.sqrt(Math.pow(node1.lat - node2.lat, 2) + Math.pow(node1.lon - node2.lon, 2));
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        Long[] key = graphMap.keySet().toArray(new Long[1]);
        double min = distance(lon, lat, key[0]);
        long id = key[0];
        for (Long x: key) {
            double dis = distance(lon, lat, x);
            if (distance(lon, lat, x) < min) {
                min = dis;
                id = x;
            }
        }
        return id;
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        return graphMap.get(v).lon;
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        return graphMap.get(v).lat;
    }

    GraphNode getNode(long id) {
        return graphMap.get(id);
    }

    void put(long id, GraphNode node) {
        graphMap.put(id, node);
    }

    void putLocation(String name, Location loc) {
        if (locationMap.containsKey(name)) {
            LinkedList<Location> locs = locationMap.get(name);
            if (!locs.contains(loc)) {
                locs.add(loc);
                return;
            }
            return;
        } else {
            LinkedList<Location> locs = new LinkedList<>();
            locs.add(loc);
            locationMap.put(name, locs);
        }
    }

    void putWay(String name, Way way) {
        wayMap.put(name, way);
    }

    void putCleanString(String clean, String origin) {
        if (cleanStringMap.containsKey(clean)) {
            LinkedList<String> list = cleanStringMap.get(clean);
            if (list.size() != 0) {
                if (!list.contains(origin)) {
                    list.add(origin);
                    return;
                }
                return;
            }
            list.add(origin);
        } else {
            LinkedList<String> list = new LinkedList<>();
            list.add(origin);
            cleanStringMap.put(clean, list);
        }
    }

    double distance(double lon, double lat, long v) {
        GraphNode node = graphMap.get(v);
        return Math.sqrt(Math.pow(lon - node.lon, 2) + Math.pow(lat - node.lat, 2));
    }

    public LinkedList<String> findLocation(String prefix) {
        String[] temp = trie.findLocation(prefix).toArray(new String[1]);
        LinkedList<String> loc = new LinkedList<>();
        for (String s: temp) {
            loc.addAll(cleanStringMap.get(s));
        }
        return loc;
    }

    List<Map<String, Object>> getLocations(String name) {
        String cleanString = cleanString(name);
        LinkedList<String> list = cleanStringMap.get(cleanString);
        List<Map<String, Object>> result = new LinkedList<>();
        for (String s: list) {
            LinkedList<Location> locs = locationMap.get(s);
            for (Location loc: locs) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("name", loc.name);
                temp.put("lat", loc.lat);
                temp.put("lon", loc.lon);
                temp.put("id", loc.id);
                result.add(temp);
            }
        }
        return result;
    }

    public void prefixTreeInsert(String s) {
        trie.insertString(s);
    }

    public static void main(String[] args) {
        GraphDB db = new GraphDB("berkeley.osm");
        System.out.println(db);
    }
}
