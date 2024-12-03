/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath;

import javax.xml.namespace.QName;

class XPathStep {
    final boolean _attr;
    final boolean _deep;
    int _flags;
    final QName _name;
    XPathStep _next;
    XPathStep _prev;
    boolean _hasBacktrack;
    XPathStep _backtrack;

    XPathStep(boolean deep, boolean attr, QName name) {
        this._name = name;
        this._deep = deep;
        this._attr = attr;
        int flags = 0;
        if (this._deep || !this._attr) {
            flags |= 2;
        }
        if (this._attr) {
            flags |= 4;
        }
        this._flags = flags;
    }

    boolean isWild() {
        return this._name.getLocalPart().length() == 0;
    }

    boolean match(QName name) {
        String local = this._name.getLocalPart();
        String nameLocal = name.getLocalPart();
        int localLength = local.length();
        if (localLength == 0) {
            String uri = this._name.getNamespaceURI();
            return uri.isEmpty() || uri.equals(name.getNamespaceURI());
        }
        if (localLength != nameLocal.length()) {
            return false;
        }
        String uri = this._name.getNamespaceURI();
        String nameUri = name.getNamespaceURI();
        if (uri.length() != nameUri.length()) {
            return false;
        }
        return local.equals(nameLocal) && uri.equals(nameUri);
    }
}

