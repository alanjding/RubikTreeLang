public class RubiksCube {

    /**
     * Cube cell layout:
     * <p>
     * 17 18
     * 19 20
     * <p>
     * 21 22   23  0    1  2    3  4
     * 5  6    7  8    9 10   11 12
     * <p>
     * 13 14
     * 15 16
     */

    private Node[] cube;
    private RubiksCube parent;
    private int id;

    // Any turn comprises three four-cycles on the cube's cells
    private static final int[][] U =
            {{17, 18, 20, 19}, {4, 2, 0, 22}, {21, 3, 1, 23}};

    private static final int[][] L =
            {{21, 22, 6, 5}, {17, 23, 13, 12}, {4, 19, 7, 15}};

    private static final int[][] F =
            {{23, 0, 8, 7}, {19, 1, 14, 6}, {22, 20, 9, 13}};

    private static final int[][] R =
            {{1, 2, 10, 9}, {20, 3, 16, 8}, {0, 18, 11, 14}};

    private static final int[][] B =
            {{3, 4, 12, 11}, {18, 21, 15, 10}, {2, 17, 5, 16}};

    private static final int[][] D =
            {{13, 14, 16, 15}, {7, 9, 11, 5}, {6, 8, 10, 12}};

    public RubiksCube(RubiksCube parent, RubiksTrie trie) {
        cube = new Node[24];
        for (int i = 0; i < 24; i++)
            cube[i] = new Node(this, trie);

        this.parent = parent;
        id = trie.assignCubeID();
    }

    int getID() {
        return id;
    }

    Node getRWNode() {
        return cube[0];
    }

    RubiksCube getParent() {
        return parent;
    }

    /**
     * Not a language feature: this is for the visualizer to search for a cube
     * with a specified ID.
     *
     * @return
     */
    Node[] getNodes() {
        return cube;
    }

    // Modifies cube (Node array)
    private void cyclePermute(int[] indices) {
        int cycleSize = indices.length;
        if (cycleSize == 0)
            throw new IllegalArgumentException("Cycle must have non-zero size");

        Node temp = cube[indices[cycleSize - 1]];
        for (int i = cycleSize - 1; i > 0; i--) {
            cube[indices[i]] = cube[indices[i - 1]];
        }
        cube[indices[0]] = temp;
    }

    /**
     * Turns the virtual Rubik's Cube as specified by the method arguments.
     *
     * @param face Which face to turn: can be U, L, F, R, B, or D.
     * @param type Number of 90 degree rotations.
     *             -1 or 3 can be used for "prime".
     */
    void turn(char face, int type) {
        int[][] cycles;

        switch (face) {
            case 'U':
                cycles = U;
                break;
            case 'L':
                cycles = L;
                break;
            case 'F':
                cycles = F;
                break;
            case 'R':
                cycles = R;
                break;
            case 'B':
                cycles = B;
                break;
            case 'D':
                cycles = D;
                break;
            default:
                throw new IllegalArgumentException("Invalid face to turn");
        }

        int numRotations = type & 3; // sign-agnostic modulo 4
        for (int i = 0; i < numRotations; i++)
            for (int[] cycle : cycles)
                cyclePermute(cycle);
    }

    public String toString() {
        String parentID = getParent() == null ?
                "current cube is the root and has no parent" :
                String.valueOf(getParent().getID());

        return "Rubik's Cube ID: " + getID() +
                "\nParent Rubik's Cube ID: " + parentID +
                String.format("\n\n%15s%-7s%-8s\n", "", cube[17], cube[18])
                + String.format("%15s%-7s%-8s\n\n", "", cube[19], cube[20])
                + String.format("%-7s%-8s%-7s%-8s%-7s%-8s%-7s%-8s\n",
                cube[21], cube[22], cube[23], cube[0],
                cube[1], cube[2], cube[3], cube[4])
                + String.format("%-7s%-8s%-7s%-8s%-7s%-8s%-7s%-8s\n\n",
                cube[5], cube[6], cube[7], cube[8],
                cube[9], cube[10], cube[11], cube[12])
                + String.format("%15s%-7s%-8s\n", "", cube[13], cube[14])
                + String.format("%15s%-7s%-8s\n", "", cube[15], cube[16]);
    }
}
