package by.bsu.dsm;

import java.util.HashMap;
import java.util.Map;

public class DSMNode {

    private static int counter = 0;
    private Map<Character, DSMNode> next = new HashMap<>();
    private int idx;
    private boolean terminal;

    private DSMNode(int idx) {
        this.idx = idx;
    }

    public static DSMNode dsmNode() {
        return new DSMNode(++counter);
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public Map<Character, DSMNode> getNext() {
        return next;
    }

    public void setNext(Map<Character, DSMNode> next) {
        this.next = next;
    }
}
