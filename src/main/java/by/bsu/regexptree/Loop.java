package by.bsu.regexptree;

import by.bsu.nsm.NSM;

import static by.bsu.nsm.NSM.concat;
import static by.bsu.nsm.NSM.loop;

public class Loop extends RegExpNode {

    private RegExpNode base;

    Loop(RegExpNode base) {
        this.base = base;
    }

    public RegExpNode getBase() {
        return base;
    }

    public void setBase(RegExpNode base) {
        this.base = base;
    }

    @Override
    public NSM transform() {
        NSM root = loop(base.transform());
        if (getNext() != null) root = concat(root, getNext().transform());
        return root;
    }
}
