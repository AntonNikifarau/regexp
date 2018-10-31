package by.bsu.dsm;

import java.util.Set;

public class DSM {

    private DSMNode root;
    private Set<Character> dictionary;

    public DSM(DSMNode root, Set<Character> dictionary) {
        this.root = root;
        this.dictionary = dictionary;
    }

    public boolean match(String str) {
        if (str == null) return false;
        DSMNode node = root;
        for (char c : str.toCharArray()) {
            if (node.getNext().containsKey(c)) node = node.getNext().get(c);
            else return false;
        }
        return node.isTerminal();
    }
}
