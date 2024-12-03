/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter;

import java.util.ArrayList;
import java.util.List;

public class DocumentTreeNode<DocumentType>
implements Cloneable {
    private String _text;
    private int _lvl;
    private final List<DocumentTreeNode<DocumentType>> _children;
    private DocumentTreeNode _parent;
    private DocumentType _oldPage;

    public DocumentTreeNode(String text, int lvl) {
        this._lvl = lvl;
        this._text = text;
        this._children = new ArrayList<DocumentTreeNode<DocumentType>>();
    }

    public void addChild(DocumentTreeNode<DocumentType> node) {
        this._children.add(node);
    }

    public DocumentTreeNode<DocumentType> get(int index) {
        return this._children.get(index);
    }

    public List<DocumentTreeNode<DocumentType>> getChildren() {
        return this._children;
    }

    public DocumentTreeNode<DocumentType> getParent() {
        return this._parent;
    }

    public void setParent(DocumentTreeNode<DocumentType> parent) {
        this._parent = parent;
    }

    public int getSize() {
        return this._children.size();
    }

    public String getText() {
        return this._text;
    }

    public int getLvl() {
        return this._lvl;
    }

    public void setLvl(int lvl) {
        this._lvl = lvl;
    }

    public void setText(String text) {
        this._text = text;
    }

    public void setOldPage(DocumentType existingPage) {
        this._oldPage = existingPage;
    }

    public DocumentType getOldPage() {
        return this._oldPage;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

