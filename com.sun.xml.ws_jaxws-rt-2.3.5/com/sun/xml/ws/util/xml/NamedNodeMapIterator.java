/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util.xml;

import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapIterator
implements Iterator {
    protected NamedNodeMap _map;
    protected int _index;

    public NamedNodeMapIterator(NamedNodeMap map) {
        this._map = map;
        this._index = 0;
    }

    @Override
    public boolean hasNext() {
        if (this._map == null) {
            return false;
        }
        return this._index < this._map.getLength();
    }

    public Object next() {
        Node obj = this._map.item(this._index);
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

