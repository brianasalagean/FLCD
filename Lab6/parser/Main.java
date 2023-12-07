package parser;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Grammar grammar;
        Parser parser;
        try {
            while (true) {
                System.out.println("Enter an option ('g1' for g1.txt, 'g2' for g2.txt, 'x' to exit):");
                String option = scanner.nextLine();

                switch (option) {
                    case "g1" -> {
                        grammar = Grammar.fromFile("src/parser/data/g1.txt");
                        parser = new Parser(grammar);
                        System.out.println("First: " + parser.getFirstSet());
                        System.out.println("Follow: " + parser.getFollowSet());
                    }
                    case "g2" -> {
                        grammar = Grammar.fromFile("src/parser/data/g2.txt");
                        parser = new Parser(grammar);
                        System.out.println("First: " + parser.getFirstSet());
                        System.out.println("Follow: " + parser.getFollowSet());
                    }
                    case "test" -> {
                        Test test = new Test();
                        test.testAll();
                    }
                    case "x" -> {
                        System.out.println("Exiting...");
                        scanner.close();
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid option. Please enter 'g1', 'g2', or 'x'.");
                }
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
