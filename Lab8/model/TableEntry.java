package model;

public class TableEntry {
    private Node item;
    private int parentIndex;
    private int rightSiblingIndex;

    public TableEntry(Node item, int parentIndex, int rightSiblingIndex) {
        this.item = item;
        this.parentIndex = parentIndex;
        this.rightSiblingIndex = rightSiblingIndex;
    }

    public Node getItem() {
        return item;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public int getRightSiblingIndex() {
        return rightSiblingIndex;
    }
}
