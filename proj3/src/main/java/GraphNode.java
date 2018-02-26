import java.util.LinkedList;

/**
 * Created by 62339 on 2017/4/14.
 */
class GraphNode {
    long id;
    double lon;
    double lat;
    LinkedList<Long> adjacencyList = new LinkedList<>();
    String name;


    GraphNode(long id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    void addAdjNode(long adjId) {
        adjacencyList.add(adjId);
    }

    boolean connected() {
        return adjacencyList.size() != 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        GraphNode temp = (GraphNode) obj;
        return this.id == temp.id && this.lat == temp.lat && this.lon == temp.lon;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
