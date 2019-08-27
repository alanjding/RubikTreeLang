import java.nio.file.Paths;
import java.util.Scanner;

public class Visualizer {

    private Interpreter interp;

    private Visualizer(String path) {
        this.interp = new Interpreter(Paths.get(path));
    }

    /**
     * Displays the instruction sequence as an indexed list.
     */
    private void displayInstructions() {

    }

    /**
     * Attempts to run the next step of the program, displaying the current
     * Rubik's Cube, its parent ID, and the global byte after the execution of
     * the step, as well as the program counter and consumed instruction BEFORE
     * executing the step (i.e. the instruction corresponding to what was just
     * run).
     *
     * @return true if the next step runs successfully, false otherwise
     */
    private boolean step() {

    }

    /**
     * Steps until the program's program counter reaches the value of pc. The
     * command at pc is not executed.
     * @param pc - stop execution once the program counter reaches pc
     */
    private void stepUntil(int pc) {

    }

    /**
     * Steps "steps" times or until the program terminates, whichever occurs
     * earlier.
     * @param steps - maximum number of steps to take
     */
    private void stepAtMost(int steps) {

    }

    /**
     * Handles user control of the Visualizer.
     */
    private void run() {
        System.out.println("Welcome to the RubikTreeLang Visualizer.");
        System.out.println("Things you can tell the Visualizer to do:");
        System.out.println("\tstep n (n is a non-negative integer):");
        System.out.println("\t\tAdvances program by at most n steps and " +
                "displays the program state at the end of each step.");
        System.out.println("\tstepuntil pc (pc is a non-negative integer):");
        System.out.println("\t\tAdvances program until its program counter " +
                "reaches the value pc and displays the program state at the " +
                "end of each step.");
        System.out.println("\tstepuntilend:");
        System.out.println("\t\tFinishes execution of the program and " +
                "displays the program state at the end of each step.");
        System.out.println("\tinstruction:");
        System.out.println("\t\tPrints the instruction sequence as an indexed" +
                "list.");
        System.out.println("\texit:");
        System.out.println("\t\tExits the Visualizer.");

        Scanner in = new Scanner(System.in);
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
            System.out.println("Usage: java Visualizer rtlFile");
            return;
        }

        Visualizer v = new Visualizer(args[0]);
        v.run();
    }
}
