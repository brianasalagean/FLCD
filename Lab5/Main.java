package parser;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            // Create Grammar object from g1.txt
            Grammar g1 = Grammar.fromFile("src/parser/data/g1.txt");
            System.out.println("Grammar g1:");
            System.out.println(g1);

            // Check if g1 is a valid CFG
            if (Grammar.validate(g1)) {
                System.out.println("Grammar g1 is a valid CFG.\n");
            } else {
                System.out.println("Grammar g1 is NOT a valid CFG.\n");
            }

            // Create Grammar object from g2.txt
            Grammar g2 = Grammar.fromFile("src/parser/data/g2.txt");
            System.out.println("Grammar g2:");
            System.out.println(g2);

            // Check if g2 is a valid CFG
            if (Grammar.validate(g2)) {
                System.out.println("Grammar g2 is a valid CFG.\n");
            } else {
                System.out.println("Grammar g2 is NOT a valid CFG.\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

