package by.bsu.regexptree;

import by.bsu.nsm.NSM;

import static by.bsu.nsm.NSM.branch;
import static by.bsu.nsm.NSM.concat;

public class Branch extends RegExpNode {

    private RegExpNode[] nodes;

    Branch(RegExpNode[] nodes) {
        this.nodes = nodes;
    }

    public RegExpNode[] getNodes() {
        return nodes;
    }

    @Override
    public NSM transform() {
        NSM[] nsms = new NSM[nodes.length];
        for (int i = 0; i < nsms.length; i++) {
            nsms[i] = nodes[i].transform();
        }
        NSM root = branch(nsms);
        if (getNext() != null) root = concat(root, getNext().transform());
        return root;
    }
}
