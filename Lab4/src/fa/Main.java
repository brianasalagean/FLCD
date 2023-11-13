package fa;
import java.util.Objects;

public class Main {
    public static void menu() {
        System.out.println("1. Print states");
        System.out.println("2. Print alphabet");
        System.out.println("3. Print initial state");
        System.out.println("4. Print final states");
        System.out.println("5. Print transitions");
        System.out.println("6. Check sequence");
        System.out.println("7. Get matching substring");
        System.out.println("0. Exit");
    }

    public static void main(String[] args) {
        FA fa = new FA("src/fa/data/fa.in");
        String sequence;
        boolean condition = true;
        menu();

        while (condition) {
            System.out.println("option: ");
            var option = new java.util.Scanner(System.in).nextInt();

            switch (option) {
                case 1:
                    fa.printStates();
                    break;
                case 2:
                    fa.printAlphabet();
                    break;
                case 3:
                    fa.printInitialState();
                    break;
                case 4:
                    fa.printFinalStates();
                    break;
                case 5:
                    fa.printTransitions();
                    break;
                case 6:
                    System.out.println("sequence: ");
                    sequence = new java.util.Scanner(System.in).nextLine();
                    System.out.println("Result: " + fa.checkIfAccepted(sequence));
                    break;
                case 7:
                    System.out.println("sequence: ");
                    sequence = new java.util.Scanner(System.in).nextLine();
                    var acceptedSequence = fa.getNextAccepted(sequence);
                    if (Objects.equals(acceptedSequence, "")) {
                        System.out.println("No matching substring");
                    } else {
                        System.out.println(acceptedSequence);
                    }
                    break;
                case 0:
                    condition = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }
}