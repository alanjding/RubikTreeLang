public class RubiksTrie {

    private RubiksCube root;
    private int numCubes;
    private byte global;

    public RubiksTrie() {
        numCubes = 0;
        root = new RubiksCube(null, this); // sets numCubes to 1
        global = 0;
    }

    RubiksCube getRoot() {
        return root;
    }

    int getNumCubes() {
        return numCubes;
    }

    void setGlobalByte(byte b) {
        global = b;
    }

    byte getGlobalByte() {
        return global;
    }

    int assignCubeID() {
        int id = numCubes;
        numCubes++;
        return id;
    }
}
