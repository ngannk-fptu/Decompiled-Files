/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import org.apache.lucene.store.Lock;

class NoLock
extends Lock {
    NoLock() {
    }

    @Override
    public boolean obtain() throws IOException {
        return true;
    }

    @Override
    public void release() {
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    public String toString() {
        return "NoLock";
    }
}

