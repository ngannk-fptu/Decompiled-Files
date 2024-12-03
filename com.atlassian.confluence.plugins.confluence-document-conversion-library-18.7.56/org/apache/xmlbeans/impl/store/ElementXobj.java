/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.ElementAttributes;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.NamedNodeXobj;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

class ElementXobj
extends NamedNodeXobj
implements Element {
    private ElementAttributes _attributes;

    ElementXobj(Locale l, QName name) {
        super(l, 2, 1);
        this._name = name;
    }

    @Override
    Xobj newNode(Locale l) {
        return new ElementXobj(l, this._name);
    }

    @Override
    public NamedNodeMap getAttributes() {
        if (this._attributes == null) {
            this._attributes = new ElementAttributes(this);
        }
        return this._attributes;
    }

    @Override
    public String getAttribute(String name) {
        return DomImpl._element_getAttribute(this, name);
    }

    @Override
    public Attr getAttributeNode(String name) {
        return DomImpl._element_getAttributeNode(this, name);
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        return DomImpl._element_getAttributeNodeNS(this, namespaceURI, localName);
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) {
        return DomImpl._element_getAttributeNS(this, namespaceURI, localName);
    }

    @Override
    public NodeList getElementsByTagName(String name) {
        return DomImpl._element_getElementsByTagName(this, name);
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return DomImpl._element_getElementsByTagNameNS(this, namespaceURI, localName);
    }

    @Override
    public String getTagName() {
        return DomImpl._element_getTagName(this);
    }

    @Override
    public boolean hasAttribute(String name) {
        return DomImpl._element_hasAttribute(this, name);
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return DomImpl._element_hasAttributeNS(this, namespaceURI, localName);
    }

    @Override
    public void removeAttribute(String name) {
        DomImpl._element_removeAttribute(this, name);
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) {
        return DomImpl._element_removeAttributeNode(this, oldAttr);
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName) {
        DomImpl._element_removeAttributeNS(this, namespaceURI, localName);
    }

    @Override
    public void setAttribute(String name, String value) {
        DomImpl._element_setAttribute(this, name, value);
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) {
        return DomImpl._element_setAttributeNode(this, newAttr);
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) {
        return DomImpl._element_setAttributeNodeNS(this, newAttr);
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) {
        DomImpl._element_setAttributeNS(this, namespaceURI, qualifiedName, value);
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setIdAttribute(String name, boolean isId) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }
}

