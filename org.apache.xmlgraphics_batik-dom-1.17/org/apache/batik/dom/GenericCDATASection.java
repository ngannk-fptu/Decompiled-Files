/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractText;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class GenericCDATASection
extends AbstractText
implements CDATASection {
    protected boolean readonly;

    protected GenericCDATASection() {
    }

    public GenericCDATASection(String value, AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setNodeValue(value);
    }

    @Override
    public String getNodeName() {
        return "#cdata-section";
    }

    @Override
    public short getNodeType() {
        return 4;
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
        return this.getOwnerDocument().createCDATASection(text);
    }

    @Override
    protected Node newNode() {
        return new GenericCDATASection();
    }
}

