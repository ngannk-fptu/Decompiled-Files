/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.tidy.AttVal;
import org.w3c.tidy.AttributeTable;
import org.w3c.tidy.DOMAttrImpl;
import org.w3c.tidy.DOMNodeImpl;
import org.w3c.tidy.DOMNodeListByTagNameImpl;
import org.w3c.tidy.Node;

public class DOMElementImpl
extends DOMNodeImpl
implements Element {
    protected DOMElementImpl(Node adaptee) {
        super(adaptee);
    }

    public short getNodeType() {
        return 1;
    }

    public String getTagName() {
        return super.getNodeName();
    }

    public String getAttribute(String name) {
        if (this.adaptee == null) {
            return null;
        }
        AttVal att = this.adaptee.attributes;
        while (att != null && !att.attribute.equals(name)) {
            att = att.next;
        }
        if (att != null) {
            return att.value;
        }
        return "";
    }

    public void setAttribute(String name, String value) throws DOMException {
        if (this.adaptee == null) {
            return;
        }
        AttVal att = this.adaptee.attributes;
        while (att != null && !att.attribute.equals(name)) {
            att = att.next;
        }
        if (att != null) {
            att.value = value;
        } else {
            att = new AttVal(null, null, 34, name, value);
            att.dict = AttributeTable.getDefaultAttributeTable().findAttribute(att);
            if (this.adaptee.attributes == null) {
                this.adaptee.attributes = att;
            } else {
                att.next = this.adaptee.attributes;
                this.adaptee.attributes = att;
            }
        }
    }

    public void removeAttribute(String name) throws DOMException {
        if (this.adaptee == null) {
            return;
        }
        AttVal att = this.adaptee.attributes;
        AttVal pre = null;
        while (att != null && !att.attribute.equals(name)) {
            pre = att;
            att = att.next;
        }
        if (att != null) {
            if (pre == null) {
                this.adaptee.attributes = att.next;
            } else {
                pre.next = att.next;
            }
        }
    }

    public Attr getAttributeNode(String name) {
        if (this.adaptee == null) {
            return null;
        }
        AttVal att = this.adaptee.attributes;
        while (att != null && !att.attribute.equals(name)) {
            att = att.next;
        }
        if (att != null) {
            return att.getAdapter();
        }
        return null;
    }

    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        if (newAttr == null) {
            return null;
        }
        if (!(newAttr instanceof DOMAttrImpl)) {
            throw new DOMException(4, "newAttr not instanceof DOMAttrImpl");
        }
        DOMAttrImpl newatt = (DOMAttrImpl)newAttr;
        String name = newatt.avAdaptee.attribute;
        Attr result = null;
        AttVal att = this.adaptee.attributes;
        while (att != null && !att.attribute.equals(name)) {
            att = att.next;
        }
        if (att != null) {
            result = att.getAdapter();
            att.adapter = newAttr;
        } else if (this.adaptee.attributes == null) {
            this.adaptee.attributes = newatt.avAdaptee;
        } else {
            newatt.avAdaptee.next = this.adaptee.attributes;
            this.adaptee.attributes = newatt.avAdaptee;
        }
        return result;
    }

    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        if (oldAttr == null) {
            return null;
        }
        Attr result = null;
        AttVal att = this.adaptee.attributes;
        AttVal pre = null;
        while (att != null && att.getAdapter() != oldAttr) {
            pre = att;
            att = att.next;
        }
        if (att != null) {
            if (pre == null) {
                this.adaptee.attributes = att.next;
            } else {
                pre.next = att.next;
            }
        } else {
            throw new DOMException(8, "oldAttr not found");
        }
        result = oldAttr;
        return result;
    }

    public NodeList getElementsByTagName(String name) {
        return new DOMNodeListByTagNameImpl(this.adaptee, name);
    }

    public void normalize() {
    }

    public String getAttributeNS(String namespaceURI, String localName) {
        throw new DOMException(9, "DOM method not supported");
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        throw new DOMException(9, "DOM method not supported");
    }

    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        throw new DOMException(9, "DOM method not supported");
    }

    public boolean hasAttribute(String name) {
        return false;
    }

    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return false;
    }

    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    public void setIdAttribute(String name, boolean isId) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }
}

