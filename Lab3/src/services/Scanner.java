package services;

import model.Pair;
import utils.SymbolTable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Scanner {
    private String program;
    private SymbolTable symbolTable;
    private List<Pair<String, Pair<Integer, Integer>>> PIF;
    private final List<String> reservedWords;
    private final List<String> tokens;
    private int line;
    private int index;

    public Scanner() {
        this.reservedWords = new ArrayList<>();
        this.tokens = new ArrayList<>();
        try {
            readTokensFromFile();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setProgram(String program) {
        this.program = program;
    }

    private void readTokensFromFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("src/resources/token.txt"));
        String line;

        while((line = br.readLine()) != null) {
            switch(line) {
                case "prog", "int", "str", "char", "real", "read", "if", "else", "print", "do", "while", "array", "const", "funct" -> reservedWords.add(line);
                default -> tokens.add(line);
            }
        }
    }

    private void skipSpaces() {
        while (index < program.length() && Character.isWhitespace(program.charAt(index))) {
            if (program.charAt(index) == '\n')
                line += 1;
            index += 1;
        }
    }

    private void skipComments() {
        while (index < program.length() && program.charAt(index) == '/' && program.charAt(index + 1) == '/') {
            while (index < program.length() && program.charAt(index) != '\n')
                index += 1;
        }
    }

    private Pair<Integer, Integer> getPositionFromSymbolTable(String token) {
        try {
            return symbolTable.getPosition(token);
        }
        catch (Exception e) {
            try {
                symbolTable.add(token);
                return symbolTable.getPosition(token);
            }
            catch (Exception e2) {
                System.out.println(e2.getMessage());
            }
        }

        return new Pair<>(-1, -1);
    }

    private boolean validIdentifier(String identifier, String programSubstring) {
        if (reservedWords.contains(identifier)) {
            return false;
        }
        if (Pattern.compile("^[A-Za-z_][A-Za-z0-9_]* \\s*(?:array\\[[A-Za-z_][A-Za-z0-9_]*\\])?\\s*\\((int|char|str|real)\\)").matcher(programSubstring).find()) {
            return true;
        }
        return symbolTable.contains(identifier);
    }

    private boolean treatIdentifierToken() {
        var regex = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)");
        var matcher = regex.matcher(program.substring(index));

        if(!matcher.find()) {
            return false;
        }

        var identifier = matcher.group(1);
        if (!validIdentifier(identifier, program.substring(index))) {
            return false;
        }

        index += identifier.length();
        Pair<Integer, Integer> pos = getPositionFromSymbolTable(identifier);

        PIF.add(new Pair<>(identifier, pos));
        return true;
    }

    private boolean treatStringConstantToken() {
        var regex = Pattern.compile("^\"[a-zA-z0-9_ ?:*^+=.!]*\"");
        var matcher = regex.matcher(program.substring(index));

        if (!matcher.find()) {
            return false;
        }

        var stringConstant = matcher.group(1);
        index += stringConstant.length();
        Pair<Integer, Integer> pos = getPositionFromSymbolTable(stringConstant);

        PIF.add(new Pair<>("string const", pos));
        return true;
    }

    private boolean treatIntConstantToken() {
        var regex = Pattern.compile("^([+-]?[1-9][0-9]*|0)");
        var matcher = regex.matcher(program.substring(index));
        var invalidRegex = Pattern.compile("^([+-]?[1-9][0-9]*|0)[a-zA-z_]");

        if (!matcher.find() || invalidRegex.matcher(program.substring(index)).find()) {
            return false;
        }

        String intConstant = matcher.group(1);
        index += intConstant.length();
        Pair<Integer, Integer> pos = getPositionFromSymbolTable(intConstant);

        PIF.add(new Pair<>("int const", pos));
        return true;
    }

    private boolean treatTokenFromList() {
        String string = program.substring(index).split(" ")[0];

        for (var word: reservedWords) {
            if(string.startsWith(word)) {
                var regex = "^" + "[a-zA-Z0-9_]*" + word + "[a-zA-Z0-9_]+";
                if (Pattern.compile(regex).matcher(string).find()) {
                    return false;
                }
                index += word.length();
                PIF.add(new Pair<>(word, new Pair<>(-1, -1)));
                return true;
            }
        }

        for (String token: tokens) {
            if (Objects.equals(token, string) || string.startsWith(token)) {
                index += token.length();
                PIF.add(new Pair<>(token, new Pair<>(-1, -1)));
                return true;
            }
        }

        return false;
    }

    private void getNextToken() throws Exception {
        skipSpaces();
        skipComments();
        if (index == program.length()) {
            return;
        }
        if (treatIdentifierToken()) {
            return;
        }
        if (treatStringConstantToken()) {
            return;
        }
        if (treatIntConstantToken()) {
            return;
        }
        if (treatTokenFromList()) {
            return;
        }

        throw new Exception("Invalid token at line" + line + ", index" + index);
    }

    public void scan(String filename) {
        try {
            Path path = Path.of("src/resources/" + filename);
            setProgram(Files.readString(path));
            symbolTable = new SymbolTable(53);
            PIF = new ArrayList<>();
            index = 0;
            line = 1;

            while (index < program.length()) {
                getNextToken();
            }

            FileWriter fileWriter = new FileWriter("PIF-" + filename.replace(".txt", ".out"));
            for (var pair : PIF) {
                fileWriter.write(pair.getFirst() + " -> (" + pair.getSecond().getFirst() + ", " + pair.getSecond().getSecond() + ")\n");
            }
            fileWriter.close();
            fileWriter = new FileWriter("ST-" + filename.replace(".txt", ".out"));
            fileWriter.write(symbolTable.toString());
            fileWriter.close();

            System.out.println("Lexically correct");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
