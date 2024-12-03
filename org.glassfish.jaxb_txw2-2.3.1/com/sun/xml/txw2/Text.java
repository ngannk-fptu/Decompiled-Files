/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.Content;
import com.sun.xml.txw2.Document;
import com.sun.xml.txw2.NamespaceResolver;

abstract class Text
extends Content {
    protected final StringBuilder buffer = new StringBuilder();

    protected Text(Document document, NamespaceResolver nsResolver, Object obj) {
        document.writeValue(obj, nsResolver, this.buffer);
    }

    @Override
    boolean concludesPendingStartTag() {
        return false;
    }
}

