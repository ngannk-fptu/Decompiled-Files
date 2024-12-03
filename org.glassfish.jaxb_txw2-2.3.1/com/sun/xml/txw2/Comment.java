/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.Content;
import com.sun.xml.txw2.ContentVisitor;
import com.sun.xml.txw2.Document;
import com.sun.xml.txw2.NamespaceResolver;

final class Comment
extends Content {
    private final StringBuilder buffer = new StringBuilder();

    public Comment(Document document, NamespaceResolver nsResolver, Object obj) {
        document.writeValue(obj, nsResolver, this.buffer);
    }

    @Override
    boolean concludesPendingStartTag() {
        return false;
    }

    @Override
    void accept(ContentVisitor visitor) {
        visitor.onComment(this.buffer);
    }
}

