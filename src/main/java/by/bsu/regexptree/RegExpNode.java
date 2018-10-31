package by.bsu.regexptree;

import by.bsu.nsm.NSM;

public abstract class RegExpNode {

    RegExpNode next;

    public abstract NSM transform();

    public RegExpNode getNext() {
        return next;
    }

    public void setNext(RegExpNode next) {
        this.next = next;
    }
}
