/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.Content;
import com.sun.xml.txw2.ContentVisitor;

final class EndTag
extends Content {
    EndTag() {
    }

    @Override
    boolean concludesPendingStartTag() {
        return true;
    }

    @Override
    void accept(ContentVisitor visitor) {
        visitor.onEndTag();
    }
}

