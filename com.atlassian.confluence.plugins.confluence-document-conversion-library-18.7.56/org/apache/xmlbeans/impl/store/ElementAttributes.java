/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.ElementXobj;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class ElementAttributes
implements NamedNodeMap {
    private ElementXobj _elementXobj;

    ElementAttributes(ElementXobj elementXobj) {
        this._elementXobj = elementXobj;
    }

    @Override
    public int getLength() {
        return DomImpl._attributes_getLength(this._elementXobj);
    }

    @Override
    public Node getNamedItem(String name) {
        return DomImpl._attributes_getNamedItem(this._elementXobj, name);
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) {
        return DomImpl._attributes_getNamedItemNS(this._elementXobj, namespaceURI, localName);
    }

    @Override
    public Node item(int index) {
        return DomImpl._attributes_item(this._elementXobj, index);
    }

    @Override
    public Node removeNamedItem(String name) {
        return DomImpl._attributes_removeNamedItem(this._elementXobj, name);
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String localName) {
        return DomImpl._attributes_removeNamedItemNS(this._elementXobj, namespaceURI, localName);
    }

    @Override
    public Node setNamedItem(Node arg) {
        return DomImpl._attributes_setNamedItem(this._elementXobj, arg);
    }

    @Override
    public Node setNamedItemNS(Node arg) {
        return DomImpl._attributes_setNamedItemNS(this._elementXobj, arg);
    }
}

