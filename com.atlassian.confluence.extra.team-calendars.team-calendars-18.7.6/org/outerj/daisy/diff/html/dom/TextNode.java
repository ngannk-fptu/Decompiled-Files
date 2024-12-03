/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom;

import java.util.ArrayList;
import java.util.List;
import org.outerj.daisy.diff.html.dom.Node;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.modification.Modification;
import org.outerj.daisy.diff.html.modification.ModificationType;

public class TextNode
extends Node
implements Cloneable {
    private String s;
    private Modification modification = new Modification(ModificationType.NONE);

    public TextNode(TagNode parent, String s) {
        super(parent);
        this.s = s;
    }

    @Override
    public Node copyTree() {
        try {
            Node node = (Node)this.clone();
            node.setParent(null);
            return node;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public Node getLeftMostChild() {
        return this;
    }

    @Override
    public List<Node> getMinimalDeletedSet(long id) {
        ArrayList<Node> nodes = new ArrayList<Node>(1);
        if (this.getModification().getType() == ModificationType.REMOVED && this.getModification().getID() == id) {
            nodes.add(this);
        }
        return nodes;
    }

    public Modification getModification() {
        return this.modification;
    }

    @Override
    public Node getRightMostChild() {
        return this;
    }

    public String getText() {
        return this.s;
    }

    public boolean isSameText(Object other) {
        TextNode otherTextNode;
        if (other == null) {
            return false;
        }
        try {
            otherTextNode = (TextNode)other;
        }
        catch (ClassCastException e) {
            return false;
        }
        return this.getText().replace('\n', ' ').equals(otherTextNode.getText().replace('\n', ' '));
    }

    public void setModification(Modification m) {
        this.modification = m;
    }

    public String toString() {
        return this.getText();
    }
}

