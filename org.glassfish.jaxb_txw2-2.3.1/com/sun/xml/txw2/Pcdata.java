/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.ContentVisitor;
import com.sun.xml.txw2.Document;
import com.sun.xml.txw2.NamespaceResolver;
import com.sun.xml.txw2.Text;

final class Pcdata
extends Text {
    Pcdata(Document document, NamespaceResolver nsResolver, Object obj) {
        super(document, nsResolver, obj);
    }

    @Override
    void accept(ContentVisitor visitor) {
        visitor.onPcdata(this.buffer);
    }
}

