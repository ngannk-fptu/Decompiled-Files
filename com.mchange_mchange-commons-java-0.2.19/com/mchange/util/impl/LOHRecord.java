/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.impl.LOHRecElem;
import com.mchange.util.impl.LongObjectHash;

class LOHRecord
extends LOHRecElem {
    LongObjectHash parent;
    int size = 0;

    LOHRecord(long l) {
        super(l, null, null);
    }

    LOHRecElem findLong(long l) {
        LOHRecElem lOHRecElem = this;
        while (lOHRecElem.next != null) {
            if (lOHRecElem.next.num == l) {
                return lOHRecElem;
            }
            lOHRecElem = lOHRecElem.next;
        }
        return null;
    }

    boolean add(long l, Object object, boolean bl) {
        LOHRecElem lOHRecElem = this.findLong(l);
        if (lOHRecElem != null) {
            if (bl) {
                lOHRecElem.next = new LOHRecElem(l, object, lOHRecElem.next.next);
            }
            return true;
        }
        this.next = new LOHRecElem(l, object, this.next);
        ++this.size;
        return false;
    }

    Object remove(long l) {
        LOHRecElem lOHRecElem = this.findLong(l);
        if (lOHRecElem == null) {
            return null;
        }
        Object object = lOHRecElem.next.obj;
        lOHRecElem.next = lOHRecElem.next.next;
        --this.size;
        if (this.size == 0) {
            this.parent.records[(int)this.num] = null;
        }
        return object;
    }

    Object get(long l) {
        LOHRecElem lOHRecElem = this.findLong(l);
        if (lOHRecElem != null) {
            return lOHRecElem.next.obj;
        }
        return null;
    }

    LOHRecord split(int n) {
        LOHRecord lOHRecord = null;
        LOHRecElem lOHRecElem = null;
        LOHRecElem lOHRecElem2 = this;
        while (lOHRecElem2.next != null) {
            if (lOHRecElem2.next.num % (long)n != this.num) {
                if (lOHRecord == null) {
                    lOHRecElem = lOHRecord = new LOHRecord(this.num * 2L);
                }
                lOHRecElem.next = lOHRecElem2.next;
                lOHRecElem2.next = lOHRecElem2.next.next;
                lOHRecElem = lOHRecElem.next;
                lOHRecElem.next = null;
            }
            lOHRecElem2 = lOHRecElem2.next;
        }
        return lOHRecord;
    }
}

