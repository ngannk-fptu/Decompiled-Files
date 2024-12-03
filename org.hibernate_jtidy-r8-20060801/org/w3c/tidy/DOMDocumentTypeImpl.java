/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.tidy.DOMNodeImpl;
import org.w3c.tidy.Node;
import org.w3c.tidy.TidyUtils;

public class DOMDocumentTypeImpl
extends DOMNodeImpl
implements DocumentType {
    protected DOMDocumentTypeImpl(Node adaptee) {
        super(adaptee);
    }

    public short getNodeType() {
        return 10;
    }

    public String getNodeName() {
        return this.getName();
    }

    public String getName() {
        String value = null;
        if (this.adaptee.type == 1 && this.adaptee.textarray != null && this.adaptee.start < this.adaptee.end) {
            value = TidyUtils.getString(this.adaptee.textarray, this.adaptee.start, this.adaptee.end - this.adaptee.start);
        }
        return value;
    }

    public NamedNodeMap getEntities() {
        return null;
    }

    public NamedNodeMap getNotations() {
        return null;
    }

    public String getPublicId() {
        return null;
    }

    public String getSystemId() {
        return null;
    }

    public String getInternalSubset() {
        return null;
    }
}

