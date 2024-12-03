/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.impl.CircularList;
import com.mchange.util.impl.CircularListRecord;
import java.util.Enumeration;
import java.util.NoSuchElementException;

class CircularListEnumeration
implements Enumeration {
    boolean forward;
    boolean terminated;
    boolean done;
    CircularListRecord startRecord;
    CircularListRecord lastRecord;

    CircularListEnumeration(CircularList circularList, boolean bl, boolean bl2) {
        if (circularList.firstRecord == null) {
            this.done = true;
        } else {
            this.done = false;
            this.forward = bl;
            this.terminated = bl2;
            this.startRecord = bl ? circularList.firstRecord : circularList.firstRecord.prev;
            this.lastRecord = bl ? this.startRecord.prev : this.startRecord;
        }
    }

    @Override
    public boolean hasMoreElements() {
        return !this.done;
    }

    public Object nextElement() {
        if (this.done) {
            throw new NoSuchElementException();
        }
        CircularListRecord circularListRecord = this.lastRecord = this.forward ? this.lastRecord.next : this.lastRecord.prev;
        if (this.terminated && this.lastRecord == (this.forward ? this.startRecord.prev : this.startRecord)) {
            this.done = true;
        }
        return this.lastRecord.object;
    }
}

