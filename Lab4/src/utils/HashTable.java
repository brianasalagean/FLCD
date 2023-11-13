package utils;

import model.Pair;

import java.util.ArrayList;

public class HashTable {
    private final ArrayList<ArrayList<String>> elements;
    private final int size;

    public HashTable(int size) {
        this.size = size;
        this.elements = new ArrayList<>();
        for (int index = 0; index < this.size; index += 1) {
            this.elements.add(new ArrayList<>());
        }
    }

    public int hash(String key) {
        int nrFromStr = 0;

        for (int index = 0; index < key.length(); index += 1) {
            nrFromStr += key.charAt(index);
        }

        return nrFromStr % size;
    }

    public Pair<Integer, String> add(String key) throws Exception {
        if (contains(key))
            throw new Exception("key already exists");
        elements.get(hash(key)).add(key);
        return new Pair<>(hash(key), key);
    }

    public Pair<Integer, Integer> getPosition(String key) throws Exception {
        if (!contains(key))
            throw new Exception("key doesn't exist");
        return new Pair<>(hash(key), elements.get(hash(key)).indexOf(key));
    }

    public boolean contains(String key) {
        return elements.get(hash(key)).contains(key);
    }

    @Override
    public String toString() {
        return "HashTable{" + "items=" + elements + '}';
    }
}
