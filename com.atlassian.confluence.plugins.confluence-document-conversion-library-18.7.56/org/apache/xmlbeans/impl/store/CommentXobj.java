/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.NodeXobj;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class CommentXobj
extends NodeXobj
implements Comment {
    CommentXobj(Locale l) {
        super(l, 4, 8);
    }

    @Override
    Xobj newNode(Locale l) {
        return new CommentXobj(l);
    }

    @Override
    public NodeList getChildNodes() {
        return DomImpl._emptyNodeList;
    }

    @Override
    public void appendData(String arg) {
        DomImpl._characterData_appendData(this, arg);
    }

    @Override
    public void deleteData(int offset, int count) {
        DomImpl._characterData_deleteData(this, offset, count);
    }

    @Override
    public String getData() {
        return DomImpl._characterData_getData(this);
    }

    @Override
    public int getLength() {
        return DomImpl._characterData_getLength(this);
    }

    @Override
    public Node getFirstChild() {
        return null;
    }

    @Override
    public void insertData(int offset, String arg) {
        DomImpl._characterData_insertData(this, offset, arg);
    }

    @Override
    public void replaceData(int offset, int count, String arg) {
        DomImpl._characterData_replaceData(this, offset, count, arg);
    }

    @Override
    public void setData(String data) {
        DomImpl._characterData_setData(this, data);
    }

    @Override
    public String substringData(int offset, int count) {
        return DomImpl._characterData_substringData(this, offset, count);
    }
}

