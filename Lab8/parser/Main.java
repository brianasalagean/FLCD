package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Grammar g1 = null;
        Grammar g2 = null;
        try {
            g1 = Grammar.fromFile("src/parser/data/g1.txt");;
            g2 = Grammar.fromFile("src/parser/data/g2.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parser p1 = new Parser(g1);
        Parser p2 = new Parser(g2);

        while (true) {
            System.out.print(">> ");
            String cmd = scanner.nextLine();
            if ("g1".equals(cmd)) {
                System.out.println("Read g1");
                evaluate(p1, "src/parser/data/seq.txt", g1, "src/parser/data/out1.txt");
            } else if ("g2".equals(cmd)) {
                System.out.println("Read g2");
                evaluate(p2, "src/parser/data/PIF.out", g2, "src/parser/data/out2.txt");
            } else {
                System.out.println("Invalid option");
            }
        }
    }

    private static void evaluate(Parser parser, String sequenceFile, Grammar grammar, String resultFile) {
        System.out.println(parser.getFirstSet());
        System.out.println(parser.getFollowSet());
        Tree tree = new Tree(grammar, resultFile);
        List<String> result = List.of(parser.evaluateSequence(readSequence(sequenceFile)).split(" "));
        System.out.println(result);
        tree.build(result);
        tree.printTable();
    }

    private static List<String> readSequence(String file) {
        List<String> sequence = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String symbol = line;
                if (line.contains("->")) {
                    symbol = List.of(line.split("->")).get(0);
                }
                sequence.add(symbol.trim());
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return sequence;
    }
}
