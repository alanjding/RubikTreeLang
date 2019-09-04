import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter {
    private StringReader bf;

    public Converter(String inputPath) {
        String bfStr;

        try {
            bfStr = readFile(inputPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        bf = new StringReader(decomment(bfStr));
    }

    private static String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.US_ASCII);
    }

    private static String decomment(String commentedBF) {
        return commentedBF.replaceAll("[^<>+\\-.,\\[\\]]+", "");
    }

    private static Map<Character, String> createCommandMap() {
        Map<Character, String> commandMap = new HashMap<>();

        commandMap.put('>', "inputd ptg [ F v setd 25 + ^ ] g-- [ F v setd 25" +
                " + ^ ] g-- [ F v setd 25 + ^ ] g-- [ F x v setd 25 + ^ ] g--" +
                " [ F v setd 25 + ^ ] g-- [ F v setd 25 + ^ ] g-- [ F v setd" +
                " 25 + ^ ] g-- [ F y v setd 25 + ^ ] g-- [ F v setd 25 + ^ ]" +
                " g-- [ F v setd 25 + ^ ] g-- [ F v setd 25 + ^ ] g-- [ F x v" +
                " setd 25 + ^ ] g-- [ F v setd 25 + ^ ] g-- [ F v setd 25 + ^" +
                " ] g-- [ F v setd 25 + ^ ] g-- [ F y v setd 25 + ^ ] g-- [ F" +
                " v setd 25 + ^ ] g-- [ F v setd 25 + ^ ] g-- [ F v setd 25 +" +
                " ^ ] g-- [ F x v setd 25 + ^ ] g-- [ F v setd 25 + ^ ] g-- [" +
                " F v setd 25 + ^ ] g-- [ F v setd 25 + ^ ] g-- [ F y v g++ ]" +
                " g--");

        commandMap.put('<', "[ ^ y' F' v setd 47 + ^ ] g-- [ v setd 23 + ^ F'" +
                " ] g-- [ v setd 23 + ^ F' ] g-- [ v setd 23 + ^ F' ] g-- [ v" +
                " setd 23 + ^ x' F' ] g-- [ v setd 23 + ^ F' ] g-- [ v setd" +
                " 23 + ^ F' ] g-- [ v setd 23 + ^ F' ] g-- [ v setd 23 + ^ y'" +
                " F' ] g-- [ v setd 23 + ^ F' ] g-- [ v setd 23 + ^ F' ] g--" +
                " [ v setd 23 + ^ F' ] g-- [ v setd 23 + ^ x' F' ] g-- [ v" +
                " setd 23 + ^ F' ] g-- [ v setd 23 + ^ F' ] g-- [ v setd 23 +" +
                " ^ F' ] g-- [ v setd 23 + ^ y' F' ] g-- [ v setd 23 + ^ F' ]" +
                " g-- [ v setd 23 + ^ F' ] g-- [ v setd 23 + ^ F' ] g-- [ v" +
                " setd 23 + ^ x' F' ] g-- [ v setd 23 + ^ F' ] g-- [ v setd" +
                " 23 + ^ F' ] g-- [ v setd 23 + ^ F' ] g--");

        commandMap.put('[', "<");
        commandMap.put(']', ">");

        commandMap.put('+', "[ ptg F v setd 1 + ^ F' gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 + ^ gtp gsetd 24 ] g-- [ ptg v setd 1 + ^ gtp" +
                " gsetd 24 ] g--");

        commandMap.put('-', "[ ptg F v setd 1 - ^ F' gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp" +
                " gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg" +
                " v setd 1 - ^ gtp gsetd 24 ] g-- [ ptg v setd 1 - ^ gtp" +
                " gsetd 24 ] g--");

        commandMap.put(',', "inputc");
        commandMap.put('.', "outputc");

        return commandMap;
    }

    /**
     * Reads one char from the bf StringReader if the end hasn't been reached.
     *
     * @return read char or -1 if at the end of the stream
     */
    private int readChar() {
        try {
            return bf.read();
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
    }

    /**
     * Converts the BF file read into the scanner into the .rtl format and
     * saves the newly created file.
     *
     * @param outputPath - where to save the .rtl file
     */
    private void convert(String outputPath) {
        Path file = Paths.get(outputPath);
        StringBuilder line = new StringBuilder();
        Map<Character, String> commandMap = createCommandMap();

        int c;
        for (c = readChar(); c != -1; c = readChar()) {
            if (c == -2)
                return;

            line.append(commandMap.get((char) c));
            line.append(" ");
        }

        List<String> lines = Collections.singletonList(line.toString());
        try {
            Files.write(file, lines, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(".rtl file write failed.");
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(
                    "Usage: java Converter bfInputPath rtlOutputPath");
            return;
        }

        Converter converter = new Converter(args[0]);
        converter.convert(args[1]);
    }
}
