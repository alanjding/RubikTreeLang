public class Node {

    private byte payload;
    private RubiksCube cube;
    private RubiksCube child;
    private RubiksTrie trie;

    public Node(RubiksCube cube, RubiksTrie trie) {
        payload = 0;
        this.cube = cube;
        child = null;
        this.trie = trie;
    }

    void setPayload(byte b) {
        payload = b;
    }

    byte getPayload() {
        return payload;
    }

    RubiksCube getChild() {
        return child;
    }

    void initializeChildRubiksCube() {
        child = new RubiksCube(cube, trie);
    }

    public String toString() {
        return String.format("%02x", payload)
                + "|"
                + ((child == null) ? "." : child.getID());
    }
}
