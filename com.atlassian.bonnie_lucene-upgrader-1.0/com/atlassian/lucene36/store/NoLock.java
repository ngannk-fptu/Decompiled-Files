/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.Lock;
import java.io.IOException;

class NoLock
extends Lock {
    NoLock() {
    }

    public boolean obtain() throws IOException {
        return true;
    }

    public void release() {
    }

    public boolean isLocked() {
        return false;
    }

    public String toString() {
        return "NoLock";
    }
}

