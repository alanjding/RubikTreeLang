import java.io.EOFException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Visualizer {

    private Interpreter interp;
    private String path;

    private Visualizer(String path) {
        this.path = path;
        this.interp = new Interpreter(Paths.get(path));
    }

    /**
     * Prints all valid Visualizer commands.
     */
    private void displayValidCommands() {
        System.out.println("Things you can tell the Visualizer to do:");
        System.out.println("\tstep n (n is a non-negative integer):");
        System.out.println("\t\tAdvances program by at most n steps and " +
                "displays the program state at the end of each step.");
        System.out.println("\tstepuntil pc (pc is a non-negative integer):");
        System.out.println("\t\tAdvances program until its program counter " +
                "reaches the value pc and displays the program state at the " +
                "end of each step.");
        System.out.println("\trun:");
        System.out.println("\t\tFinishes execution of the program and " +
                "displays the program state at the end of each step.");
        System.out.println("\tdisplaycube id (id is a non-negative integer):");
        System.out.println("\t\tDisplays the cube with ID id.");
        System.out.println("\trestart:");
        System.out.println("\t\tResets the program's state/memory.");
        System.out.println("\tcode:");
        System.out.println("\t\tPrints the code's instruction sequence as an " +
                "indexed list.");
        System.out.println("\texit:");
        System.out.println("\t\tExits the Visualizer.");
    }

    /**
     * Displays the code instruction sequence as an indexed list.
     */
    private void displayCode() {
        String[] instructions = interp.getInstructions();
        for (int pc = 0; pc < instructions.length; pc++) {
            System.out.format("%-6s%-8s| ", pc, instructions[pc]);
            if (pc % 8 == 7 || pc == instructions.length - 1)
                System.out.println();
        }
    }

    /**
     * Resets the program's state/memory.
     */
    private void restart() {
        interp = new Interpreter(Paths.get(path));
    }

    /**
     * Attempts to run the next step of the program, displaying the current
     * Rubik's Cube, its parent ID, and the global byte after the execution of
     * the step, as well as the program counter and consumed instruction BEFORE
     * executing the step (i.e. the instruction corresponding to what was just
     * run).
     */
    private void step() throws EOFException, UnsupportedOperationException {
        System.out.println(
                "----------------------------------------------------------\n");
        System.out.println("Consumed instruction "
                + interp.getInstructions()[interp.getProgramCounter()]
                + " at pc = " + interp.getProgramCounter());
        interp.processNextCommand();
        System.out.format("Global byte hex value: %x\n",
                interp.getTrie().getGlobalByte());
        System.out.println(interp.getCurrCube());
        System.out.println();
    }

    /**
     * Steps until the program's program counter reaches the value of pc. The
     * command at pc is not executed.
     *
     * @param endpc - stop execution once the program counter reaches pc
     */
    private void stepUntil(int endpc) {
        while (interp.getProgramCounter() < endpc)
            try {
                step();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("stepuntil or run returned prematurely.");
                return;
            }
    }

    /**
     * Steps "steps" times or until the program terminates, whichever occurs
     * earlier.
     *
     * @param steps - maximum number of steps to take
     */
    private void stepAtMost(int steps) {
        for (int i = 0; i < steps; i++) {
            if (interp.getProgramCounter() >= interp.getInstructions().length) {
                System.out.println("step successfully executed "
                        + i + " steps before reaching the end of the program.");
                return;
            }

            try {
                step();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("step returned prematurely.");
                return;
            }
        }

        System.out.println("All " + steps + " steps executed successfully.");
    }

    /**
     * Returns a pointer to the Rubik's Cube with ID id.
     *
     * @param id - cube ID to look for
     * @return Desired Rubik's Cube if found, null otherwise.
     */
    private RubiksCube getCube(int id) {
        // level order traversal
        Queue<RubiksCube> q = new LinkedList<>();
        q.add(interp.getTrie().getRoot());

        while (!q.isEmpty()) {
            RubiksCube curr = q.remove();
            if (curr.getID() == id)
                return curr;

            for (Node n : curr.getNodes())
                if (n.getChild() != null)
                    q.add(n.getChild());
        }

        return null;
    }

    /**
     * Handles user control of the Visualizer.
     */
    private void run() {
        System.out.println("Welcome to the RubikTreeLang Visualizer.\n");
        displayValidCommands();

        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String command = in.next();
            switch (command) {
                case "step":
                    int numSteps;

                    try {
                        numSteps = in.nextInt();
                    } catch (Exception e) {
                        System.out.println("Argument must be an integer.");
                        in.nextLine(); // consume extraneous arguments and \n
                        break;
                    }

                    if (numSteps < 0)
                        System.out.println("Argument must be non-negative.");
                    else
                        stepAtMost(numSteps);

                    in.nextLine();
                    break;
                case "stepuntil":
                    int end;

                    try {
                        end = in.nextInt();
                    } catch (Exception e) {
                        System.out.println("Argument must be an integer.");
                        in.nextLine();
                        break;
                    }

                    if (end > interp.getInstructions().length)
                        System.out.println(
                                "Specified end lies beyond end of program.");
                    else if (end < 0)
                        System.out.println("Argument must be non-negative.");
                    else
                        stepUntil(end);

                    in.nextLine();
                    break;
                case "run":
                    stepUntil(interp.getInstructions().length);
                    in.nextLine();
                    break;
                case "displaycube":
                    int id;

                    try {
                        id = in.nextInt();
                    } catch (Exception e) {
                        System.out.println("Argument must be an integer.");
                        in.nextLine();
                        break;
                    }

                    if (id >= interp.getTrie().getNumCubes())
                        System.out.println("Rubik's Cube with ID " + id
                                + " does not exist.");
                    else if (id < 0)
                        System.out.println("Argument must be non-negative.");
                    else {
                        RubiksCube cube = getCube(id);
                        if (cube == null)
                            System.out.println("Rubik's Cube with ID " + id
                                    + " does not exist.");
                        else
                            System.out.println(cube);
                    }

                    in.nextLine();
                    break;
                case "restart":
                    restart();
                    in.nextLine();
                    break;
                case "code":
                    displayCode();
                    in.nextLine();
                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Invalid command.\n");
                    displayValidCommands();
                    in.nextLine();
            }
        }
    }

    /**
     * Runs the visualizer. Once compiled, this Visualizer takes exactly one
     * command-line argument corresponding to a project-relative path to the
     * .rtl file to be run and visualized.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Visualizer rtlFilePath");
            return;
        }

        Visualizer v = new Visualizer(args[0]);
        v.run();
    }
}
