/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.ContentVisitor;
import com.sun.xml.txw2.Document;

abstract class Content {
    private Content next;

    Content() {
    }

    final Content getNext() {
        return this.next;
    }

    final void setNext(Document doc, Content next) {
        assert (next != null);
        assert (this.next == null) : "next of " + this + " is already set to " + this.next;
        this.next = next;
        doc.run();
    }

    boolean isReadyToCommit() {
        return true;
    }

    abstract boolean concludesPendingStartTag();

    abstract void accept(ContentVisitor var1);

    public void written() {
    }
}

