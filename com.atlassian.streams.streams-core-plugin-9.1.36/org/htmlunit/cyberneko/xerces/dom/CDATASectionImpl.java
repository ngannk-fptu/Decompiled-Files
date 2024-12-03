/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.TextImpl;
import org.w3c.dom.CDATASection;

public class CDATASectionImpl
extends TextImpl
implements CDATASection {
    public CDATASectionImpl(CoreDocumentImpl ownerDoc, String data) {
        super(ownerDoc, data);
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

