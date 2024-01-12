package parser;

import java.util.*;

public class Parser {

    private Grammar grammar;
    private Map<String, Set<String>> firstSet;
    private Map<String, Set<String>> followSet;
    private final Map<Map.Entry<String, String>, String> table;

    public Parser(Grammar grammar) {
        this.grammar = grammar;
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();
        this.table = new HashMap<>();
        for (String nonTerminal : grammar.getN()) {
            firstSet.put(nonTerminal, new HashSet<>());
            followSet.put(nonTerminal, new HashSet<>());
        }
        generateFirst();
        generateFollow();
        try {
            generateTable();
            System.out.println(this.table);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Map<Map.Entry<String, String>, String> getTable() {
        return table;
    }

    public Map<String, Set<String>> getFirstSet() {
        return firstSet;
    }

    public Map<String, Set<String>> getFollowSet() {
        return followSet;
    }

    private Set<String> innerLoop(Set<String> initialSet, List<String> items, Set<String> additionalSet) {
        Set<String> copySet = new HashSet<>(initialSet);
        for (int i = 0; i < items.size(); i++) {
            if (grammar.isNonTerminal(items.get(i))) {
                copySet.addAll(firstSet.get(items.get(i)));
                if (firstSet.get(items.get(i)).contains("E")) {
                    if (i < items.size() - 1) {
                        continue;
                    }
                    copySet.addAll(additionalSet);
                    break;
                } else {
                    break;
                }
            } else {
                copySet.add(items.get(i));
                break;
            }
        }
        return copySet;
    }

    private void generateFirst() {
        boolean isSetChanged;
        do {
            isSetChanged = false;
            for (Map.Entry<String, List<String>> entry : grammar.getP().entrySet()) {
                for (String v : entry.getValue()) {
                    List<String> rhs = grammar.splitRhs(v);
                    Set<String> copySet = new HashSet<>(firstSet.get(entry.getKey()));
                    copySet.addAll(innerLoop(copySet, rhs, Set.of("E")));

                    if (firstSet.get(entry.getKey()).size() != copySet.size()) {
                        firstSet.put(entry.getKey(), copySet);
                        isSetChanged = true;
                    }
                }
            }
        } while (isSetChanged);
    }

    private void generateFollow() {
        followSet.put(grammar.getS(), new HashSet<>(Set.of("$")));
        boolean isSetChanged;
        do {
            isSetChanged = false;
            for (Map.Entry<String, List<String>> entry : grammar.getP().entrySet()) {
                for (String v : entry.getValue()) {
                    List<String> rhs = grammar.splitRhs(v);
                    for (int i = 0; i < rhs.size(); i++) {
                        if (!grammar.isNonTerminal(rhs.get(i))) {
                            continue;
                        }
                        Set<String> copySet = new HashSet<>(followSet.get(rhs.get(i)));
                        if (i < rhs.size() - 1) {
                            copySet.addAll(innerLoop(copySet, rhs.subList(i + 1, rhs.size()), followSet.get(entry.getKey())));
                        } else {
                            copySet.addAll(followSet.get(entry.getKey()));
                        }

                        if (followSet.get(rhs.get(i)).size() != copySet.size()) {
                            followSet.put(rhs.get(i), copySet);
                            isSetChanged = true;
                        }
                    }
                }
            }
        } while (isSetChanged);
    }

    public void generateTable() throws Exception {
        List<String> terminals = this.grammar.getE();
        List<String> nonTerminals = this.grammar.getN();

        for (Map.Entry<String, List<String>> entry : this.grammar.getP().entrySet()) {
            String rowSymbol = entry.getKey();

            for (String v : entry.getValue()) {
                List<String> rule = this.grammar.splitRhs(v);

                List<String> combinedList = new ArrayList<>(terminals);
                combinedList.add("E");

                for (String columnSymbol : combinedList) {
                    //    Pair<String, String> pair = new Pair<>(rowSymbol, columnSymbol);

                    Map.Entry<String, String> pair = new AbstractMap.SimpleEntry<>(rowSymbol, columnSymbol);
                    if (rule.get(0).equals(columnSymbol) && !columnSymbol.equals("E")) {
                        this.table.put(pair, v);
                    }
                    else if (nonTerminals.contains(rule.get(0)) && this.firstSet.get(rule.get(0)).contains(columnSymbol)) {
                        if (!this.table.containsKey(pair)) {
                            this.table.put(pair, v);
                        }
                        else {
                            System.out.println(pair);
                            throw new Exception("Grammar is not LL(1).");
                        }
                    }
                    else {
                        if (rule.get(0).equals("E")) {
                            for (String b : this.followSet.get(rowSymbol)) {
                                if (b.equals("E")) {
                                    this.table.put(new AbstractMap.SimpleEntry<>(rowSymbol, "$"), v);
                                }
                                else { this.table.put(new AbstractMap.SimpleEntry<>(rowSymbol, b), v); }
                            }
                        }
                        else {
                            Set<String> firsts = new HashSet<>();
                            for (String symbol : this.grammar.getP().get(rowSymbol)) {
                                if (nonTerminals.contains(symbol)) {
                                    firsts.addAll(this.firstSet.get(symbol));
                                }
                            }
                            if (firsts.contains("E")) {
                                for (String b : this.followSet.get(rowSymbol)) {
                                    Map.Entry<String, String> newPair;
                                    if (b.equals("E")) {
                                        newPair = new AbstractMap.SimpleEntry<>(rowSymbol, "$");
                                    }
                                    else {
                                        newPair = new AbstractMap.SimpleEntry<>(rowSymbol, b);
                                    }
                                    if (!this.table.containsKey(newPair)) {
                                        this.table.put(newPair, v);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (String t : terminals) {
            this.table.put(new AbstractMap.SimpleEntry<>(t,t), "pop");
        }
        this.table.put(new AbstractMap.SimpleEntry<>("$", "$"), "acc");
    }

    public String evaluateSequence(List<String> sequence) {
        Stack<String> alpha = new Stack<>();
        Stack<String> beta = new Stack<>();
        StringBuilder result = new StringBuilder();

        alpha.push("$");

        for (int i = sequence.size() - 1; i >= 0; i--) {
            alpha.push(sequence.get(i));
        }

        beta.push("$");
        beta.push(grammar.getS());

        while (!(alpha.peek().equals("$") && beta.peek().equals("$"))) {
            String alphaPeek = alpha.peek();
            String betaPeek = beta.peek();

            Map.Entry<String, String> key = new AbstractMap.SimpleEntry<>(betaPeek, alphaPeek);
            String value = table.get(key);

            if (this.table.containsKey(key)) {
                if (value.equals("pop")) {
                    alpha.pop();
                    beta.pop();
                }
                else {
                    beta.pop();
                    if (!value.equals("E")) {
                        String[] val = value.split(" ");
                        for (int i = val.length - 1; i >=0 ; --i) {
                            beta.push(val[i]);
                        }
                    }
                    result.append(value).append(" ");
                }
            }
            else {
                System.out.println("Syntax error for key " + key);
                System.out.println("Current alpha and beta for sequence parsing:");
                System.out.println(alpha);
                System.out.println(beta);
                return null;
            }
        }
        return result.toString();
    }
}
