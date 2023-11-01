package utils;

import model.Pair;

public class SymbolTable {
    private final HashTable hashTable;

    public SymbolTable(int size) {
        hashTable = new HashTable(size);
    }

    public Pair<Integer, String> add(String key) throws Exception {
        return hashTable.add(key);
    }

    public boolean contains(String key) {
        return hashTable.contains(key);
    }

    public Pair<Integer, Integer> getPosition(String key) throws Exception {
        return hashTable.getPosition(key);
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                hashTable +
                '}';
    }
}
