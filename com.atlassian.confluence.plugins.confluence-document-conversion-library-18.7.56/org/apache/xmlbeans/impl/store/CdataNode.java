/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.TextNode;
import org.w3c.dom.CDATASection;

class CdataNode
extends TextNode
implements CDATASection {
    CdataNode(Locale l) {
        super(l);
    }

    @Override
    public int nodeType() {
        return 4;
    }

    @Override
    public String name() {
        return "#cdata-section";
    }
}

