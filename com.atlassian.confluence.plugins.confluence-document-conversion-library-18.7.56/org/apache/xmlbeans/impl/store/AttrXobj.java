/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.NamedNodeXobj;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

class AttrXobj
extends NamedNodeXobj
implements Attr {
    AttrXobj(Locale l, QName name) {
        super(l, 3, 2);
        this._name = name;
    }

    @Override
    Xobj newNode(Locale l) {
        return new AttrXobj(l, this._name);
    }

    @Override
    public Node getNextSibling() {
        return null;
    }

    @Override
    public String getName() {
        return DomImpl._node_getNodeName(this);
    }

    @Override
    public Element getOwnerElement() {
        return DomImpl._attr_getOwnerElement(this);
    }

    @Override
    public boolean getSpecified() {
        return DomImpl._attr_getSpecified(this);
    }

    @Override
    public String getValue() {
        return DomImpl._node_getNodeValue(this);
    }

    @Override
    public void setValue(String value) {
        DomImpl._node_setNodeValue(this, value);
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public boolean isId() {
        return false;
    }
}

