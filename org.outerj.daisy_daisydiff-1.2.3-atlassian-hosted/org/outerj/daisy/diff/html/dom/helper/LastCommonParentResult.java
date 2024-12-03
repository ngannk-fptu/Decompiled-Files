/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom.helper;

import org.outerj.daisy.diff.html.dom.TagNode;

public class LastCommonParentResult {
    private TagNode parent;
    private boolean splittingNeeded = false;
    private int lastCommonParentDepth = -1;
    private int indexInLastCommonParent = -1;

    public LastCommonParentResult() {
    }

    public LastCommonParentResult(TagNode parent) {
        this.parent = parent;
    }

    public TagNode getLastCommonParent() {
        return this.parent;
    }

    public void setLastCommonParent(TagNode parent) {
        this.parent = parent;
    }

    public boolean isSplittingNeeded() {
        return this.splittingNeeded;
    }

    public void setSplittingNeeded() {
        this.splittingNeeded = true;
    }

    public int getLastCommonParentDepth() {
        return this.lastCommonParentDepth;
    }

    public void setLastCommonParentDepth(int depth) {
        this.lastCommonParentDepth = depth;
    }

    public int getIndexInLastCommonParent() {
        return this.indexInLastCommonParent;
    }

    public void setIndexInLastCommonParent(int index) {
        this.indexInLastCommonParent = index;
    }
}

