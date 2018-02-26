import java.util.LinkedList;

/**
 * Created by 62339 on 2017/4/14.
 */
class Way {
    String name;
    LinkedList<GraphNode> way;
    boolean valid;
    String maxSpeed;

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    Way() {
        way = new LinkedList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void addNode(GraphNode node) {
        way.add(node);
    }

    public boolean isValid() {
        return valid;
    }

    void connect() {
        GraphNode[] temp = new GraphNode[1];
        temp[0] = new GraphNode(1, 2, 3);
        GraphNode[] nodes = way.toArray(temp);
        for (int i = 0; i < nodes.length; i += 1) {
            try {
                if (!nodes[i].equals(nodes[i + 1])) {
                    nodes[i].addAdjNode(nodes[i + 1].id);
                }
            } catch (IndexOutOfBoundsException e) {

            }
            try {
                if (!nodes[i].equals(nodes[i - 1])) {
                    nodes[i].addAdjNode(nodes[i - 1].id);
                }
            } catch (IndexOutOfBoundsException e) {

            }
        }
    }
}
