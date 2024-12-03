/*
 * Decompiled with CFR 0.152.
 */
package antlr.collections.impl;

import antlr.collections.impl.LLCell;
import antlr.collections.impl.LList;
import java.util.Enumeration;
import java.util.NoSuchElementException;

final class LLEnumeration
implements Enumeration {
    LLCell cursor;
    LList list;

    public LLEnumeration(LList lList) {
        this.list = lList;
        this.cursor = this.list.head;
    }

    public boolean hasMoreElements() {
        return this.cursor != null;
    }

    public Object nextElement() {
        if (!this.hasMoreElements()) {
            throw new NoSuchElementException();
        }
        LLCell lLCell = this.cursor;
        this.cursor = this.cursor.next;
        return lLCell.data;
    }
}

