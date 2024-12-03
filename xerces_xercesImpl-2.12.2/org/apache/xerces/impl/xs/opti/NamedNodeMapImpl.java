/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl
implements NamedNodeMap {
    Attr[] attrs;

    public NamedNodeMapImpl(Attr[] attrArray) {
        this.attrs = attrArray;
    }

    @Override
    public Node getNamedItem(String string) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (!this.attrs[i].getName().equals(string)) continue;
            return this.attrs[i];
        }
        return null;
    }

    @Override
    public Node item(int n) {
        if (n < 0 && n > this.getLength()) {
            return null;
        }
        return this.attrs[n];
    }

    @Override
    public int getLength() {
        return this.attrs.length;
    }

    @Override
    public Node getNamedItemNS(String string, String string2) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (!this.attrs[i].getName().equals(string2) || !this.attrs[i].getNamespaceURI().equals(string)) continue;
            return this.attrs[i];
        }
        return null;
    }

    @Override
    public Node setNamedItemNS(Node node) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Node setNamedItem(Node node) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Node removeNamedItem(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Node removeNamedItemNS(String string, String string2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }
}

