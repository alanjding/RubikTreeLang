public class RubiksTrie {

    private RubiksCube root;
    private int numCubes;
    private byte global;

    public RubiksTrie() {
        numCubes = 0;
        root = new RubiksCube(null, this); // sets numCubes to 1
        global = 0;
    }

    public RubiksCube getRoot() {
        return root;
    }

    public void setGlobalByte(byte b) {
        global = b;
    }

    public byte getGlobalByte() {
        return global;
    }

    public int assignCubeID() {
        int id = numCubes;
        numCubes++;
        return id;
    }
}
