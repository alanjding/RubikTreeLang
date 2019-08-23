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

    public void setPayload(byte b) {
        payload = b;
    }

    public byte getPayload() {
        return payload;
    }

    public void initializeChildRubiksCube() {
        child = new RubiksCube(cube, trie);
    }

    public void incrementPayloadBy(byte b) {
        payload += b;
    }

    public void decrementPayloadBy(byte b) {
        payload -= b;
    }

    public void multiplyPayloadBy(byte b) {
        payload *= b;
    }

    public void dividePayloadBy(byte b) {
        payload /= b;
    }

    public void modPayloadBy(byte b) {
        payload %= b;
    }

    public String toString() {
        return String.format("%02x", payload)
                + "|"
                + ((child == null) ? "." : child.getID());
    }
}
