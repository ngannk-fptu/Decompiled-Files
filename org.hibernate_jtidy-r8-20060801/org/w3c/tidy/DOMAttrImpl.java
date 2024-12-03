/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.tidy.AttVal;
import org.w3c.tidy.DOMNodeImpl;
import org.w3c.tidy.DOMNodeListImpl;

public class DOMAttrImpl
extends DOMNodeImpl
implements Attr,
Cloneable {
    protected AttVal avAdaptee;

    protected DOMAttrImpl(AttVal adaptee) {
        super(null);
        this.avAdaptee = adaptee;
    }

    public String getNodeValue() throws DOMException {
        return this.getValue();
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        this.setValue(nodeValue);
    }

    public String getNodeName() {
        return this.getName();
    }

    public short getNodeType() {
        return 2;
    }

    public String getName() {
        return this.avAdaptee.attribute;
    }

    public boolean getSpecified() {
        return this.avAdaptee.value != null;
    }

    public String getValue() {
        return this.avAdaptee.value == null ? this.avAdaptee.attribute : this.avAdaptee.value;
    }

    public void setValue(String value) {
        this.avAdaptee.value = value;
    }

    public Node getParentNode() {
        return null;
    }

    public NodeList getChildNodes() {
        return new DOMNodeListImpl(null);
    }

    public Node getFirstChild() {
        return null;
    }

    public Node getLastChild() {
        return null;
    }

    public Node getPreviousSibling() {
        return null;
    }

    public Node getNextSibling() {
        return null;
    }

    public NamedNodeMap getAttributes() {
        return null;
    }

    public Document getOwnerDocument() {
        return null;
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw new DOMException(7, "Not supported");
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new DOMException(7, "Not supported");
    }

    public Node removeChild(Node oldChild) throws DOMException {
        throw new DOMException(7, "Not supported");
    }

    public Node appendChild(Node newChild) throws DOMException {
        throw new DOMException(7, "Not supported");
    }

    public boolean hasChildNodes() {
        return false;
    }

    public Node cloneNode(boolean deep) {
        return (Node)this.clone();
    }

    public Element getOwnerElement() {
        return null;
    }

    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    public boolean isId() {
        return "id".equals(this.avAdaptee.getAttribute());
    }

    protected Object clone() {
        DOMAttrImpl clone;
        try {
            clone = (DOMAttrImpl)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported");
        }
        clone.avAdaptee = (AttVal)this.avAdaptee.clone();
        return clone;
    }
}

