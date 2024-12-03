/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;

import java.util.Iterator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDDictionaryWrapper;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItemIterator;

public abstract class PDOutlineNode
extends PDDictionaryWrapper {
    public PDOutlineNode() {
    }

    public PDOutlineNode(COSDictionary dict) {
        super(dict);
    }

    PDOutlineNode getParent() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.PARENT);
        if (base instanceof COSDictionary) {
            COSDictionary parent = (COSDictionary)base;
            if (COSName.OUTLINES.equals(parent.getCOSName(COSName.TYPE))) {
                return new PDDocumentOutline(parent);
            }
            return new PDOutlineItem(parent);
        }
        return null;
    }

    void setParent(PDOutlineNode parent) {
        this.getCOSObject().setItem(COSName.PARENT, (COSObjectable)parent);
    }

    public void addLast(PDOutlineItem newChild) {
        this.requireSingleNode(newChild);
        this.append(newChild);
        this.updateParentOpenCountForAddedChild(newChild);
    }

    public void addFirst(PDOutlineItem newChild) {
        this.requireSingleNode(newChild);
        this.prepend(newChild);
        this.updateParentOpenCountForAddedChild(newChild);
    }

    void requireSingleNode(PDOutlineItem node) {
        if (node.getNextSibling() != null || node.getPreviousSibling() != null) {
            throw new IllegalArgumentException("A single node with no siblings is required");
        }
    }

    private void append(PDOutlineItem newChild) {
        newChild.setParent(this);
        if (!this.hasChildren()) {
            this.setFirstChild(newChild);
        } else {
            PDOutlineItem previousLastChild = this.getLastChild();
            previousLastChild.setNextSibling(newChild);
            newChild.setPreviousSibling(previousLastChild);
        }
        this.setLastChild(newChild);
    }

    private void prepend(PDOutlineItem newChild) {
        newChild.setParent(this);
        if (!this.hasChildren()) {
            this.setLastChild(newChild);
        } else {
            PDOutlineItem previousFirstChild = this.getFirstChild();
            newChild.setNextSibling(previousFirstChild);
            previousFirstChild.setPreviousSibling(newChild);
        }
        this.setFirstChild(newChild);
    }

    void updateParentOpenCountForAddedChild(PDOutlineItem newChild) {
        int delta = 1;
        if (newChild.isNodeOpen()) {
            delta += newChild.getOpenCount();
        }
        newChild.updateParentOpenCount(delta);
    }

    public boolean hasChildren() {
        return this.getCOSObject().getCOSDictionary(COSName.FIRST) != null;
    }

    PDOutlineItem getOutlineItem(COSName name) {
        COSBase base = this.getCOSObject().getDictionaryObject(name);
        if (base instanceof COSDictionary) {
            return new PDOutlineItem((COSDictionary)base);
        }
        return null;
    }

    public PDOutlineItem getFirstChild() {
        return this.getOutlineItem(COSName.FIRST);
    }

    void setFirstChild(PDOutlineNode outlineNode) {
        this.getCOSObject().setItem(COSName.FIRST, (COSObjectable)outlineNode);
    }

    public PDOutlineItem getLastChild() {
        return this.getOutlineItem(COSName.LAST);
    }

    void setLastChild(PDOutlineNode outlineNode) {
        this.getCOSObject().setItem(COSName.LAST, (COSObjectable)outlineNode);
    }

    public int getOpenCount() {
        return this.getCOSObject().getInt(COSName.COUNT, 0);
    }

    void setOpenCount(int openCount) {
        this.getCOSObject().setInt(COSName.COUNT, openCount);
    }

    public void openNode() {
        if (!this.isNodeOpen()) {
            this.switchNodeCount();
        }
    }

    public void closeNode() {
        if (this.isNodeOpen()) {
            this.switchNodeCount();
        }
    }

    private void switchNodeCount() {
        int openCount = this.getOpenCount();
        this.setOpenCount(-openCount);
        this.updateParentOpenCount(-openCount);
    }

    public boolean isNodeOpen() {
        return this.getOpenCount() > 0;
    }

    void updateParentOpenCount(int delta) {
        PDOutlineNode parent = this.getParent();
        if (parent != null) {
            if (parent.isNodeOpen()) {
                parent.setOpenCount(parent.getOpenCount() + delta);
                parent.updateParentOpenCount(delta);
            } else {
                parent.setOpenCount(parent.getOpenCount() - delta);
            }
        }
    }

    public Iterable<PDOutlineItem> children() {
        return new Iterable<PDOutlineItem>(){

            @Override
            public Iterator<PDOutlineItem> iterator() {
                return new PDOutlineItemIterator(PDOutlineNode.this.getFirstChild());
            }
        };
    }
}

