package by.bsu.regexptree;

import by.bsu.nsm.NSM;

import static by.bsu.nsm.NSM.concat;
import static by.bsu.nsm.NSM.text;

public class Text extends RegExpNode {

    private String text;

    Text(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public NSM transform() {
        if (text == null || text.isEmpty()) return null;
        char[] chars = text.toCharArray();
        NSM root = text(chars[0]);
        if (chars.length > 1) {
            for (int i = 1; i < chars.length; i++) {
                char c = chars[i];
                root = concat(root, text(c));
            }
        }
        if (getNext() != null) root = concat(root, getNext().transform());
        return root;
    }

}
