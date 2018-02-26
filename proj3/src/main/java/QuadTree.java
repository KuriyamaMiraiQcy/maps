import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Created by 62339 on 2017/4/12.
 */
public class QuadTree {
    private Node root;

    public QuadTree(double ullon, double ullat, double lrlon, double lrlat) {
        root = new Node(ullon, ullat, lrlon, lrlat, null, "root");

        buildNLevelQuadTree(7, root);
    }

    public Node getRoot() {
        return root;
    }

    public void searchHelper(Node rootNode, double ullon, double ullat, double lrlon, double lrlat,
                             int level, PriorityQueue<Node> img, HashSet<Double> lon) {
        if (checkIntersect(rootNode, ullon, ullat, lrlon, lrlat)) {
            if (level == 0) {
                img.add(rootNode);
                lon.add(rootNode.ullon);
                return;
            }
            searchHelper(rootNode.nw, ullon, ullat, lrlon, lrlat, level - 1, img, lon);
            searchHelper(rootNode.ne, ullon, ullat, lrlon, lrlat, level - 1, img, lon);
            searchHelper(rootNode.sw, ullon, ullat, lrlon, lrlat, level - 1, img, lon);
            searchHelper(rootNode.se, ullon, ullat, lrlon, lrlat, level - 1, img, lon);
        }
        return;
    }

    private boolean checkIntersect(Node rootNode, double ullon,
                                   double ullat, double lrlon, double lrlat) {
        return !(rootNode.ullon > lrlon || rootNode.ullat < lrlat
                || rootNode.lrlat > ullat || rootNode.lrlon < ullon);
    }

    private void buildNLevelQuadTree(int level, Node rootNode) {
        if (level == 0) {
            return;
        }

        String nwImageName;
        String neImageName;
        String swImageName;
        String seImageName;

        if (rootNode.parent == null) {
            nwImageName = "1";
            neImageName = "2";
            swImageName = "3";
            seImageName = "4";
        } else {
            nwImageName = rootNode.imageName + "1";
            neImageName = rootNode.imageName + "2";
            swImageName = rootNode.imageName + "3";
            seImageName = rootNode.imageName + "4";
        }

        double halfLon = (rootNode.lrlon + rootNode.ullon) / 2;
        double halfLat = (rootNode.lrlat + rootNode.ullat) / 2;

        rootNode.nw = new Node(rootNode.ullon, rootNode.ullat,
                halfLon, halfLat, rootNode, nwImageName);
        rootNode.ne = new Node(halfLon, rootNode.ullat,
                rootNode.lrlon, halfLat, rootNode, neImageName);
        rootNode.sw = new Node(rootNode.ullon, halfLat, halfLon,
                rootNode.lrlat, rootNode, swImageName);
        rootNode.se = new Node(halfLon, halfLat, rootNode.lrlon,
                rootNode.lrlat, rootNode, seImageName);

        buildNLevelQuadTree(level - 1, rootNode.nw);
        buildNLevelQuadTree(level - 1, rootNode.ne);
        buildNLevelQuadTree(level - 1, rootNode.sw);
        buildNLevelQuadTree(level - 1, rootNode.se);
    }

    class Node implements Comparable {
        double ullon;
        double ullat;
        double lrlon;
        double lrlat;
        private Node parent;
        private Node nw;
        private Node ne;
        private Node sw;
        private Node se;
        String imageName;

        Node(double ullon, double ullat, double lrlon,
             double lrlat, Node parent, String imageName) {
            this.ullon = ullon;
            this.ullat = ullat;
            this.lrlon = lrlon;
            this.lrlat = lrlat;
            this.parent = parent;
            this.imageName = imageName;
        }

        @Override
        public int compareTo(Object o) {
            if (this == o) {
                return 0;
            }
            if (o == null || getClass() != o.getClass()) {
                throw new RuntimeException();
            }

            Node temp = (Node) o;
            if (this.ullat == temp.ullat) {
                if (this.ullon == temp.ullat) {
                    return 0;
                } else if (this.ullon > temp.ullon) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                if (this.ullat > temp.ullat) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }

    @Override
    public String toString() {
        return nodeImageName(root);
    }

    private String nodeImageName(Node rootNode) {
        if (rootNode.nw == null) {
            return rootNode.imageName;
        }

        return rootNode.imageName + "\n " + nodeImageName(rootNode.nw)
                + "\n " + nodeImageName(rootNode.ne) + "\n " + nodeImageName(rootNode.sw)
                + "\n " + nodeImageName(rootNode.se);
    }
}
