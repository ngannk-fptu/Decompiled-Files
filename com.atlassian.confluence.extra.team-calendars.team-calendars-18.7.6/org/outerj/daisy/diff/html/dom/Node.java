/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.dom.helper.LastCommonParentResult;

public abstract class Node {
    protected TagNode parent;
    private TagNode root;
    private boolean whiteBefore = false;
    private boolean whiteAfter = false;

    public Node(TagNode parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
            this.root = parent.getRoot();
        } else if (this instanceof TagNode) {
            this.root = (TagNode)this;
        }
    }

    public TagNode getParent() {
        return this.parent;
    }

    public List<TagNode> getParentTree() {
        ArrayList<TagNode> ancestors = new ArrayList<TagNode>();
        for (TagNode ancestor = this.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            ancestors.add(ancestor);
        }
        Collections.reverse(ancestors);
        return ancestors;
    }

    public TagNode getRoot() {
        return this.root;
    }

    public abstract List<Node> getMinimalDeletedSet(long var1);

    public void detectIgnorableWhiteSpace() {
    }

    public LastCommonParentResult getLastCommonParent(Node other) {
        if (other == null) {
            throw new IllegalArgumentException("The given TextNode is null");
        }
        LastCommonParentResult result = new LastCommonParentResult();
        List<TagNode> myParents = this.getParentTree();
        List<TagNode> otherParents = other.getParentTree();
        int i = 1;
        boolean isSame = true;
        while (isSame && i < myParents.size() && i < otherParents.size()) {
            if (!myParents.get(i).isSameTag(otherParents.get(i))) {
                isSame = false;
                continue;
            }
            ++i;
        }
        result.setLastCommonParentDepth(i - 1);
        result.setLastCommonParent(myParents.get(i - 1));
        if (!isSame) {
            result.setIndexInLastCommonParent(myParents.get(i - 1).getIndexOf(myParents.get(i)));
            result.setSplittingNeeded();
        } else if (myParents.size() < otherParents.size()) {
            result.setIndexInLastCommonParent(myParents.get(i - 1).getIndexOf(this));
        } else if (myParents.size() > otherParents.size()) {
            result.setIndexInLastCommonParent(myParents.get(i - 1).getIndexOf(myParents.get(i)));
            result.setSplittingNeeded();
        } else {
            result.setIndexInLastCommonParent(myParents.get(i - 1).getIndexOf(this));
        }
        return result;
    }

    public void setParent(TagNode parent) {
        this.parent = parent;
        if (parent != null) {
            this.setRoot(parent.getRoot());
        }
    }

    protected void setRoot(TagNode root) {
        this.root = root;
    }

    public abstract Node copyTree();

    public boolean inPre() {
        for (TagNode ancestor : this.getParentTree()) {
            if (!ancestor.isPre()) continue;
            return true;
        }
        return false;
    }

    public boolean isWhiteBefore() {
        return this.whiteBefore;
    }

    public void setWhiteBefore(boolean whiteBefore) {
        this.whiteBefore = whiteBefore;
    }

    public boolean isWhiteAfter() {
        return this.whiteAfter;
    }

    public void setWhiteAfter(boolean whiteAfter) {
        this.whiteAfter = whiteAfter;
    }

    public abstract Node getLeftMostChild();

    public abstract Node getRightMostChild();
}

