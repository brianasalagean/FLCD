package fa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FA {
    private List<String> states;
    private List<String> alphabet;
    private String initialState;
    private List<String> finalStates;
    private List<Transition> transitions;
    private final String filename;

    public FA(String filename) {
        this.filename = filename;
        this.states = new ArrayList<>();
        this.alphabet = new ArrayList<>();
        this.initialState = "";
        this.finalStates = new ArrayList<>();
        this.transitions = new ArrayList<>();
        try {
            readFromFile();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void readFromFile() throws Exception {
        var regex = Pattern.compile("^([a-z_]*)=");
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while((line = br.readLine()) != null) {
            var matcher = regex.matcher(line);
            var match = matcher.find();
            switch (matcher.group(0)) {
                case "states=" ->
                        this.states = List.of(line.substring(line.indexOf('{') + 1, line.length() - 1).trim().split(", *"));
                case "initial_state=" -> this.initialState = line.substring(line.indexOf("=") + 1).trim();
                case "alphabet=" ->
                        this.alphabet = List.of(line.substring(line.indexOf('{') + 1, line.length() - 1).trim().split(", *"));
                case "final_states=" ->
                        this.finalStates = List.of(line.substring(line.indexOf('{') + 1, line.length() - 1).trim().split(", *"));
                case "transitions=" -> {
                    List<String> transitionsList = List.of(line.substring(line.indexOf('{') + 1, line.length() - 1).trim().split("; *"));
                    for (String transition : transitionsList) {
                        List<String> transitionAttributes = List.of(transition.substring(1, transition.length() - 1).trim().split(", *"));
                        this.transitions.add(new Transition(transitionAttributes.get(0), transitionAttributes.get(1), transitionAttributes.get(2)));
                    }
                }
                default -> throw new Exception("Invalid line");
            }
        }
    }

    public void printStates() {
        System.out.println(states);
    }

    public void printAlphabet() {
        System.out.println(alphabet);
    }

    public void printInitialState() {
        System.out.println(initialState);
    }

    public void printFinalStates() {
        System.out.println(finalStates);
    }

    public void printTransitions() {
        System.out.println(transitions);
    }

    public boolean checkIfAccepted(String sequence) {
        List<String> sequenceCharacters = List.of(sequence.split(""));
        String currentState = initialState;

        for (String ch: sequenceCharacters) {
            boolean accepted = false;
            for (Transition t: transitions) {
                if (t.getFrom().equals(currentState) && t.getLabel().equals(ch)) {
                    currentState = t.getTo();
                    accepted = true;
                    break;
                }
            }
            if (!accepted) {
                return false;
            }
        }

        return finalStates.contains(currentState);
    }

    public String getNextAccepted(String sequence) {
        List<String> sequenceCharacters = List.of(sequence.split(""));
        String currentState = initialState;
        StringBuilder sb = new StringBuilder();

        for (String ch: sequenceCharacters) {
            String state = null;
            for (Transition t: transitions) {
                if (t.getFrom().equals(currentState) && t.getLabel().equals(ch)) {
                    state = t.getTo();
                    sb.append(ch);
                    break;
                }
            }

            if (state == null) {
                if (!finalStates.contains(currentState)) {
                    return null;
                }
                return sb.toString();
            }

            currentState = state;
        }

        return sb.toString();
    }
}