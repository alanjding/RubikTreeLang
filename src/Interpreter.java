import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Interpreter {

    private RubiksTrie trie;
    private RubiksCube currCube;
    private String[] instructions;
    private int pc; // program counter
    private Stack<Integer> openBracketLocations;
    private static Map<String, Runnable> commandMap;
    private Scanner sc;

    private static final String WHITESPACE_PATTERN = "\\p{javaWhitespace}+";

    // any commands that require an argument
    // will populate this instance variable
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

        return fileContent.replaceAll("#.*", "")
                .split(WHITESPACE_PATTERN);
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
            if (match != null) {
                byte payload = binaryStringToByte(
                        match.substring(Math.max(match.length() - 8, 0)));
                currCube.getRWNode().setPayload(payload);
            } else
                currCube.getRWNode().setPayload((byte) 0);
        });

        commandMap.put("inputc", () -> {
            sc.useDelimiter("");

            if (sc.hasNext())
                currCube.getRWNode().setPayload((byte) sc.next().charAt(0));
            else
                currCube.getRWNode().setPayload((byte) 0);

            sc.useDelimiter(WHITESPACE_PATTERN);
        });

        commandMap.put("inputd", () -> {
            String match = sc.findInLine("[0-9]+");
            if (match != null) {
                // can cause overflow and throw an exception
                byte payload = (byte) Integer.parseInt(match);

                currCube.getRWNode().setPayload(payload);
            } else
                currCube.getRWNode().setPayload((byte) 0);
        });

        commandMap.put("inputx", () -> {
            String match = sc.findInLine("[0-9A-Fa-f]+");
            if (match != null) {
                byte payload = Integer.decode("0x" +
                        match.substring(Math.max(match.length() - 2, 0)))
                        .byteValue();
                currCube.getRWNode().setPayload(payload);
            } else
                currCube.getRWNode().setPayload((byte) 0);
        });

        commandMap.put("outputb", () -> {
            String formatted = String.format("%8s",
                    Integer.toBinaryString(currCube.getRWNode()
                            .getPayload() & 0xff))
                    .replace(' ', '0');

            System.out.println(formatted);
        });

        commandMap.put("outputc", () -> System.out.print(
                (char) currCube.getRWNode().getPayload()));

        commandMap.put("outputd", () -> System.out.println(
                currCube.getRWNode().getPayload() & 0xff));

        commandMap.put("outputx", () ->
                System.out.format("%x%n", currCube.getRWNode().getPayload()));

        commandMap.put("outputX", () ->
                System.out.format("%X%n", currCube.getRWNode().getPayload()));

        commandMap.put("+", () -> currCube.getRWNode().setPayload((byte)
                (currCube.getRWNode().getPayload() + trie.getGlobalByte())));

        commandMap.put("-", () -> currCube.getRWNode().setPayload((byte)
                (currCube.getRWNode().getPayload() - trie.getGlobalByte())));

        commandMap.put("*", () -> currCube.getRWNode().setPayload((byte)
                (currCube.getRWNode().getPayload() * trie.getGlobalByte())));

        commandMap.put("/", () -> currCube.getRWNode().setPayload((byte)
                (currCube.getRWNode().getPayload() / trie.getGlobalByte())));

        commandMap.put("%", () -> currCube.getRWNode().setPayload((byte)
                (currCube.getRWNode().getPayload() % trie.getGlobalByte())));

        commandMap.put("{", () -> {
            if (trie.getGlobalByte() == 0)
                while (!instructions[pc].equals("}")) pc++;
            else
                openBracketLocations.push(pc);

        });

        // minus 1 is necessary because pc is incremented by default in
        // processNextCommand and we want to recheck the "{" condition each
        // time around
        commandMap.put("}", () -> pc = openBracketLocations.pop() - 1);

        commandMap.put("v", () -> {
            Node node = currCube.getRWNode();
            if (node.getChild() == null) {
                node.initializeChildRubiksCube();
            }
            currCube = node.getChild();
        });

        commandMap.put("^", () -> {
            if (currCube.getParent() != null)
                currCube = currCube.getParent();
        });

        commandMap.put("U", () -> currCube.turn('U', 1));
        commandMap.put("U2", () -> currCube.turn('U', 2));
        commandMap.put("U'", () -> currCube.turn('U', -1));
        commandMap.put("L", () -> currCube.turn('L', 1));
        commandMap.put("L2", () -> currCube.turn('L', 2));
        commandMap.put("L'", () -> currCube.turn('L', -1));
        commandMap.put("F", () -> currCube.turn('F', 1));
        commandMap.put("F2", () -> currCube.turn('F', 2));
        commandMap.put("F'", () -> currCube.turn('F', -1));
        commandMap.put("R", () -> currCube.turn('R', 1));
        commandMap.put("R2", () -> currCube.turn('R', 2));
        commandMap.put("R'", () -> currCube.turn('R', -1));
        commandMap.put("B", () -> currCube.turn('B', 1));
        commandMap.put("B2", () -> currCube.turn('B', 2));
        commandMap.put("B'", () -> currCube.turn('B', -1));
        commandMap.put("D", () -> currCube.turn('D', 1));
        commandMap.put("D2", () -> currCube.turn('D', 2));
        commandMap.put("D'", () -> currCube.turn('D', -1));

        commandMap.put("x", () -> {
            currCube.turn('R', 1);
            currCube.turn('L', -1);
        });

        commandMap.put("x'", () -> {
            currCube.turn('R', -1);
            currCube.turn('L', 1);
        });

        commandMap.put("x2", () -> {
            currCube.turn('R', 2);
            currCube.turn('L', 2);
        });

        commandMap.put("y", () -> {
            currCube.turn('U', 1);
            currCube.turn('D', -1);
        });

        commandMap.put("y'", () -> {
            currCube.turn('U', -1);
            currCube.turn('D', 1);
        });

        commandMap.put("y2", () -> {
            currCube.turn('U', 2);
            currCube.turn('D', 2);
        });

        commandMap.put("z", () -> {
            currCube.turn('F', 1);
            currCube.turn('B', -1);
        });

        commandMap.put("z'", () -> {
            currCube.turn('F', -1);
            currCube.turn('B', 1);
        });

        commandMap.put("z2", () -> {
            currCube.turn('F', 2);
            currCube.turn('B', 2);
        });

        // There might be empty strings in the instruction array
        commandMap.put("", () -> {});

        return commandMap;
    }

    void processNextCommand() throws EOFException,
            UnsupportedOperationException {
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

    /**
     * If called without any previous calls to processNextCommand, this method
     * runs the whole RubikTreeLang program from start to finish. Otherwise,
     * this method runs the program from where the interpreter last stopped.
     */
    void processRemainingCommands() {
        while (pc < instructions.length)
            try {
                processNextCommand();
            } catch (Exception e) {
                e.printStackTrace();
                pc = instructions.length;
            }
    }

    public static void main(String[] args) {
        Interpreter subj = new Interpreter(Paths.get("examples/cat.rtl"));
        System.out.println(Arrays.toString(subj.instructions));
        subj.processRemainingCommands();
        System.out.println(subj.currCube.toString());
    }
}
