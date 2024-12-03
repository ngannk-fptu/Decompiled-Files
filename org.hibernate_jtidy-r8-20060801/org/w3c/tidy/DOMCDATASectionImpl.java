/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.CDATASection;
import org.w3c.tidy.DOMTextImpl;
import org.w3c.tidy.Node;

public class DOMCDATASectionImpl
extends DOMTextImpl
implements CDATASection {
    protected DOMCDATASectionImpl(Node adaptee) {
        super(adaptee);
    }

    public String getNodeName() {
        return "#cdata-section";
    }

    public short getNodeType() {
        return 4;
    }
}

