package by.bsu.nsm;

import by.bsu.dsm.DSM;
import by.bsu.dsm.DSMNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static by.bsu.nsm.NSMNode.n;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

public class NSM {

    private Set<Character> dictionary = new HashSet<>();
    private NSMNode start;
    private NSMNode end;

    private NSM(NSMNode start, NSMNode end) {
        this.start = start;
        this.end = end;
    }

    public static NSM text(Character c) {
        NSMNode finish = n();
        NSMNode start = n();
        start.getNext().put(c, new LinkedList<>());
        start.getNext().get(c).add(finish);
        NSM nsm = new NSM(start, finish);
        nsm.dictionary.add(c);
        return nsm;
    }

    public static NSM concat(NSM... nsms) {
        if (nsms.length < 1) return null;
        if (nsms.length == 1) return nsms[0];

        for (int i = 1; i < nsms.length; i++) {
            NSMNode a = nsms[i - 1].end;
            NSMNode b = nsms[i].start;
            a.getNext().putAll(b.getNext());
            nsms[0].dictionary.addAll(nsms[i].dictionary);
        }
        NSM nsm = new NSM(nsms[0].start, nsms[nsms.length - 1].end);
        nsm.dictionary = nsms[0].dictionary;
        return nsm;
    }

    public static NSM loop(NSM a) {
        NSMNode start = a.start;
        NSMNode end = a.end;

        start.getNext().putIfAbsent('-', new LinkedList<>());
        start.getNext().get('-').add(end);

        end.getNext().putIfAbsent('-', new LinkedList<>());
        end.getNext().get('-').add(start);

        return a;
    }

    public static NSM branch(NSM[] nsms) {
        if (nsms.length < 1) return null;
        if (nsms.length == 1) return nsms[0];
        NSMNode start = n();
        NSMNode end = n();
        start.getNext().put('-', new LinkedList<>());
        Set<Character> dictionary = new HashSet<>();
        for (NSM nsm : nsms) {
            start.getNext().get('-').add(nsm.start);
            nsm.end.getNext().putIfAbsent('-', new LinkedList<>());
            nsm.end.getNext().get('-').add(end);
            dictionary.addAll(nsm.dictionary);
        }
        NSM nsm = new NSM(start, end);
        nsm.dictionary = dictionary;
        return nsm;
    }

    private static Set<NSMNode> bar(NSMNode root) {
        Set<NSMNode> set = new HashSet<>();
        set.add(root);
        Stack<NSMNode> stack = new Stack<>();

        for (NSMNode node : root.getNext().getOrDefault('-', new LinkedList<>())) {
            if (!set.contains(node)) {
                set.add(node);
                stack.push(node);
            }
        }

        while (!stack.isEmpty()) {
            NSMNode node = stack.pop();
            for (NSMNode n : node.getNext().getOrDefault('-', new LinkedList<>())) {
                if (!set.contains(n)) {
                    set.add(n);
                    stack.push(n);
                }
            }
        }
        return set;
    }

    private static Set<NSMNode> bar(Set<NSMNode> nodes) {
        return nodes.stream().map(NSM::bar).flatMap(Set::stream).collect(toSet());
    }

    private static Set<NSMNode> bar(Set<NSMNode> nodes, Character c) {
        return nodes.stream().map(n -> n.getNext().getOrDefault(c, emptyList())).flatMap(List::stream).collect(toSet());
    }

    public DSM toDSM() {
        HashMap<Set<NSMNode>, DSMNode> map = new HashMap<>();
        Stack<DSMNode> stack = new Stack<>();
        DSMNode root = DSMNode.dsmNode();
        stack.push(root);
        Set<NSMNode> s1 = bar(start);
        map.put(s1, stack.peek());
        if (s1.contains(end)) root.setTerminal(true);

        while (!stack.isEmpty()) {
            DSMNode dsmNode = stack.pop();
            for (Character c : dictionary) {
                Set<NSMNode> s2 = get(map, dsmNode);
                Set<NSMNode> s3 = bar(s2, c);
                Set<NSMNode> s4 = bar(s3);
                Set<NSMNode> set = new HashSet<>(s4);
                if (!map.containsKey(set)) {
                    DSMNode newNode = DSMNode.dsmNode();
                    stack.push(newNode);
                    map.put(set, newNode);
                    if (set.contains(end)) newNode.setTerminal(true);
                }
                DSMNode n1 = map.get(set);
                dsmNode.getNext().put(c, n1);
            }
        }

        return new DSM(root, dictionary);
    }

    private Set<NSMNode> get(Map<Set<NSMNode>, DSMNode> map, DSMNode node) {
        return map.entrySet().stream().filter(e -> e.getValue() == node).findFirst().get().getKey();
    }
}
