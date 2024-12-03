/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util.xml;

import java.util.Iterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListIterator
implements Iterator {
    protected NodeList _list;
    protected int _index;

    public NodeListIterator(NodeList list) {
        this._list = list;
        this._index = 0;
    }

    @Override
    public boolean hasNext() {
        if (this._list == null) {
            return false;
        }
        return this._index < this._list.getLength();
    }

    public Object next() {
        Node obj = this._list.item(this._index);
        if (obj != null) {
            ++this._index;
        }
        return obj;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

