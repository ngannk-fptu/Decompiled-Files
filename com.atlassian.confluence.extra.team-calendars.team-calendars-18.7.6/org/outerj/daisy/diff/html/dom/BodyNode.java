/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom;

import java.util.ArrayList;
import java.util.List;
import org.outerj.daisy.diff.html.dom.Node;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.xml.sax.helpers.AttributesImpl;

public class BodyNode
extends TagNode {
    public BodyNode() {
        super(null, "body", new AttributesImpl());
    }

    @Override
    public Node copyTree() {
        BodyNode newThis = new BodyNode();
        for (Node child : this) {
            Node newChild = child.copyTree();
            newChild.setParent(newThis);
            newThis.addChild(newChild);
        }
        return newThis;
    }

    @Override
    public List<Node> getMinimalDeletedSet(long id) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        for (Node child : this) {
            List<Node> childrenChildren = child.getMinimalDeletedSet(id);
            nodes.addAll(childrenChildren);
        }
        return nodes;
    }
}

