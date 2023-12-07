package parser;

import java.util.*;

public class Parser {

    private Grammar grammar;
    private Map<String, Set<String>> firstSet;
    private Map<String, Set<String>> followSet;

    public Parser(Grammar grammar) {
        this.grammar = grammar;
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();
        for (String nonTerminal : grammar.getN()) {
            firstSet.put(nonTerminal, new HashSet<>());
            followSet.put(nonTerminal, new HashSet<>());
        }
        generateFirst();
        generateFollow();
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
}
