package parser;

import model.Node;
import model.TableEntry;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Tree {
    private Node root;
    private Grammar grammar;
    private int crt;
    private List<String> ws;
    private FileWriter fw;

    public Tree(Grammar grammar, String filename) {
        this.root = null;
        this.grammar = grammar;
        this.crt = 0;
        this.ws = new ArrayList<>();
        try {
            fw = new FileWriter(filename);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Node build(List<String> ws) {
        this.ws = ws;
        return buildTree(grammar.getS());
    }

    private Node buildTree(String currentSymbol) {
        if (Objects.equals(currentSymbol, "E") || ws.isEmpty()) {
            return null;
        }

        Node node = new Node(currentSymbol, null, null);
        if (this.root == null) {
            this.root = node;
        }

        if (ws.size() == 1 && ws.contains("E")) {
            return node;
        }

        if (grammar.isTerminal(currentSymbol)) {
            return node;
        }

        List<String> productions = grammar.getProductionsFor(currentSymbol);
        for (String production: productions) {
            List<String> symbols = List.of(production.trim().split(" "));
            List<Node> children = new ArrayList<>();
            boolean productionIsValid = true;
            int index = 0;
            for (String symbol: symbols) {
                if (!Objects.equals(symbol, ws.get(index))) {
                    productionIsValid = false;
                    break;
                }
                index += 1;
            }

            if (!productionIsValid) {
                continue;
            }
            else {
                ws = ws.subList(symbols.size(), ws.size());
            }

            for (String symbol: symbols) {
                Node child = buildTree(symbol);
                if (child == null) {
                    break;
                }

                if (node.getChild() == null) {
                    node.setChild(child);
                }
                children.add(child);
                if (children.size() > 1) {
                    children.get(children.size() - 2).setRightSibling(child);
                }
            }
        }
        return node;
    }

    public void printTable() {
        bfs(root, -1, -1);
        try {
            this.fw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<TableEntry> bfs(Node node, Integer fatherCrt, Integer rightSiblingCrt) {
        if (node == null) {
            return Collections.emptyList();
        }
        System.out.printf("%d | %s | %d | %d%n", crt, node.getValue(), fatherCrt, rightSiblingCrt);

        try {
            this.fw.write( crt + " | " + node.getValue() + " | " + fatherCrt + " | " + rightSiblingCrt + '\n');
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        int crt = this.crt;
        this.crt += 1;

        if (rightSiblingCrt != -1) {
            List<TableEntry> children = new ArrayList<>(List.of(new TableEntry(node.getChild(), crt, -1)));
            children.addAll(bfs(node.getRightSibling(), fatherCrt, crt));
            return children;
        } else {
            List<TableEntry> children = new ArrayList<>(List.of(new TableEntry(node.getChild(), crt, -1)));
            children.addAll(bfs(node.getRightSibling(), fatherCrt, crt));
            for (TableEntry child : children) {
                bfs((Node) child.getItem(), (Integer) child.getParentIndex(), (Integer) child.getRightSiblingIndex());
            }
            return Collections.emptyList();
        }
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Node node = this.root;
        while (node != null) {
            result.append(node);
            node = node.getRightSibling();
        }
        return result.toString();
    }
}

