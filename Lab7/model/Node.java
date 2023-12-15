package model;

public class Node {
    private String value;
    private Node child;
    private Node rightSibling;

    public Node(String value, Node child, Node rightSibling) {
        this.value = value;
        this.child = child;
        this.rightSibling = rightSibling;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Node getChild() {
        return child;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public Node getRightSibling() {
        return rightSibling;
    }

    public void setRightSibling(Node rightSibling) {
        this.rightSibling = rightSibling;
    }

    @Override
    public String toString() {
        return "(" + value + ", " + child + ", " + rightSibling + ")";
    }
}
