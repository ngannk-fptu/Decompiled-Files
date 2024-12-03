/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.impl.IOHRecElem;
import com.mchange.util.impl.IntObjectHash;

class IOHRecord
extends IOHRecElem {
    IntObjectHash parent;
    int size = 0;

    IOHRecord(int n) {
        super(n, null, null);
    }

    IOHRecElem findInt(int n) {
        IOHRecElem iOHRecElem = this;
        while (iOHRecElem.next != null) {
            if (iOHRecElem.next.num == n) {
                return iOHRecElem;
            }
            iOHRecElem = iOHRecElem.next;
        }
        return null;
    }

    boolean add(int n, Object object, boolean bl) {
        IOHRecElem iOHRecElem = this.findInt(n);
        if (iOHRecElem != null) {
            if (bl) {
                iOHRecElem.next = new IOHRecElem(n, object, iOHRecElem.next.next);
            }
            return true;
        }
        this.next = new IOHRecElem(n, object, this.next);
        ++this.size;
        return false;
    }

    Object remove(int n) {
        IOHRecElem iOHRecElem = this.findInt(n);
        if (iOHRecElem == null) {
            return null;
        }
        Object object = iOHRecElem.next.obj;
        iOHRecElem.next = iOHRecElem.next.next;
        --this.size;
        if (this.size == 0) {
            this.parent.records[this.num] = null;
        }
        return object;
    }

    Object get(int n) {
        IOHRecElem iOHRecElem = this.findInt(n);
        if (iOHRecElem != null) {
            return iOHRecElem.next.obj;
        }
        return null;
    }

    IOHRecord split(int n) {
        IOHRecord iOHRecord = null;
        IOHRecElem iOHRecElem = null;
        IOHRecElem iOHRecElem2 = this;
        while (iOHRecElem2.next != null) {
            if (Math.abs(iOHRecElem2.next.num % n) != this.num) {
                if (iOHRecord == null) {
                    iOHRecElem = iOHRecord = new IOHRecord(this.num * 2);
                }
                iOHRecElem.next = iOHRecElem2.next;
                iOHRecElem2.next = iOHRecElem2.next.next;
                iOHRecElem = iOHRecElem.next;
                iOHRecElem.next = null;
            }
            iOHRecElem2 = iOHRecElem2.next;
        }
        return iOHRecord;
    }
}

