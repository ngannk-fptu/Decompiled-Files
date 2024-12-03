/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.Content;
import com.sun.xml.txw2.ContentVisitor;

final class StartDocument
extends Content {
    StartDocument() {
    }

    @Override
    boolean concludesPendingStartTag() {
        return true;
    }

    @Override
    void accept(ContentVisitor visitor) {
        visitor.onStartDocument();
    }
}

