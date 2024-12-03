/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import org.apache.xerces.impl.xs.opti.NodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

public class AttrImpl
extends NodeImpl
implements Attr {
    Element element;
    String value;

    public AttrImpl() {
        this.nodeType = (short)2;
    }

    public AttrImpl(Element element, String string, String string2, String string3, String string4, String string5) {
        super(string, string2, string3, string4, (short)2);
        this.element = element;
        this.value = string5;
    }

    @Override
    public String getName() {
        return this.rawname;
    }

    @Override
    public boolean getSpecified() {
        return true;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getNodeValue() {
        return this.getValue();
    }

    @Override
    public Element getOwnerElement() {
        return this.element;
    }

    @Override
    public Document getOwnerDocument() {
        return this.element.getOwnerDocument();
    }

    @Override
    public void setValue(String string) throws DOMException {
        this.value = string;
    }

    @Override
    public boolean isId() {
        return false;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    @Override
    public String toString() {
        return this.getName() + "=" + "\"" + this.getValue() + "\"";
    }
}

