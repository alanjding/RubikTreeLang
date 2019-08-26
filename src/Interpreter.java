import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter {

    private RubiksTrie trie;
    private RubiksCube currCube;
    private String[] instructions;
    private int pc; // program counter
    private Stack<Integer> openBracketLocations;
    private static Map<String, Runnable> commandMap;
    private Scanner sc;

    // any commands that require an argument will populate this instance variable
    private String argument;

    public Interpreter(Path codePath) {
        trie = new RubiksTrie();
        currCube = trie.getRoot();
        instructions = parseCode(codePath);
        pc = 0;
        openBracketLocations = new Stack<>();
        commandMap = createCommandMap();
        sc = new Scanner(System.in);
    }

    private static String[] parseCode(Path path) {
        String fileContent;
        try {
            fileContent = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        fileContent = fileContent.replaceAll("#.*", "");
        return fileContent.split("((\\s|\\n)+)");
    }

    /**
     * Java's own Byte.parseByte doesn't handle bytes with the leading bit set
     * to 1.
     *
     * @param s a String matching [01]+
     * @return byte representation of s
     */
    private static byte binaryStringToByte(String s) {
        byte result = 0;
        for (int i = 0; i < 8; i++) {
            if (s.charAt(s.length() - i - 1) == '1')
                result |= 1 << i;
        }

        return result;
    }

    /**
     * Creates a HashMap that takes a String and returns a Runnable. To run the
     * returned Runnable, call .run() on it. The Runnables in here alter the
     * state of the Interpreter.
     *
     * @return the command map
     */
    private Map<String, Runnable> createCommandMap() {
        Map<String, Runnable> commandMap = new HashMap<>();
        commandMap.put("setc", () -> {
            pc++;
            argument = instructions[pc];
            currCube.getRWNode().setPayload((byte) argument.charAt(0));
        });
        commandMap.put("setd", () -> {
            pc++;
            argument = instructions[pc];
            currCube.getRWNode()
                    .setPayload((byte) Integer.parseInt(argument));
        });
        commandMap.put("setx", () -> {
            pc++;
            argument = instructions[pc];
            currCube.getRWNode()
                    .setPayload(Integer.decode("0x" + argument).byteValue());
        });
        commandMap.put("gsetc", () -> {
            pc++;
            argument = instructions[pc];
            trie.setGlobalByte((byte) argument.charAt(0));
        });
        commandMap.put("gsetd", () -> {
            pc++;
            argument = instructions[pc];

            // can cause overflow and throw an exception
            trie.setGlobalByte((byte) Integer.parseInt(argument));
        });
        commandMap.put("gsetx", () -> {
            pc++;
            argument = instructions[pc];
            trie.setGlobalByte(Integer.decode("0x" + argument).byteValue());
        });
        commandMap.put("gtp", () -> currCube.getRWNode()
                .setPayload(trie.getGlobalByte()));
        commandMap.put("ptg", () -> trie.setGlobalByte(currCube.getRWNode()
                .getPayload()));
        commandMap.put("g++", () -> trie.incrementGlobalByte());
        commandMap.put("g--", () -> trie.decrementGlobalByte());
        commandMap.put("inputb", () -> {
            String match = sc.findInLine("[01]+");
            byte payload = binaryStringToByte(
                    match.substring(Math.max(match.length() - 8, 0)));
            currCube.getRWNode().setPayload(payload);
        });
        commandMap.put("inputc", () -> currCube.getRWNode()
                .setPayload((byte) sc.findInLine(".").charAt(0)));
        commandMap.put("inputd", () -> {
            String match = sc.findInLine("[0-9]+");

            // can cause overflow and throw an exception
            byte payload = (byte) Integer.parseInt(match);

            currCube.getRWNode().setPayload(payload);
        });
        commandMap.put("inputx", () -> {
            String match = sc.findInLine("[0-9A-Fa-f]+");
            byte payload = Integer.decode("0x" +
                    match.substring(Math.max(match.length() - 2, 0))).byteValue();
            currCube.getRWNode().setPayload(payload);
        });
        commandMap.put("outputb", () -> {
            String formatted = String.format("%8s",
                    Integer.toBinaryString(currCube.getRWNode().getPayload() & 0xff))
                    .replace(' ', '0');

            System.out.println(formatted);
        });
        commandMap.put("outputc", () -> {

        });
        commandMap.put("outputd", () -> {

        });
        commandMap.put("outputx", () -> {

        });
        commandMap.put("outputX", () -> {

        });
        commandMap.put("+", () -> {

        });
        commandMap.put("-", () -> {

        });
        commandMap.put("*", () -> {

        });
        commandMap.put("/", () -> {

        });
        commandMap.put("%", () -> {

        });
        commandMap.put("{", () -> {

        });
        commandMap.put("}", () -> {

        });
        commandMap.put("v", () -> {

        });
        commandMap.put("^", () -> {

        });
        commandMap.put("", () -> {
        });

        return commandMap;
    }

    public void processNextCommand() throws EOFException {
        if (pc >= instructions.length)
            throw new EOFException("Attempted to process command past the end");

        Runnable instruction = commandMap.get(instructions[pc]);
        if (instruction == null)
            throw new UnsupportedOperationException(
                    "Invalid command '" + instructions[pc] + "' at pc = " + pc);
        else
            instruction.run();

        pc++;
    }

    public static void main(String[] args) {
//        Interpreter subj = new Interpreter(Paths.get("../examples/iterator.rtl"));
//        System.out.println(Arrays.toString(subj.instructions));

        Scanner in = new Scanner(System.in);
        System.out.println(in.next("."));
    }
}
