package fa;

public class Transition {
    private final String from;
    private final String to;
    private final String label;

    public Transition(String from, String to, String label) {
        this.from = from;
        this.to = to;
        this.label = label;
    }


    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "{(" + this.from + "->" + this.to + ")," + this.label + "}";
    }
}
