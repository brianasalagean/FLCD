package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Grammar {

    private List<String> N;
    private List<String> E;
    private Map<String, List<String>> P;
    private String S;

    public List<String> getN() {
        return N;
    }

    public List<String> getE() {
        return E;
    }

    public Map<String, List<String>> getP() {
        return P;
    }

    public String getS() {
        return S;
    }

    public Grammar(List<String> N, List<String> E, Map<String, List<String>> P, String S) {
        this.N = N;
        this.E = E;
        this.P = P;
        this.S = S;
    }

    public static boolean validate(Grammar g) {
        if (!contains(g.N, g.S)) {
            return false;
        }

        for (String key : g.P.keySet()) {
            if (!contains(g.N, key)) {
                return false;
            }

            for (String move : g.P.get(key)) {
                List<String> moveParts = List.of(move.trim().split(" "));
                for (String str : moveParts) {
                    if (!contains(g.N, str) && !contains(g.E, str) && !Objects.equals(str, "E")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean contains(List<String> array, String value) {
        for (String item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static boolean contains(List<String> array, char value) {
        for (String item : array) {
            if (item.length() == 1 && item.charAt(0) == value) {
                return true;
            }
        }
        return false;
    }

    public static List<String> parseLine(String line) {
        return List.of(line.substring(line.indexOf('{') + 1, line.length() - 1).trim().split(", *"));
    }

    public static Grammar fromFile(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            List<String> N = parseLine(br.readLine());
            List<String> E = parseLine(br.readLine());
            String S = br.readLine().split("=")[1].strip();
            List<String> rules = br.lines().toList();
            Map<String, List<String>> P = parseRules(rules.subList(1, rules.size() - 1));

            return new Grammar(N, E, P, S);
        }
    }

    public static Map<String, List<String>> parseRules(List<String> rules) {
        Map<String, List<String>> result = new HashMap<>();
        int index = 1;

        for (String rule : rules) {
            List<String> parts = List.of(rule.split("->"));
            String lhs = parts.get(0).strip();
            List<String> rhs = List.of(parts.get(1).substring(0, parts.get(1).indexOf(",")).trim().split("\\|"));

            if (result.containsKey(lhs)) {
                List<String> existingRhs = result.get(lhs);
                List<String> combinedRhs = new ArrayList<>(existingRhs.size() + rhs.size());
                System.arraycopy(existingRhs, 0, combinedRhs, 0, existingRhs.size());
                System.arraycopy(rhs, 0, combinedRhs, existingRhs.size(), rhs.size());
                result.put(lhs, combinedRhs);
            } else {
                result.put(lhs, rhs);
            }

            index++;
        }

        return result;
    }

    public List<String> splitRhs(String prod) {
        return List.of(prod.trim().split(" "));
    }

    public boolean isNonTerminal(String value) {
        return contains(N, value);
    }

    public boolean isTerminal(String value) {
        return contains(E, value);
    }

    public List<String> getProductionsFor(String nonTerminal) {
        if (!isNonTerminal(nonTerminal)) {
            throw new RuntimeException("Can only show productions for non-terminals");
        }

        return P.get(nonTerminal);
    }

    public String getProductionForIndex(String index) {
        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
            for (String v : entry.getValue()) {
                if (Objects.equals(v, index)) {
                    return entry.getKey() + " -> " + v;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("N = { ").append(String.join(", ", N)).append(" }\n");
        result.append("E = { ").append(String.join(", ", E)).append(" }\n");
        result.append("P = { ").append(String.join(", ", P.entrySet().stream()
                .map(entry -> entry.getKey() + " -> " + String.join(" | ", entry.getValue()))
                .toArray(String[]::new))).append(" }\n");
        result.append("S = ").append(S).append("\n");
        return result.toString();
    }
}

