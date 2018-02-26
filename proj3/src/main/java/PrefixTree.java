import java.util.LinkedList;

/**
 * Created by 62339 on 2017/4/16.
 */
public class PrefixTree {
    PrefixTreeNode root;

    class PrefixTreeNode {
        boolean isLeaf;
        boolean isString = false;
        char ch;
        PrefixTreeNode[] next = new PrefixTreeNode[27];

        PrefixTreeNode(char ch, boolean isLeaf) {
            this.ch = ch;
            this.isLeaf = isLeaf;
        }
    }

    PrefixTree() {
        root = new PrefixTreeNode(' ', true);
    }

    void insertString(String s) {
        PrefixTreeNode temp = root;
        for (char ch : s.toCharArray()) {
            int index = ch - 'a' + 1;
            if (ch == ' ') {
                index = 0;
            }
            if (temp.next[index] == null) {
                temp.next[index] = new PrefixTreeNode(ch, true);
            }
            temp.isLeaf = false;
            temp = temp.next[index];
        }
        temp.isString = true;
    }

    PrefixTreeNode findPrefix(String s) {
        PrefixTreeNode temp = root;
        for (char ch: s.toCharArray()) {
            int index = ch - 'a' + 1;
            if (ch == ' ') {
                index = 0;
            }
            if (temp.next[index] == null) {
                return null;
            }
            temp = temp.next[index];
        }
        return temp;
    }

    LinkedList<String> findLocation(String name) {
        PrefixTreeNode prefixLoc = findPrefix(name);

        return findLocationHelper(prefixLoc, name);
    }

    private LinkedList<String> findLocationHelper(PrefixTreeNode pre, String name) {
        LinkedList<String> temp = new LinkedList<>();
        if (pre.isLeaf) {
            temp.add(name);
        } else {
            if (pre.isString) {
                temp.add(name);
            }
            for (int i = 0; i < 27; i += 1) {
                if (pre.next[i] != null) {
                    LinkedList<String> childrenList = findLocationHelper(pre.next[i], name + pre.next[i].ch);
                    temp.addAll(childrenList);
                }
            }
        }
        return temp;
    }
}
