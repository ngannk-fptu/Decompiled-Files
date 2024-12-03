/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.TextImpl;
import org.w3c.dom.CDATASection;

public class CDATASectionImpl
extends TextImpl
implements CDATASection {
    static final long serialVersionUID = 2372071297878177780L;

    public CDATASectionImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl, string);
    }

    @Override
    public short getNodeType() {
        return 4;
    }

    @Override
    public String getNodeName() {
        return "#cdata-section";
    }
}

