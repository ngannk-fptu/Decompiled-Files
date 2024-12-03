/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.tidy.AttVal;

public class DOMAttrMapImpl
implements NamedNodeMap {
    private AttVal first;

    protected DOMAttrMapImpl(AttVal firstAttVal) {
        this.first = firstAttVal;
    }

    public Node getNamedItem(String name) {
        AttVal att = this.first;
        while (att != null && !att.attribute.equals(name)) {
            att = att.next;
        }
        if (att != null) {
            return att.getAdapter();
        }
        return null;
    }

    public Node item(int index) {
        AttVal att = this.first;
        for (int i = 0; att != null && i < index; ++i) {
            att = att.next;
        }
        if (att != null) {
            return att.getAdapter();
        }
        return null;
    }

    public int getLength() {
        int len = 0;
        AttVal att = this.first;
        while (att != null) {
            ++len;
            att = att.next;
        }
        return len;
    }

    public Node setNamedItem(Node arg) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public Node removeNamedItem(String name) throws DOMException {
        AttVal att = this.first;
        AttVal previous = null;
        while (att != null) {
            if (att.attribute.equals(name)) {
                if (previous == null) {
                    this.first = att.getNext();
                    break;
                }
                previous.setNext(att.getNext());
                break;
            }
            previous = att;
            att = att.next;
        }
        if (att != null) {
            return att.getAdapter();
        }
        throw new DOMException(8, "Named item " + name + "Not found");
    }

    public Node getNamedItemNS(String namespaceURI, String localName) {
        throw new DOMException(9, "DOM method not supported");
    }

    public Node setNamedItemNS(Node arg) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }
}

