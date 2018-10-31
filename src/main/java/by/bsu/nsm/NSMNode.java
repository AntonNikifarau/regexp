package by.bsu.nsm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Node of Non-deterministic State Machine
 */
public class NSMNode {

    private static int counter = 0;
    private int idx;
    private Map<Character, List<NSMNode>> next;

    private NSMNode(int idx, Map<Character, List<NSMNode>> next) {
        this.idx = idx;
        this.next = next;
    }

    public Map<Character, List<NSMNode>> getNext() {
        return next;
    }

    static NSMNode n() {
        return new NSMNode(++counter, new HashMap<>());
    }

    static NSMNode n(Map<Character, List<NSMNode>> next) {
        return new NSMNode(++counter, next);
    }
}
