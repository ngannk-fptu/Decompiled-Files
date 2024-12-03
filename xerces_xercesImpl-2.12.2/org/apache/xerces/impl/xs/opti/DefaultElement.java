/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import org.apache.xerces.impl.xs.opti.NodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

public class DefaultElement
extends NodeImpl
implements Element {
    public DefaultElement() {
    }

    public DefaultElement(String string, String string2, String string3, String string4, short s) {
        super(string, string2, string3, string4, s);
    }

    @Override
    public String getTagName() {
        return null;
    }

    @Override
    public String getAttribute(String string) {
        return null;
    }

    @Override
    public Attr getAttributeNode(String string) {
        return null;
    }

    @Override
    public NodeList getElementsByTagName(String string) {
        return null;
    }

    @Override
    public String getAttributeNS(String string, String string2) {
        return null;
    }

    @Override
    public Attr getAttributeNodeNS(String string, String string2) {
        return null;
    }

    @Override
    public NodeList getElementsByTagNameNS(String string, String string2) {
        return null;
    }

    @Override
    public boolean hasAttribute(String string) {
        return false;
    }

    @Override
    public boolean hasAttributeNS(String string, String string2) {
        return false;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    @Override
    public void setAttribute(String string, String string2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void removeAttribute(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Attr removeAttributeNode(Attr attr) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Attr setAttributeNode(Attr attr) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void setAttributeNS(String string, String string2, String string3) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void removeAttributeNS(String string, String string2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Attr setAttributeNodeNS(Attr attr) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void setIdAttributeNode(Attr attr, boolean bl) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void setIdAttribute(String string, boolean bl) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void setIdAttributeNS(String string, String string2, boolean bl) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }
}

