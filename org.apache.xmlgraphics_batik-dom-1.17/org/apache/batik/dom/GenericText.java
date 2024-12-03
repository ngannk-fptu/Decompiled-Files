/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractText;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class GenericText
extends AbstractText {
    protected boolean readonly;

    protected GenericText() {
    }

    public GenericText(String value, AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setNodeValue(value);
    }

    @Override
    public String getNodeName() {
        return "#text";
    }

    @Override
    public short getNodeType() {
        return 3;
    }

    @Override
    public boolean isReadonly() {
        return this.readonly;
    }

    @Override
    public void setReadonly(boolean v) {
        this.readonly = v;
    }

    @Override
    protected Text createTextNode(String text) {
        return this.getOwnerDocument().createTextNode(text);
    }

    @Override
    protected Node newNode() {
        return new GenericText();
    }
}

