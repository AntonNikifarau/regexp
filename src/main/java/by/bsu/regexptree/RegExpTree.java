package by.bsu.regexptree;

import by.bsu.nsm.NSM;

import java.util.Stack;

public class RegExpTree {
    private String regexp;
    private RegExpNode root;

    private RegExpTree(String regexp, RegExpNode root) {
        this.regexp = regexp;
        this.root = root;
    }

    public static RegExpTree parse(String regexp) {
        StringBuilder buffer = new StringBuilder();
        Stack<Stack<RegExpNode>> stack = new Stack<>();
        stack.push(new Stack<>());

        int br = 0;
        char[] arrChar = regexp.toCharArray();
        for (int i = 0; i < arrChar.length; ++i) {
            char c = arrChar[i];
            if (Character.isLetterOrDigit(c)) {
                buffer.append(c);
            } else if (')' == c) {
                if (i == 0) throw new IllegalStateException("RegExp can't start with )");
                char prevChar = arrChar[i - 1];
                if (!Character.isLetterOrDigit(prevChar) && ')' != prevChar && '*' != prevChar)
                    throw new IllegalStateException("Incorrect syntaxis for ), idx: " + i);
                --br;
                if (buffer.length() > 0) {
                    Text text = new Text(buffer.toString());
                    if (!stack.peek().isEmpty()) {
                        RegExpNode last = stack.peek().peek();
                        if (last instanceof Branch) {
                            Branch branch = (Branch) last;
                            branch.getNodes()[1] = text;
                        } else {
                            stack.peek().push(text);
                        }
                    } else {
                        stack.peek().push(text);
                    }
                    buffer = new StringBuilder();
                }
                RegExpNode node = linkNodes(stack.pop());
                if (!stack.peek().isEmpty()) {
                    RegExpNode last = stack.peek().peek();
                    if (last instanceof Branch) {
                        Branch branch = (Branch) last;
                        branch.getNodes()[1] = node;
                    } else {
                        stack.peek().push(node);
                    }
                } else {
                    stack.peek().push(node);
                }
            } else if ('(' == c) {
                ++br;
                if (buffer.length() > 0) {
                    stack.peek().push(new Text(buffer.toString()));
                    buffer = new StringBuilder();
                }
                stack.push(new Stack<>());
            } else if ('*' == c) {
                if (i == 0) throw new IllegalStateException("RegExp can't start with *");
                char prevChar = arrChar[i - 1];
                if (!Character.isLetterOrDigit(prevChar) && ')' != prevChar)
                    throw new IllegalStateException("Incorrect syntaxis for *, idx: " + i);
                if (buffer.length() > 0) {
                    Loop loop = new Loop(new Text(buffer.toString()));
                    stack.peek().push(loop);
                    buffer = new StringBuilder();
                } else if (!stack.peek().isEmpty()) {
                    Loop loop = new Loop(stack.peek().pop());
                    stack.peek().push(loop);
                }
            } else if ('|' == c) {
                if (i == 0) throw new IllegalStateException("RegExp can't start with |");
                char prevChar = arrChar[i - 1];
                if (!Character.isLetterOrDigit(prevChar) && ')' != prevChar && '*' != prevChar)
                    throw new IllegalStateException("Incorrect syntaxis for |, idx: " + i);
                if (buffer.length() > 0) {
                    RegExpNode[] nodes = new RegExpNode[2];
                    nodes[0] = new Text(buffer.toString());
                    stack.peek().push(new Branch(nodes));
                    buffer = new StringBuilder();
                } else if (!stack.peek().isEmpty()) {
                    RegExpNode[] nodes = new RegExpNode[2];
                    nodes[0] = stack.peek().pop();
                    stack.peek().push(new Branch(nodes));
                }
            } else throw new IllegalStateException("Unsupported symbol: " + c);
        }

        if (br != 0) throw new IllegalStateException("Parentheses mismatch");

        if (buffer.length() > 0) stack.peek().push(new Text(buffer.toString()));
        // stack.peek().push(new Finish());

        RegExpNode root = linkNodes(stack.pop());
        // root = removeGroups(root);

        return new RegExpTree(regexp, root);
    }


    private static Group linkNodes(Stack<RegExpNode> stack) {
        if (stack.size() == 1) {
            return new Group(stack.pop());
        } else if (stack.size() > 1) {
            RegExpNode last = stack.pop();
            RegExpNode prev = stack.pop();
            prev.next = last;
            while (!stack.isEmpty()) {
                last = prev;
                prev = stack.pop();
                prev.next = last;
            }
            return new Group(prev);
        }
        return new Group(null);
    }

    private static RegExpNode removeGroups(RegExpNode node) {
        if (node == null) return null;
        RegExpNode root;
        if (node instanceof Group) {
            root = ((Group) node).getBase();
        } else {
            root = node;
        }

        if (root instanceof Group) {
            Group group = (Group) root;
            root = removeGroups(group);
        } else if (root instanceof Loop) {
            Loop loop = (Loop) root;
            loop.setBase(removeGroups(loop.getBase()));
        } else if (root instanceof Branch) {
            Branch branch = (Branch) root;
            for (int i = 0; i < branch.getNodes().length; ++i) {
                branch.getNodes()[i] = removeGroups(branch.getNodes()[i]);
            }
        }

        root.next = removeGroups(root.next);
        return root;
    }

    public NSM toNSM() {
        return root.transform();
    }

    @Override
    public String toString() {
        return regexp;
    }
}
