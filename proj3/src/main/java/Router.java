import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest,
     * where the longs are node IDs.
     */
    public static LinkedList<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                                double destlon, double destlat) {
        return shortestPathHelper(g, stlon, stlat, destlon, destlat);
    }

    public static LinkedList<Long> shortestPathHelper(GraphDB g, double stlon, double stlat,
                                               double destlon, double destlat) {
        long sid = g.closest(stlon, stlat);
        long did = g.closest(destlon, destlat);
        GraphNode sNode = g.getNode(sid);
        GraphNode dNode = g.getNode(did);

        PriorityQueue<RouterNode> minHeap = new PriorityQueue<>();
        HashMap<Long, RouterNode> visited = new HashMap<>();

        RouterNode init = new RouterNode(sNode, 0, g.distance(sid, did), null);
        visited.put(sid, init);
        minHeap.add(init);

        while (!minHeap.peek().node.equals(dNode)) {
            RouterNode min = minHeap.poll();
            long minId = min.node.id;
            visited.remove(minId);
            for (Long x: g.adjacent(minId)) {
                if (!visited.containsKey(x)) {
                    GraphNode temp = g.getNode(x);
                    RouterNode adj = new RouterNode(temp, min.step + g.distance(minId, x),
                            g.distance(x, did), min);
                    minHeap.add(adj);
                    visited.put(x, adj);
                } else {
                    RouterNode temp = visited.get(x);
                    double currentDist = temp.step;
                    double newDist = min.step + g.distance(minId, x);

                    if (newDist < currentDist) {
                        minHeap.remove(temp);
                        RouterNode adj = new RouterNode(temp.node, newDist,
                                g.distance(x, did), min);
                        minHeap.add(adj);
                        visited.put(x, adj);

                    }
                }
            }
        }
        LinkedList<Long> result = new LinkedList<>();

        RouterNode dest = minHeap.poll();
        while (dest != null) {
            result.addFirst(dest.node.id);
            dest = dest.prev;
        }
        return result;
    }

    static class RouterNode implements Comparable {
        GraphNode node;
        double step;
        double est;
        RouterNode prev;

        RouterNode(GraphNode node, double step, double est, RouterNode prev) {
            this.node = node;
            this.step = step;
            this.est = est;
            this.prev = prev;
        }

        @Override
        public int compareTo(Object o) {
            if (this == o) {
                return 0;
            }
            if (o == null || this.getClass() != o.getClass()) {
                throw new RuntimeException();
            }
            RouterNode temp = (RouterNode) o;
            if (this.step + this.est - temp.step - temp.est > 0) {
                return 1;
            } else if (this.step + this.est - temp.step - temp.est == 0) {
                return 0;
            } else {
                return  -1;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }

            RouterNode temp = (RouterNode) obj;

            return temp.node.equals(this.node);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
